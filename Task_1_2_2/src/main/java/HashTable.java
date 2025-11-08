import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Auto-generated tests and support code.
 */
public class HashTable<K, V> implements Iterable<HashTableEntry<K, V>> {
    private HashTableEntry<K, V>[] table;
    private int size;
    private int capacity;
    private int modCount;

    private static final float LOAD_FACTOR = 0.75f;

    @SuppressWarnings("unchecked")
    public HashTable() {
        this.capacity = 16;
        this.table = (HashTableEntry<K, V>[]) new HashTableEntry[capacity];
        this.modCount = 0;
        this.size = 0;
    }

    private int hash(K key) {
        if (key == null) return 0;
        return Math.abs(key.hashCode()) % capacity;
    }
    /**
     * Auto-generated.
     */

    public void put(K key, V value) {
        int index = hash(key);
        HashTableEntry<K, V> entry = table[index];
        while (entry != null) {
            if (keysEqual(entry.key, key)) {
                entry.value = value;
                modCount++;
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
     * Auto-generated.
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
     * Auto-generated.
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
     * Auto-generated.
     */

    public void update(K key, V value) {
        put(key, value);
    }
    /**
     * Auto-generated.
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
        capacity *= 2;
        HashTableEntry<K, V>[] old = table;
        table = (HashTableEntry<K, V>[]) new HashTableEntry[capacity];
        int oldSize = size;
        size = 0;
        for (HashTableEntry<K, V> e : old) {
            HashTableEntry<K, V> cur = e;
            while (cur != null) {
                put(cur.key, cur.value);
                cur = cur.next;
            }
        }
        size = oldSize;
    }

    private boolean keysEqual(K a, K b) {
        return (a == b) || (a != null && a.equals(b));
    }

    @Override
    public Iterator<HashTableEntry<K, V>> iterator() {
        return new HashTableIterator();
    }

    private class HashTableIterator implements Iterator<HashTableEntry<K, V>> {
        private int expectedModCount;
        private int bucketIndex;
        private HashTableEntry<K, V> nextEntry;

        HashTableIterator() {
            this.expectedModCount = modCount;
            this.bucketIndex = 0;
            this.nextEntry = null;
            advanceToNextNonEmpty();
        }


        private void advanceToNextNonEmpty() {
            if (nextEntry != null && nextEntry.next != null) {
                nextEntry = nextEntry.next;
                return;
            }
            nextEntry = null;
            while (bucketIndex < capacity) {
                if (table[bucketIndex] != null) {
                    nextEntry = table[bucketIndex++];
                    return;
                }
                bucketIndex++;
            }
        }

        @Override
/**
 * Auto-generated.
 */
        public boolean hasNext() {
            return nextEntry != null;
        }

        @Override
        public HashTableEntry<K, V> next() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException("HashTable modified during iteration");
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

        @Override
/**
 * Auto-generated.
 */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
/**
 * Auto-generated.
 */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < table.length; i++) {
            HashTableEntry<K, V> entry = table[i];
            while (entry != null) {
                sb.append(entry.key).append("=").append(entry.value).append(", ");
                entry = entry.next;
            }
        }
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("}");
        return sb.toString();
    }
}
