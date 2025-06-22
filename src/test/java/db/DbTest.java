package db;

import org.example.db.DataBase;
import org.example.db.MySqlDb;
import org.example.db.ProductRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;

public class DbTest {
    private DataBase db;
    @BeforeEach
    public void setUp() {
        db = MySqlDb.connect("sample", true);
    }

    @Test
    public void TestInsertProductRow() {
        ProductRow productRow = new ProductRow(1, "apple", 34.23);
        System.out.println("Inserting row " + productRow);
        db.insert(productRow);
        final ProductRow insertedRow = db.listAll().getFirst();
        System.out.println("Inserted row " + insertedRow);
        Assertions.assertEquals(productRow, insertedRow);
    }

    @Test
    public void TestInsertFewProducts() {
        ProductRow[] products = {
                new ProductRow(1, "apple", 34.23),
                new ProductRow(2, "orange", 0.23)
        };
        insertProducts(products);
        for (int i = 0; i < products.length; i++) {
            final ProductRow insertedRow = db.listAll().get(i);
            System.out.println("Inserted row " + insertedRow);
            Assertions.assertEquals(products[i], insertedRow);
        }
        db.listAll().forEach(System.out::println);
    }

    @Test
    void insertAndGetById() {
        ProductRow productRow = new ProductRow(1, "apple", 34.23);
        System.out.println("Inserting row " + productRow);
        db.insert(productRow);
        final ProductRow insertedRow = db.selectById(1);
        System.out.println("Inserted row " + insertedRow);
        Assertions.assertEquals(productRow, insertedRow);
    }

    @Test
    void updateProductRow() {
        ProductRow productRow = new ProductRow(1, "apple", 34.23);
        System.out.println("Inserting row " + productRow);
        db.insert(productRow);
        final ProductRow newProduct = new ProductRow(1, "golden apple", 9999.99);
        System.out.println("Updating row " + newProduct);
        db.update(newProduct);
        final ProductRow updatedProduct = db.selectById(1);
        System.out.println("Updated row " + updatedProduct);
        Assertions.assertEquals(newProduct, updatedProduct);
    }

    @Test
    void deleteProductRow() {
        ProductRow productRow = new ProductRow(1, "apple", 34.23);
        System.out.println("Inserting row " + productRow);
        db.insert(productRow);
        final ProductRow insertedRow = db.selectById(1);
        System.out.println("Inserted row " + insertedRow);
        final boolean isDeleted = db.delete(1);
        Assertions.assertTrue(isDeleted);
    }

    @Test
    void filteringProductRowsByName() {
        ProductRow[] products = {
                new ProductRow(0, "orange", 0.56),
                new ProductRow(0, "apple", 34.23),
                new ProductRow(0, "orange", 0.23)
        };
        insertProducts(products);
        final HashMap<String, Object> filters = new HashMap<>();
        filters.put("name", "orange");

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        db.listAll(filters).forEach(System.out::println);
        Assertions.assertEquals("ProductRow[id=1, name=orange, price=0.56]\n" +
                "ProductRow[id=3, name=orange, price=0.23]", outContent.toString().trim());
    }

    @Test
    void paginationProductRows() {
        ProductRow[] products = {
            new ProductRow(1, "apple", 34.23),
            new ProductRow(2, "orange", 0.23),
            new ProductRow(3, "grape", 34.23),
            new ProductRow(4, "pear", 34.23),
            new ProductRow(5, "pineapple", 34.23),
        };
        insertProducts(products);
        final List<ProductRow> results = db.listAll(3, 5);
        for (int i = 0; i < 3; ++i) {
            Assertions.assertEquals(products[3 + i - 1], results.get(i));
        }
    }

    private void insertProducts(ProductRow[] products) {
        for (ProductRow productRow : products) {
            System.out.println("Inserting row " + productRow);
            db.insert(productRow);
        }
    }
}
