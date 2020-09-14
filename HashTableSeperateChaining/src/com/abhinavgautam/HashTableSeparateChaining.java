package com.abhinavgautam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

class Entry<K,V>{
    int hash;
    K key;
    V value;
    // Constructor
    public Entry(K key, V value){
        this.key = key;
        this.value = value;
        this.hash = key.hashCode();
    }
    // We are not overriding the Objects equal method. No casting is required with this method.
    public boolean equals(Entry<K,V> other){
        if(this.hash != other.hash) return false;
        return this.key.equals(other.key);
    }

    @Override
    public String toString() {
        return this.key + "=>" + this.value;
    }
}

public class HashTableSeparateChaining <K,V> implements Iterable<K>{
    private static final int DEFAULT_CAPACITY = 3;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;

    private double maxLoadFactor;
    private int capacity,size,threshold = 0;
    private LinkedList<Entry<K,V>> [] table;

    public HashTableSeparateChaining(){
        this(DEFAULT_CAPACITY,DEFAULT_LOAD_FACTOR);
    }
    public HashTableSeparateChaining(int capacity){
        this(capacity,DEFAULT_LOAD_FACTOR);
    }
    // Designated Constructor
    public HashTableSeparateChaining(int capacity, double maxLoadFactor){
        if(capacity<0)
            throw new IllegalArgumentException("Illegal Capacity");
        if(maxLoadFactor<=0 || Double.isNaN(maxLoadFactor) || Double.isInfinite(maxLoadFactor))
            throw new IllegalArgumentException("Illegal maxLoadFactor");
        this.maxLoadFactor = maxLoadFactor;
        this.capacity = Math.max(DEFAULT_CAPACITY,capacity);
        this.threshold = (int) (this.capacity*this.maxLoadFactor);
        this.table = new LinkedList[this.capacity];
    }

