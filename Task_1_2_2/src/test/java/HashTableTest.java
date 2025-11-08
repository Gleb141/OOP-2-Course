import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class HashTableTest {
    private HashTable<String, Number> ht;
    @BeforeEach
    void setup(){
        ht = new HashTable<>();
    }
    @Test
    void testPutGetUpdateRemoveContains(){
        ht.put("one", 1);
        Assertions.assertEquals(1, ht.get("one").intValue(), "put get failed");
        ht.update("one", 1.0);
        Assertions.assertEquals(1.0, ht.get("one"), "update failed");
        ht.put("two", 2);
        Assertions.assertTrue(ht.containsKey("two"), "containsKey failed 1");
        ht.remove("two");
        Assertions.assertFalse(ht.containsKey("two"), "containsKey failed 2");
        ht.get("two");
        Assertions.assertNull(ht.get("two"), "assertNull failed");
    }
    @Test
    void testNullKeyAndNullValue(){
        ht.put(null, null);
        Assertions.assertTrue(ht.containsKey(null), "containsKey null failed ");
        Assertions.assertNull(ht.get(null), "assertNull failed 2");
        ht.put("nullable",null);
        Assertions.assertTrue(ht.containsKey("nullable"), "containsKey null failed ");
        Assertions.assertNull(ht.get("nullable"), "assertNull failed 3");
    }
    @Test
    void testIterationAndConcurrentModification(){
        ht.put("a", 1);
        ht.put("b", 2);
        ht.put("c", 3);

        Set<String> keys = new HashSet<>();
        for(HashTableEntry<String, Number> e : ht) {
            keys.add(e.key);
        }
        Assertions.assertEquals(Set.of("a", "b", "c"), keys, "Iteration missing keys");
        Iterator<HashTableEntry<String,Number>> it = ht.iterator();
        if(it.hasNext()){
            it.next();
        }
        ht.put("d", 4);
        Assertions.assertThrows(ConcurrentModificationException.class, it::next,
                "Expected exception during change of iteration");
    }
    @Test
    void testResizeAndCountMany(){
        HashTable<Integer,Integer> hi = new HashTable<>();
        final int n = 2000;
        for(int i = 0; i < n; i++){
            hi.put(i, i);
        }
        int cnt = 0;
        for(HashTableEntry<Integer, Integer> i : hi){
            cnt++;
        }
        Assertions.assertEquals(n, cnt,"Resize or iteration lost elements");
        Assertions.assertEquals(n-1, hi.get(n-1), "Wrong value for last key after resize");
    }
    @Test
    void testToStringContainsEntries(){
        ht.put("one", 1);
        ht.update("one", 1.0);
        ht.put("two", 2);
        String s = ht.toString();
        Assertions.assertTrue(s.contains("one="), "toString must contain 'one='");
        Assertions.assertTrue(s.contains("two="), "toString must contain 'two='");
    }
}
