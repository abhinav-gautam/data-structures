package com.abhinavgautam;

import java.util.*;

public class HashTableQuadraticProbing<K,V> implements Iterable<K> {
    private double loadFactor;
    private int capacity, threshold, modificationCount = 0;

    // usedBuckets counts the total number of used in the hash-table including cells marked as deleted.
    // keyCount tracks the total number of unique keys inside the hash table
    private int usedBuckets =0 , keyCount = 0;

    // These arrays store the key value pairs
    private K [] keyTable;
    private V [] valueTable;

    // Flag used to indicate whether an item was found in the hash-table
    private boolean containsFlag = false;

    // Special marker token used to indicate the deletion of the key-value pair
    private final K TOMBSTONE = (K) (new Object());

    private static final int DEFAULT_CAPACITY = 8;
    private static final double DEFAULT_LOAD_FACTOR = 0.45;

    // Constructors
    public HashTableQuadraticProbing(){
        this(DEFAULT_CAPACITY,DEFAULT_LOAD_FACTOR);
    }
    public HashTableQuadraticProbing(int capacity){
        this(capacity,DEFAULT_LOAD_FACTOR);
    }
    // Designated Constructor
    public HashTableQuadraticProbing(int capacity, double loadFactor){
        if(capacity <= 0)
            throw new IllegalArgumentException("Illegal Capacity");
        if(loadFactor <= 0 || Double.isNaN(loadFactor) || Double.isInfinite(loadFactor))
            throw new IllegalArgumentException("Illegal Load Factor");
        this.loadFactor = loadFactor;
        this.capacity = Math.max(capacity,DEFAULT_CAPACITY);
        threshold = (int) (this.capacity*this.loadFactor);

        keyTable = (K[]) new Object[this.capacity];
        valueTable = (V[]) new Object[this.capacity];
    }
    // Given a number this method finds the next power of two above this value.
    private static int next2power(int n){
        return Integer.highestOneBit(n) << 1;
    }
    // Quadratic Probing function
    private static int P(int x){
        return (x*x+x) >> 1;
    }
    // Converts the hash value to an index. This strips the negative sign and places the hash value in the domain [0,capacity)
    private int normalizeIndex(int keyHash){
        return (keyHash & 0x7FFFFFFF) % capacity;
    }
    // Clears all the contents in the hash-table
    public void clear(){
        for (int i = 0; i < capacity; i++) {
            keyTable[i] = null;
            valueTable[i] = null;
        }
        keyCount=usedBuckets=0;
        modificationCount++;
    }
    // Returns the number of keys currently inside the hash-table.
    public int size(){return keyCount;}
    // Returns true/false depending on whether hash-table is empty.
    public boolean isEmpty(){return keyCount==0;}
    // Places a key-value pair into the hash-table.
    // If key already exists in the hash-table then value is updated.
    public V insert(K key, V value){
        if(key == null) throw new IllegalArgumentException("Null Key");
        if (usedBuckets >= threshold) resizeTable();

        final int hash = normalizeIndex(key.hashCode());
        // i -> HT index, j -> Tombstone index, x -> Probing function offset
        int i = hash, j = -1, x = 1;
        do {
            // The current slot was previously deleted
            if(keyTable[i] == TOMBSTONE){
                if(j==-1) j=i;
                // The current cell already contains a key
            }else if(keyTable[i] != null){
                // The key we are trying to insert already exists so update its value.
                if(keyTable[i].equals(key)){
                    V oldValue = valueTable[i];
                    // If we haven't hit the tombstone, update the value
                    if(j==-1){
                        valueTable[i] = value;
                        // If we hit the tombstone then swap it with the tombstone (optimization)
                    }else{
                        keyTable[i] = TOMBSTONE;
                        valueTable[i] = null;
                        keyTable[j] = key;
                        valueTable[j] = value;
                    }
                    modificationCount++;
                    return oldValue;
                }
                // Current cell is null so an insertion/update can occur.
            }else{
                // If we haven't hit the tombstone, insert key-value at i.
                if(j == -1){
                    usedBuckets ++;
                    keyCount++;
                    keyTable[i] = key;
                    valueTable[i] = value;
                    // If we hit the tombstone earlier, insert the key-value in place of tombstone.
                }else{
                    keyCount++;
                    keyTable[j] = key;
                    valueTable[j] =value;
                }
                modificationCount ++;
                return null;
            }
            i = normalizeIndex(hash + P(x++));
        }while (true);
    }
    // Returns true/false on whether the given key exists in the hash-table.
    public boolean hasKey(K key){
        get(key);
        return containsFlag;
    }
    // Get the value associated with the input key.
    // Returns null if the value is null and also if the value doesn't exists.
    public V get(K key){
        if(key == null) throw new IllegalArgumentException("Null key");
        final int hash = normalizeIndex(key.hashCode());
        int i = hash, j = -1, x = 1;

        // Starting at the original hash index, quadratically probe until we find the spot where our key is.
        // Or we hit the null element in which case our key does not exist.
        do {
            if(keyTable[i] == TOMBSTONE){
                if(j == -1){
                    j = i;
                }
            // We hit a non-null key
            }else if(keyTable[i] != TOMBSTONE){
                // Found the key we want
                if (keyTable[i].equals(key)){
                    containsFlag = true;
                    // If we hit tombstone earlier perform lazy relocation (optimization)
                    if(j != -1){
                        keyTable[j] = keyTable[i];
                        valueTable[j] = valueTable[i];

                        keyTable[i] = TOMBSTONE;
                        valueTable[i] = null;
                        return valueTable[j];
                    }else{
                        return valueTable[i];
                    }
                }
            // Element was not found in the hash-table
            }else{
                containsFlag = false;
                return null;
            }
            i = normalizeIndex(hash + P(x++));
        }while(true);
    }
    // Removes a key from the hash-table and returns the value.
    public V remove(K key){
        if(key == null) throw new IllegalArgumentException("Null key");
        final int hash = normalizeIndex(key.hashCode());
        int i = hash, x = 1;

        // Starting at the original hash index, quadratically probe until we find the spot where our key is.
        // Or we hit the null element in which case our key does not exist.
        for(;; i = normalizeIndex(hash + P(x++))) {
            if(keyTable[i] == TOMBSTONE) continue;
            if(keyTable == null) return null;
            if(keyTable[i].equals(key)){
                keyCount--;
                modificationCount++;
                V oldValue = valueTable[i];
                keyTable[i] = TOMBSTONE;
                valueTable[i] = null;
                return oldValue;
            }
        }
    }
    // Returns a list of keys found in the hash-table
    public List<K> keys(){
        List<K> keys = new ArrayList<>(size());
        for (int i = 0; i < capacity; i++) {
            if (keyTable[i] != null && keyTable[i] != TOMBSTONE){
                keys.add(keyTable[i]);
            }
        }
        return keys;
    }
    // Returns a list of values found in the hash-table
    public List<V> values(){
        List<V> values = new ArrayList<>(size());
        for (int i = 0; i < capacity; i++) {
            if (keyTable[i] != null && keyTable[i] != TOMBSTONE){
                values.add(valueTable[i]);
            }
        }
        return values;
    }
    // Double the size of hash-table
    private void resizeTable(){
        capacity *= 2;
        threshold = (int) (capacity*loadFactor);

        K[] oldKeyTable =(K[]) new Object[capacity];
        V[] oldValueTable =(V[]) new Object[capacity];

        // Perform key table pointer swap;
        K[] keyTableTmp = keyTable;
        keyTable = oldKeyTable;
        oldKeyTable = keyTableTmp;

        // Perform value table pointer swap;
        V[] valueTableTmp = valueTable;
        valueTable = oldValueTable;
        oldValueTable = valueTableTmp;

        // Reset the keyCount and usedBucket count since we are going to re-insert all the keys again.
        keyCount = usedBuckets = 0;
        for (int i = 0; i < oldKeyTable.length; i++) {
            if(oldKeyTable[i] != null && oldKeyTable[i] != TOMBSTONE){
                insert(oldKeyTable[i],oldValueTable[i]);
            }
            oldKeyTable[i] = null;
            oldValueTable[i] = null;
        }
    }

    // Return a String view of this hash-table.
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < capacity; i++)
            if (keyTable[i] != null && keyTable[i] != TOMBSTONE) sb.append(keyTable[i] + " => " + valueTable[i] + ", ");
        sb.append("}");

        return sb.toString();
    }
    @Override
    public Iterator<K> iterator() {
        // Before the iteration begins record the number of modifications
        // done to the hash-table. This value should not change as we iterate
        // otherwise a concurrent modification has occurred :0
        final int MODIFICATION_COUNT = modificationCount;

        return new Iterator<K>() {
            int index, keysLeft = keyCount;

            @Override
            public boolean hasNext() {
                // The contents of the table have been altered
                if (MODIFICATION_COUNT != modificationCount) throw new ConcurrentModificationException();
                return keysLeft != 0;
            }

            // Find the next element and return it
            @Override
            public K next() {
                while (keyTable[index] == null || keyTable[index] == TOMBSTONE) index++;
                keysLeft--;
                return keyTable[index++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
