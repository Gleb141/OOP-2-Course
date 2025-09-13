import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юнит-тесты для {@link Heapsort}.
 */
public class HeapsortTest {
    @Test
    void emptyArray() {
        int[] a = {};
        Heapsort.sort(a);
        assertArrayEquals(new int[]{}, a);
    }

    @Test
    void singleElement() {
        int[] a = {42};
        Heapsort.sort(a);
        assertArrayEquals(new int[]{42}, a);
    }

    @Test
    void alreadySorted() {
        int[] a = {1, 2, 3, 4, 5};
        int[] expected = a.clone();
        Heapsort.sort(a);
        assertArrayEquals(expected, a);
    }

    @Test
    void reverseSorted() {
        int[] a = {9, 7, 5, 3, 1};
        int[] expected = a.clone();
        Arrays.sort(expected);
        Heapsort.sort(a);
        assertArrayEquals(expected, a);
    }

    @Test
    void withDuplicates() {
        int[] a = {3, 3, 1, 2, 2, 3, 1};
        int[] expected = a.clone();
        Arrays.sort(expected);
        Heapsort.sort(a);
        assertArrayEquals(expected, a);
    }

    @Test
    void extremesMinMax() {
        int[] a = {Integer.MAX_VALUE, 0, -1, Integer.MIN_VALUE, 5};
        int[] expected = a.clone();
        Arrays.sort(expected);
        Heapsort.sort(a);
        assertArrayEquals(expected, a);
    }

    @Test
    void heapifyProducesMaxAtRoot() {
        int[] a = {1, 3, 2};
        Heapsort.heapify(a, a.length, 0);
        assertTrue(a[0] >= a[1] && a[0] >= a[2]);
    }

    @Test
    void heapifyDoesNothingWhenRootAlreadyLargest() {
        int[] a = {9, 4, 3};
        int[] copy = a.clone();
        Heapsort.heapify(a, a.length, 0);
        assertArrayEquals(copy, a);
    }

    @Test
    void mainTest() {
        Heapsort.main(new String[0]);
    }

    @Test
    void heapifyRightChildDominates() {
        int[] a = {1, 2, 4};
        Heapsort.heapify(a, a.length, 0);
        assertEquals(4, a[0]);
    }

    @Test
    void sortTwoElementsCoversEdgeIndexZero() {
        int[] a = {2, 1};
        Heapsort.sort(a);
        assertArrayEquals(new int[]{1, 2}, a);
    }

    @Test
    void printArrayCoversLoopAndNewline() {
        Heapsort.printArray(new int[]{});
        Heapsort.printArray(new int[]{1, 2, 3});
    }

    @Test
    void heapifyLeftChildDeepRecursion() {
        int[] a = {1, 9, 2, 10, 0, 0, 0};
        Heapsort.heapify(a, a.length, 0);
        assertArrayEquals(new int[]{9, 10, 2, 1, 0, 0, 0}, a);
        assertEquals(9, a[0]);
    }

    @Test
    void heapifyRightChildDeepRecursion() {
        int[] a = {1, 2, 5, 0, 0, 9, 8};
        Heapsort.heapify(a, a.length, 0);
        assertArrayEquals(new int[]{5, 2, 9, 0, 0, 1, 8}, a);
        assertEquals(5, a[0]);
    }
}