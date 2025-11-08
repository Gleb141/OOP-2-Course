public class Main {
    public static void main(String[] args) {
        HashTable<String, Number> hashTable = new HashTable<>();
        hashTable.put("one", 1);
        hashTable.update("one", 1.0);
        System.out.println(hashTable.get("one"));

        hashTable.put("two", 2);
        hashTable.update("two", 2.0);
        System.out.println(hashTable);

        hashTable.remove("two");
        System.out.println(hashTable.containsKey("two"));

        for (HashTableEntry<String, Number> e : hashTable) {
            System.out.println(e);
        }
    }
}
