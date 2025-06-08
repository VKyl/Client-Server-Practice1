import org.example.Main;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;


public class MainTest {
    @Test
    void TestNoSleep(){
        final Thread[] threads = Main.MessagePipeLine(0);
        for (final Thread thread : threads) assertFalse(thread.isAlive());
    }

    @Test
    void TestSleep(){
        final Thread[] threads = Main.MessagePipeLine(200);
        for (final Thread thread : threads) assertFalse(thread.isAlive());
    }
}