    // Returns the number of elements inside the hash-table
    public int size(){return size;}
    // Returns true/false depending on whether hash-table is empty
    public boolean isEmpty(){return size==0;}
    // Converts a hash value to an index. This strips the negative sign and places the hash value in the domain [0,capacity)
    private int normalizeIndex(int keyHash){
        return (keyHash & 0x7FFFFFFF) % capacity;
    }
    // Clears all the contents of the hash-table
    public void clear(){
        Arrays.fill(table,null);
        size = 0;
    }
    // Returns true/false depending on whether a key is in the hash-table
    public boolean hasKey(K key){
        int bucketIndex = normalizeIndex(key.hashCode());
        return bucketSeekEntry(bucketIndex, key) != null;
    }
    // Place a value in the hash-table
    public V insert(K key, V value){
        if(key == null) throw new IllegalArgumentException("Null Key");
        Entry<K, V> newEntry = new Entry<>(key,value);
        int bucketIndex = normalizeIndex(newEntry.hash);
        return bucketInsertEntry(bucketIndex, newEntry);
    }
    // Gets a key's value from the map and return the value. Returns null if value is null and also returns null if key doesn't exist.
    public V get(K key){
        if(key == null) return null;
        int bucketIndex = normalizeIndex(key.hashCode());
        Entry<K, V> entry = bucketSeekEntry(bucketIndex,key);
        if(entry != null) return entry.value;
        return null;
    }
    // Removes a key from the hash-table and returns the value. Returns null if the value is null and also returns null if key doesn't exist.
    public V remove(K key){
        if(key == null) return null;
        int bucketIndex = normalizeIndex(key.hashCode());
        return bucketRemoveEntry(bucketIndex, key);
    }
    // Removes am entry from a given bucket if it exists.
    private V bucketRemoveEntry(int bucketIndex, K key){
        Entry<K, V> entry = bucketSeekEntry(bucketIndex, key);
        if(entry != null){
            LinkedList<Entry<K, V>> links = table[bucketIndex];
            links.remove(entry);
            --size;
            return entry.value;
        }else {
            return null;
        }
    }
    // Inserts the entry in the given bucket only if the entry does not already exist in the given bucket, but if it does then update the entry value.
    private V bucketInsertEntry(int bucketIndex, Entry<K,V> entry){
        LinkedList<Entry<K,V>> bucket = table[bucketIndex];
        if(bucket == null) table[bucketIndex] = new LinkedList<>();

        Entry<K,V> existentEntry = bucketSeekEntry(bucketIndex,entry.key);
        if(existentEntry == null){
            bucket.add(entry);
            if(++size>threshold) resizeTable();
            return null; // Using null to indicate that there was no previous entry.
        }else{
            V oldValue = existentEntry.value;
            existentEntry.value = entry.value;
            return oldValue;
        }
    }
    // Finds and returns a particular entry in the given bucket if it exist, returns null otherwise.
    private Entry<K,V> bucketSeekEntry(int bucketIndex, K key){
        if(key == null) return null;
        LinkedList<Entry<K,V>> bucket = table[bucketIndex];
        if(bucket == null) return null;
        for (Entry<K,V> entry : bucket){
            if(entry.key.equals(key)){
                return entry;
            }
        }
        return null;
    }
    // Resize the internal table holding buckets of entries.
    private void resizeTable(){
        capacity *= 2;
        threshold = (int) (capacity * maxLoadFactor);

        LinkedList<Entry<K,V>> [] newTable = new LinkedList[capacity];
        for (int i = 0; i < table.length; i++) {
            if(table[i] != null){
                for (Entry<K,V> entry : table[i]){
                    int bucketIndex = normalizeIndex(entry.hash);
                    LinkedList<Entry<K,V>> bucket = newTable[bucketIndex];
                    if(bucket == null) newTable[bucketIndex] = bucket = new LinkedList<>();
                    bucket.add(entry);
                }
                // Avoid memory leak. Help the GC
                table[i].clear();
                table[i] = null;
            }
        }
        table = newTable;
    }
    // Returns the list of keys found within the hash-table.
    public List<K> keys(){
        List<K> keys = new ArrayList<>(size());
        for(LinkedList<Entry<K,V>> bucket : table){
            if(bucket != null){
                for (Entry<K,V> entry : bucket){
                    keys.add(entry.key);
                }
            }
        }
        return keys;
    }
    // Returns the list of values found within the hash-table.
    public List<V> values(){
        List<V> values = new ArrayList<>(size());
        for(LinkedList<Entry<K,V>> bucket : table){
            if(bucket != null){
                for (Entry<K,V> entry : bucket){
                    values.add(entry.value);
                }
            }
        }
        return values;
    }

    // Return an iterator to iterate over all the keys in this map
    @Override
    public java.util.Iterator<K> iterator() {
        final int elementCount = size();
        return new java.util.Iterator<K>() {

            int bucketIndex = 0;
            java.util.Iterator<Entry<K, V>> bucketIter = (table[0] == null) ? null : table[0].iterator();

            @Override
            public boolean hasNext() {

                // An item was added or removed while iterating
                if (elementCount != size) throw new java.util.ConcurrentModificationException();

                // No iterator or the current iterator is empty
                if (bucketIter == null || !bucketIter.hasNext()) {

                    // Search next buckets until a valid iterator is found
                    while (++bucketIndex < capacity) {
                        if (table[bucketIndex] != null) {

                            // Make sure this iterator actually has elements -_-
                            java.util.Iterator<Entry<K, V>> nextIter = table[bucketIndex].iterator();
                            if (nextIter.hasNext()) {
                                bucketIter = nextIter;
                                break;
                            }
                        }
                    }
                }
                return bucketIndex < capacity;
            }

            @Override
            public K next() {
                return bucketIter.next().key;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    // Returns a string representation of this hash table
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < capacity; i++) {
            if (table[i] == null) continue;
            for (Entry<K, V> entry : table[i]) sb.append(entry + ", ");
        }
        sb.append("}");
        return sb.toString();
    }
}
