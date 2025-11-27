/**
 * Auto-generated tests and support code.
 */
public class HashTableEntry<K, V> {
    K key;
    V value;
    HashTableEntry<K, V> next;
    /**
     * Creates a new entry.
     */

    public HashTableEntry(K key, V value) {
        this.key = key;
        this.value = value;
        this.next = null;
    }
    /**
     * Auto-generated.
     */

    @Override
    public String toString() {
        return key + "=" + value;
    }
}