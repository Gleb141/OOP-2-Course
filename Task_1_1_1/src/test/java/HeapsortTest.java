package java;

import main.java.Heapsort;
import static org.junit.jupiter.api.Assertions.*;

public class HeapsortTest {
    @Test
    void emptyArray() {
        int[] a = {};
        new Heapsort().sort(a);
        assertArrayEquels(new int[]{}, a);
    }

}