/**
 * Сортировка кучей целочисленного массива.
 * Алгоритм: построение кучи, затем поочерёдная экстракция максимума в хвост массива.
 */
public class Heapsort {
    /**
     * Сортирует массив по не убыванию с помощью алгоритма heapsort.
     *
     * @param arr массив для сортировки (меняется "на месте")
     */
    public static void sort(int[] arr) {
        int n = arr.length;

        //Строительство кучи
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }

        //Экстракция элементов
        for (int i = n - 1; i >= 0; i--) {
            //Сдвинуть корень в конец
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;


            heapify(arr, i, 0);
        }
    }


    /**
     * «Кучевание» поддерева: делает поддерево с корнем в {@code i} корректной max-кучей,
     * предполагая, что его дочерние поддеревья уже удовлетворяют свойствам кучи.
     *
     * @param arr массив-куча
     * @param n   эффективный размер кучи (элементы с индексами [0, n) принадлежат куче)
     * @param i   индекс корня поддерева
     */
    static void heapify(int[] arr, int n, int i) {
        int largest = i; // Самое большое - корень
        int l = 2 * i + 1; // Левый элемент
        int r = 2 * i + 2; // Правый элемент

        // Если левый больше корня
        if (l < n && arr[l] > arr[largest]) {
            largest = l;
        }

        // Если правый больше большего
        if (r < n && arr[r] > arr[largest]) {
            largest = r;
        }

        // Если самый большой - не корень
        if (largest != i) {
            int swap = arr[i];
            arr[i] = arr[largest];
            arr[largest] = swap;

            // Кучевание поддерева
            heapify(arr, n, largest);
        }
    }

    /**
     * Печать массива: элементы через пробел и перевод строки в конце.
     *
     * @param arr массив для печати
     */
    static void printArray(int[] arr) {
        for (int j : arr) {
            System.out.print(j + " ");
        }
        System.out.println();
    }

    /**
     * Пример использования: сортировка тестового массива и вывод результата.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        int[] arr = {12, 11, 13, 5, 6, 7}; //тест

        Heapsort.sort(arr);

        System.out.println("Вывод:");
        printArray(arr);
    }
}
