/**
 * Auto-generated tests and support code.
 */
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
/**
 * Auto-generated.
 */
    public String toString() {
        return key + "=" + value;
    }
}
