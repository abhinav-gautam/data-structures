package com.abhinavgautam;

import java.util.Arrays;

public class FenwickTree {
    // This array contains the fenwick tree ranges
    private long [] tree;

    // Create an empty Fenwick tree
    public FenwickTree(int size){
        tree = new long[size + 1];
    }
    // Make sure the values array is 1 based meaning values[0] does not get used.
    public FenwickTree(long[] values){
        if (values == null) throw new IllegalArgumentException("Values array can not be null");

        // Make a clone of the values array since we manipulate the array in-place destroying all its original content.
        tree = values.clone();
        for (int i = 1; i < tree.length; i++) {
            int j = i + lsb(i);
            if(j < tree.length) tree[j] += tree[i];
        }
    }
    // Returns the least significant bit
    private int lsb(int i){
        return Integer.lowestOneBit(i);
    }
    // Computes the prefix sum [1,i]
    public long prefixSum(int i){
        long sum = 0L;
        while(i != 0){
            sum += tree[i];
            i -= lsb(i);
        }
        return sum;
    }
    // Returns the sum of the interval [i,j]
    public long sum(int i, int j){
        if(j < i) throw new IllegalArgumentException("Make sure j > i");
        return prefixSum(j) - prefixSum(i - 1);
    }
    // Add k to index i
    public void add(int i, long k){
        while (i<tree.length){
            tree[i] += k;
            i += lsb(i);
        }
    }
    // Set index i to be equal to k
    public void set(int i, long k){
        long value = sum(i,i);
        add(i,k-value);
    }

    @Override
    public String toString() {
        return Arrays.toString(tree);
    }
}
