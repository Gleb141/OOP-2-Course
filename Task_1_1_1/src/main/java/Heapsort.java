
public class Heapsort {
    public void sort(int arr[])
    {
        int n = arr.length;

        //Строительство кучи
        for (int i = n / 2 - 1; i >= 0; i--)
            heapify(arr, n, i);

        //Экстракция элементов
        for (int i = n - 1; i >= 0; i--) {
            //Сдвинуть корень в конец
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;


            heapify(arr, i, 0);
        }
    }

    // Кучевание
    void heapify(int arr[], int n, int i)
    {
        int largest = i; // Самое большое - корень
        int l = 2 * i + 1; // Левый элемент
        int r = 2 * i + 2; // Правый элемент

        // Если левый больше корня
        if (l < n && arr[l] > arr[largest])
            largest = l;

        // Если правый больше большего
        if (r < n && arr[r] > arr[largest])
            largest = r;

        // Если самый большой - не корень
        if (largest != i) {
            int swap = arr[i];
            arr[i] = arr[largest];
            arr[largest] = swap;

            // Кучевание поддерева
            heapify(arr, n, largest);
        }
    }

    /* Печать массива */
    static void printArray(int arr[])
    {
        int n = arr.length;
        for (int i = 0; i < n; ++i)
            System.out.print(arr[i] + " ");
        System.out.println();
    }

    // Главный код
    public static void main(String args[])
    {
        int arr[] = { 12, 11, 13, 5, 6, 7 }; //тест
        int n = arr.length;

        Heapsort ob = new Heapsort();
        ob.sort(arr);

        System.out.println("Вывод:");
        printArray(arr);
    }
}
