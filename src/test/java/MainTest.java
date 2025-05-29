import org.example.BytesConverter;
import org.example.MessageEncoder;
import org.example.Product;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MainTest {

    @Test
    void givenMessage_shouldEncodeToHexString(){
        Product message = new Product("some product", 25);
        String expected = "130100000000000000010000002A4DAC00000003000000047B226E616D65223A22736F6D652070726F64756374222C227072696365223A32357D819E";
        byte[] out = MessageEncoder.encode(message);
        assertEquals(expected, BytesConverter.bytesToHex(out));
    }

    @Test
    void givenBytesArray_shouldDecodeFromHexString(){
        Product expected = new Product("some product", 25);
        byte[] in = new byte[]{19, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 42, 77, -84, 0, 0, 0, 3, 0, 0, 0, 4, 123, 34, 110, 97, 109, 101, 34, 58, 34, 115, 111, 109, 101, 32, 112, 114, 111, 100, 117, 99, 116, 34, 44, 34, 112, 114, 105, 99, 101, 34, 58, 50, 53, 125, -127, -98};
        Product out = MessageEncoder.decode(in);
        assertEquals(expected, out);
    }

    @Test
    void givenInvalidMessage_throwsException(){
        byte[] in = new byte[]{15, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 42, 77, -84, 0, 0, 0, 3, 0, 0, 0, 4, 123, 34, 110, 97, 109, 101, 34, 58, 34, 115, 111, 109, 101, 32, 112, 114, 111, 100, 117, 99, 116, 34, 44, 34, 112, 114, 105, 99, 101, 34, 58, 50, 53, 125, -127, -98};
        assertThrows(IllegalArgumentException.class ,() -> MessageEncoder.decode(in));
    }
}