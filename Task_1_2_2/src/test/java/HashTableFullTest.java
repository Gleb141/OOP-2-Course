import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Set;

public class HashTableFullTest {

    private HashTable<String, Number> ht;

    @BeforeEach
    void setUp() {
        ht = new HashTable<>();
    }

    @Test
    void putGetUpdate_basicAndOverwrite() {
        // empty get
        Assertions.assertNull(ht.get("missing"));

        // simple put/get
        ht.put("one", 1);
        Assertions.assertEquals(1, ht.get("one"));

        // overwrite same key should update value (no duplicate)
        ht.put("one", 2);
        Assertions.assertEquals(2, ht.get("one"));

        // update() is an alias of put()
        ht.update("one", 3);
        Assertions.assertEquals(3, ht.get("one"));

        // non-existing still null
        Assertions.assertNull(ht.get("two"));
    }

    @Test
    void nullKey_pathsCovered() {
        // put/get/update/remove with null key should work since hash(null) -> 0 and keysEqual handles nulls
        Assertions.assertNull(ht.get(null));
        Assertions.assertFalse(ht.containsKey(null));

        ht.put(null, 10);
        Assertions.assertTrue(ht.containsKey(null));
        Assertions.assertEquals(10, ht.get(null));

        ht.update(null, 11);
        Assertions.assertEquals(11, ht.get(null));

        ht.remove(null);
        Assertions.assertFalse(ht.containsKey(null));
        Assertions.assertNull(ht.get(null));
    }

    // Helper key to force collisions in the same bucket
    static final class BadKey {
        final int id;
        BadKey(int id) { this.id = id; }
        @Override public int hashCode() { return 42; } // all collide
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BadKey)) return false;
            return id == ((BadKey) o).id;
        }
        @Override public String toString() { return "k"+id; }
    }

    @Test
    void collisions_removeHeadMiddleTail() {
        HashTable<BadKey, Integer> h = new HashTable<>();

        BadKey k1 = new BadKey(1);
        BadKey k2 = new BadKey(2);
        BadKey k3 = new BadKey(3);

        // Insert 3 colliding keys
        h.put(k1, 10);
        h.put(k2, 20);
        h.put(k3, 30);

        // All present
        Assertions.assertEquals(10, h.get(k1));
        Assertions.assertEquals(20, h.get(k2));
        Assertions.assertEquals(30, h.get(k3));

        // Remove head of chain (depends on insertion policy but remove should handle any position).
        h.remove(k1);
        Assertions.assertNull(h.get(k1));
        Assertions.assertEquals(20, h.get(k2));
        Assertions.assertEquals(30, h.get(k3));

        // Remove middle or head (depending on insertion order). Remove k2 next.
        h.remove(k2);
        Assertions.assertNull(h.get(k2));
        Assertions.assertEquals(30, h.get(k3));

        // Remove tail / last remaining
        h.remove(k3);
        Assertions.assertNull(h.get(k3));

        // Removing a non-existent key must be a no-op
        h.remove(new BadKey(99));
    }

    @Test
    void containsKey_trueFalse() {
        ht.put("a", 1);
        Assertions.assertTrue(ht.containsKey("a"));
        Assertions.assertFalse(ht.containsKey("b"));
    }

    @Test
    void iterator_basicTraversalAndNoSuchElement() {
        // empty table
        Iterator<HashTableEntry<String, Number>> itEmpty = ht.iterator();
        Assertions.assertFalse(itEmpty.hasNext());
        Assertions.assertThrows(NoSuchElementException.class, itEmpty::next);
        // non-empty iteration covers buckets and chaining
        ht.put("x", 1);
        ht.put("y", 2);
        ht.put("z", 3);
        Set<String> seen = new HashSet<>();
        for (HashTableEntry<String, Number> e : ht) {
            Assertions.assertNotNull(e);
            Assertions.assertTrue(e.toString().contains("=")); // covers HashTableEntry.toString
            seen.add(e.toString().split("=")[0]);
        }
        Assertions.assertEquals(Set.of("x","y","z"), seen);

        // Now drive iterator to end and check NoSuchElementException on next()
        Iterator<HashTableEntry<String, Number>> it = ht.iterator();
        while (it.hasNext()) {
            it.next();
        }
        Assertions.assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void iterator_concurrentModificationDetected() {
        ht.put("a", 1);
        ht.put("b", 2);

        // CME before any next()
        Iterator<HashTableEntry<String, Number>> it = ht.iterator();
        ht.put("c", 3); // structural change
        Assertions.assertThrows(ConcurrentModificationException.class, it::next);

        // CME after some iteration
        it = ht.iterator();
        HashTableEntry<String, Number> first = it.next(); // ok
        // structural change via remove or update/put
        ht.remove(first.key);
        Assertions.assertThrows(ConcurrentModificationException.class, it::next);
    }

    @Test
    void resize_preservesAllEntries() {
        // initial capacity is 16 with load factor 0.75, so resizing should happen after inserting > 12 items
        int n = 40; // large enough to force at least one resize
        Set<Integer> keys = new HashSet<>();
        for (int i = 0; i < n; i++) {
            String k = "k" + i;
            ht.put(k, i);
            keys.add(i);
        }
        // all keys should be retrievable after resize
        for (int i = 0; i < n; i++) {
            Assertions.assertEquals(i, ht.get("k" + i));
        }

        // also validate iteration has exactly n items (no duplicates after resize)
        int count = 0;
        for (HashTableEntry<String, Number> ignored : ht) {
            count++;
        }
        Assertions.assertEquals(n, count);
    }

    @Test
    void toString_formatsBracesAndNoTrailingComma() {
        // empty
        Assertions.assertEquals("{}", ht.toString());

        // with entries
        ht.put("one", 1);
        ht.update("one", 1.0);
        ht.put("two", 2);

        String s = ht.toString();
        Assertions.assertTrue(s.startsWith("{") && s.endsWith("}"));
        Assertions.assertFalse(s.endsWith(", }"), "toString must not have a trailing comma");
        Assertions.assertTrue(s.contains("one="), "toString must contain 'one='");
        Assertions.assertTrue(s.contains("two="), "toString must contain 'two='");
    }
}
