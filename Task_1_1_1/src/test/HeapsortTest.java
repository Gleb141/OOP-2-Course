import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HeapsortTest {
    @Test
    void emptyArray() {
        int[] a = {};
        new main.java.Heapsort().sort(a);
        assertArrayEquals(new int[]{}, a);
    }

}