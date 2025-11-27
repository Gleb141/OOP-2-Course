import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Simple separate-chaining hash table with fail-fast iterator.
 */
public class HashTable<K, V> implements Iterable<HashTableEntry<K, V>> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private HashTableEntry<K, V>[] table;
    private int size;
    private int capacity;
    private int modCount;

    /**
     * Constructs an empty hash table.
     */
    @SuppressWarnings("unchecked")
    public HashTable() {
        this.capacity = DEFAULT_CAPACITY;
        this.table = (HashTableEntry<K, V>[]) new HashTableEntry[capacity];
        this.size = 0;
        this.modCount = 0;
    }

    private int hash(K key) {
        if (key == null) {
            return 0;
        }
        int h = key.hashCode();
        int m = h == Integer.MIN_VALUE ? 0 : Math.abs(h);
        return m % capacity;
    }

    private boolean keysEqual(K a, K b) {
        return a == b || (a != null && a.equals(b));
    }

    /**
     * Associates the given key with the value.
     */
    public void put(K key, V value) {
        int index = hash(key);
        HashTableEntry<K, V> entry = table[index];
        while (entry != null) {
            if (keysEqual(entry.key, key)) {
                entry.value = value;
                modCount++; // treat as structural for fail-fast semantics
                return;
            }
            entry = entry.next;
        }
        HashTableEntry<K, V> newEntry = new HashTableEntry<>(key, value);
        newEntry.next = table[index];
        table[index] = newEntry;
        size++;
        modCount++;
        if ((float) size / capacity > LOAD_FACTOR) {
            resize();
        }
    }

    /**
     * Returns the value for the key or null if absent.
     */
    public V get(K key) {
        int index = hash(key);
        HashTableEntry<K, V> entry = table[index];
        while (entry != null) {
            if (keysEqual(entry.key, key)) {
                return entry.value;
            }
            entry = entry.next;
        }
        return null;
    }

    /**
     * Removes the entry for the given key if present.
     */
    public void remove(K key) {
        int index = hash(key);
        HashTableEntry<K, V> entry = table[index];
        HashTableEntry<K, V> prev = null;
        while (entry != null) {
            if (keysEqual(entry.key, key)) {
                if (prev == null) {
                    table[index] = entry.next;
                } else {
                    prev.next = entry.next;
                }
                size--;
                modCount++;
                return;
            }
            prev = entry;
            entry = entry.next;
        }
    }

    /**
     * Updates the value for the key (alias of put).
     */
    public void update(K key, V value) {
        put(key, value);
    }

    /**
     * Returns true if the table contains the key.
     */
    public boolean containsKey(K key) {
        int index = hash(key);
        HashTableEntry<K, V> entry = table[index];
        while (entry != null) {
            if (keysEqual(entry.key, key)) {
                return true;
            }
            entry = entry.next;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = capacity * 2;
        HashTableEntry<K, V>[] old = table;
        table = (HashTableEntry<K, V>[]) new HashTableEntry[newCapacity];
        int oldCapacity = capacity;
        capacity = newCapacity;
        // rehash without changing size or modCount
        for (int i = 0; i < oldCapacity; i++) {
            HashTableEntry<K, V> e = old[i];
            while (e != null) {
                HashTableEntry<K, V> next = e.next;
                int idx = (e.key == null ? 0 : Math.abs(e.key.hashCode()) % capacity);
                e.next = table[idx];
                table[idx] = e;
                e = next;
            }
        }
    }

    /**
     * Returns an iterator over entries.
     */
    @Override
    public Iterator<HashTableEntry<K, V>> iterator() {
        return new HashTableIter();
    }

    private final class HashTableIter implements Iterator<HashTableEntry<K, V>> {
        private int bucketIndex = 0;
        private HashTableEntry<K, V> nextEntry;
        private final int expectedModCount = modCount;

        private HashTableIter() {
            advanceToNext();
        }

        private void advanceToNext() {
            while (bucketIndex < capacity && (nextEntry = table[bucketIndex]) == null) {
                bucketIndex++;
            }
            if (nextEntry != null) {
                // prepare for subsequent step
                bucketIndex++;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasNext() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            return nextEntry != null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public HashTableEntry<K, V> next() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            HashTableEntry<K, V> result = nextEntry;
            if (nextEntry.next != null) {
                nextEntry = nextEntry.next;
            } else {
                nextEntry = null;
                while (bucketIndex < capacity) {
                    if (table[bucketIndex] != null) {
                        nextEntry = table[bucketIndex++];
                        break;
                    }
                    bucketIndex++;
                }
            }
            return result;
        }
    }

    /**
     * Returns string representation of the table.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (int i = 0; i < table.length; i++) {
            HashTableEntry<K, V> entry = table[i];
            while (entry != null) {
                if (!first) {
                    sb.append(", ");
                }
                first = false;
                sb.append(entry.key).append("=").append(entry.value);
                entry = entry.next;
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
