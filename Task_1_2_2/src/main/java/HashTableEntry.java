public class HashTableEntry<K, V> {
    K key;
    V value;
    HashTableEntry<K, V> next;

    public HashTableEntry(K key, V value) {
        this.key = key;
        this.value = value;
        this.next = null;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}

