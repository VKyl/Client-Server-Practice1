package org.example.db;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class MySqlDb extends DataBase{
    public MySqlDb(String host, String db, String username, String password, String tableName, boolean isTest) {
        super("com.mysql.cj.jdbc.Driver", "jdbc:mysql://" + host + "/" + db + "?createDatabaseIfNotExist=true", username, password, tableName, isTest);
    }

    public static void main(String[] args) {
        final DataBase db = connect("sample", true);
        db.insert(new ProductRow(0, "apples", 23));
        db.listAll().forEach(System.out::println);
    }

    public static DataBase connect(String tableName, boolean isTest) {
        Dotenv dotenv = loadEnv();
        String host = dotenv.get("DB_HOST");
        String dbName = dotenv.get("DB_NAME");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASS");

        return new MySqlDb(host, dbName, user, password, tableName, isTest);
    }

    private static Dotenv loadEnv() {
        return Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .load();
    }
}
