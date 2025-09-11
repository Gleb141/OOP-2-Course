import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HeapsortTest {
    @Test
    void emptyArray() {
        int[] a = {};
        new Heapsort().sort(a);
        assertArrayEquals(new int[]{}, a);
    }

    @Test
    void singleElement() {
        int[] a = {42};
        new Heapsort().sort(a);
        assertArrayEquals(new int[]{42}, a);
    }

    @Test
    void alreadySorted() {
        int[] a = {1, 2, 3, 4, 5};
        int[] expected = a.clone();
        new Heapsort().sort(a);
        assertArrayEquals(expected, a);
    }

    @Test
    void reverseSorted() {
        int[] a = {9, 7, 5, 3, 1};
        int[] expected = a.clone();
        Arrays.sort(expected);
        new Heapsort().sort(a);
        assertArrayEquals(expected, a);
    }

    @Test
    void withDuplicates() {
        int[] a = {3, 3, 1, 2, 2, 3, 1};
        int[] expected = a.clone();
        Arrays.sort(expected);
        new Heapsort().sort(a);
        assertArrayEquals(expected, a);
    }

    @Test
    void extremesMinMax() {
        int[] a = {Integer.MAX_VALUE, 0, -1, Integer.MIN_VALUE, 5};
        int[] expected = a.clone();
        Arrays.sort(expected);
        new Heapsort().sort(a);
        assertArrayEquals(expected, a);
    }

    @Test
    void heapifyProducesMaxAtRoot() {
        int[] a = {1, 3, 2};
        new Heapsort().heapify(a, a.length, 0);
        assertTrue(a[0] >= a[1] && a[0] >= a[2]);
    }
}