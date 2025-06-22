package org.example.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class DataBase {
    private final Connection connection;
    private final String tableName;

    public DataBase(String driverClass, String connectionString, String username, String password, String tableName, boolean isTest) {
        this.tableName = tableName;
        System.out.println("Connecting to database: " + connectionString);

        try {
            Class.forName(driverClass);
            connection = DriverManager.getConnection(connectionString, username, password);
            if (isTest)
                dropTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can't find driver class", e);
        }

        createTable();
    }

    private void createTable() {
        String sql = String.format(
                "CREATE TABLE IF NOT EXISTS %s (" +
                        "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                        "name VARCHAR(1000), " +
                        "price DOUBLE);", tableName);

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ProductRow insert(ProductRow row) {
        String sql = String.format("INSERT INTO %s(name, price) VALUES (?, ?)", tableName);

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, row.name());
            statement.setDouble(2, row.price());
            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            rs.next();
            long newId = rs.getLong(1);

            return new ProductRow(newId, row.name(), row.price());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ProductRow selectById(long id) {
        String sql = String.format("SELECT * FROM %s WHERE id = ?", tableName);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            ResultSet res = statement.executeQuery();
            if (res.next()) {
                String name = res.getString("name");
                double price = res.getDouble("price");
                return new ProductRow(id, name, price);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error selecting product by id", e);
        }
    }

    public ProductRow update(ProductRow row) {
        String sql = String.format("UPDATE %s SET name = ?, price = ? WHERE id = ?", tableName);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, row.name());
            statement.setDouble(2, row.price());
            statement.setLong(3, row.id());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                return null;
            }

            return row;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating product by id", e);
        }
    }

    public boolean delete(long id) {
        String sql = String.format("DELETE FROM %s WHERE id = ?", tableName);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected != 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting product by id", e);
        }
    }

    public List<ProductRow> listAll() {
        String sql = String.format("SELECT * FROM %s", tableName);

        try (Statement statement = connection.createStatement()) {
            List<ProductRow> result = new ArrayList<>();
            ResultSet res = statement.executeQuery(sql);

            return getProductsFromQueryResult(res, result);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ProductRow> listAll(Map<String, Object> filters) {
        StringBuilder sql = buildSqlWithFilters(filters);
        List<Object> parameters = extractParameters(filters);

        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            setParameters(statement, parameters);
            return executeQuery(statement);
        } catch (SQLException e) {
            throw new RuntimeException("Error listing products", e);
        }
    }

    public List<ProductRow> listAll(long from, long to) {
        String sql = String.format("SELECT * FROM %s LIMIT ? OFFSET ?", tableName);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, to - from + 1);
            statement.setLong(2, from - 1);
            ResultSet res = statement.executeQuery();
            List<ProductRow> result = new ArrayList<>();
            return getProductsFromQueryResult(res, result);
        } catch (SQLException e) {
            throw new RuntimeException("Error listing products from " + from + " to " + to, e);
        }
    }

    private List<ProductRow> getProductsFromQueryResult(ResultSet res, List<ProductRow> result) throws SQLException {
        while (res.next()) {
            long id = res.getLong("id");
            String name = res.getString("name");
            double price = res.getDouble("price");

            result.add(new ProductRow(id, name, price));
        }
        return result;
    }

    private StringBuilder buildSqlWithFilters(Map<String, Object> filters) {
        StringBuilder sql = new StringBuilder(String.format("SELECT * FROM %s WHERE 1=1", tableName));
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            if (entry.getValue() != null) {
                sql.append(" AND ").append(entry.getKey()).append(" = ?");
            }
        }
        return sql;
    }

    private List<Object> extractParameters(Map<String, Object> filters) {
        List<Object> parameters = new ArrayList<>();
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            if (entry.getValue() != null) {
                parameters.add(entry.getValue());
            }
        }
        return parameters;
    }

    private void setParameters(PreparedStatement statement, List<Object> parameters) throws SQLException {
        for (int i = 0; i < parameters.size(); i++) {
            statement.setObject(i + 1, parameters.get(i));
        }
    }

    private List<ProductRow> executeQuery(PreparedStatement statement) throws SQLException {
        List<ProductRow> results = new ArrayList<>();
        try (ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                results.add(new ProductRow(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                ));
            }
        }
        return results;
    }


    public void dropTable() {
        String sql = String.format("DROP TABLE IF EXISTS %s", tableName);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            System.out.println("Table '" + tableName + "' dropped successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to drop table", e);
        }
    }
}
