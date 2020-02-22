import jdk.dynalink.linker.LinkerServices;

import java.util.*;

public class PQueue<T extends Comparable<T>> {
    //Number of elements currently inside the heap
    private int heapSize = 0;

    //The internal capacity of the heap
    private int heapCapacity = 0;

    //A dynamic list to track the elements inside the heap
    private List<T> heap = null;

    //Map to track the node value with the indices
    private Map<T, TreeSet<Integer>> map = new HashMap<>();

    //Construct an initially empty PQ
    public PQueue(){
        this(1);
    }

    //Construct a PQ with initial capacity
    public PQueue(int size){
        heap = new ArrayList<>(size);
    }

    //Construct a PQ using heapify in O(n)
    public PQueue(T[] elems){
        heapSize = heapCapacity = elems.length;
        heap = new ArrayList<>(heapCapacity);

        //Place all element in the heap
        for (int i = 0; i < heapSize; i++) {
            mapAdd(elems[i],i);
            heap.add(elems[i]);
        }

        //Heapify Process, O(n)
        for (int i = Math.max(0, (heapSize/2)-1); i >= 0 ; i--) {
            sink(i);
        }

    }

    //PQ construction, O(nlog(n))
    public PQueue(Collection<T> elems){
        this(elems.size());
        for (T elem:elems) add(elem);
    }

    //Return empty status of PQ
    public boolean isEmpty(){
        return heapSize==0;
    }

    //Clears everything inside the heap, O(n)
    public void clear(){
        for (int i = 0; i < heapCapacity; i++) {
            heap.set(i,null);
        heapSize=0;
        map.clear();
        }
    }

    //Return the size of the heap
    public int size(){
        return heapSize;
    }

    //Return the value of the element with the lowest priority in this PQ
    public T peek(){
        if(isEmpty()) return null;
        return heap.get(0);
    }

    //Removes the root of the heap, O(log(n))
    public T poll(){
        return removeAt(0);
    }

    //Test if element is in the heap, O(1)
    public boolean contains(T elem){
        //Map lookup to check containment, O(1)
        if(elem==null) return false;
        return map.containsKey(elem);
    }

    //Add an element to the PQ, O(log(n))
    public void add(T elem){
        if(elem == null) throw new IllegalArgumentException();

        if(heapSize < heapCapacity){
            heap.set(heapSize, elem);
        }else {
            heap.add(elem);
            heapCapacity++;
        }
        mapAdd(elem, heapSize);
        swim(heapSize);
        heapSize++;
    }

    //Tests if the value of node i<= node j
    private boolean less(int i, int j){
        T node1 = heap.get(i);
        T node2 = heap.get(j);
        return node1.compareTo(node2) <= 0;
    }

    //Bottom up node swim, O(log(n))
    private void swim(int k){
        //Grab the index of the parent node with respect to k
        int parent = (k-1)/2;

        //Keep swimming while we have not reached the root and while we are less than our parent
        while (k>0 && less(k,parent)){
            //Exchange k with parent
            swap(parent, k);
            k=parent;
            //Grab the index of the next parent node wrt k
            parent = (k-1)/2;
        }
    }

    //Top down node sink, O(log(n))
    private void sink(int k){
        while (true){
            int left = 2*k+1; //Left Node
            int right = 2*k+2; //Right Node
            int smallest = left; //Assume left is the smallest node of the two children

            //Find which is smaller left or right
            if (right<heapSize && less(right,left)){
                smallest = right;
            }

            //Stop if we're outside the bounds of the tree or we can't sink k anymore
            if(left>=heapSize || less(k,smallest)) break;

            //Move down the tree following the smallest node
            swap(smallest,k);
            k = smallest;
        }
    }

    //Swap two nodes. Assumes i&j are valid, O(1)
    private void swap(int i, int j){
        T i_elem = heap.get(i);
        T j_elem = heap.get(j);

        heap.set(i, j_elem);
        heap.set(j, i_elem);

        mapSwap(i_elem, j_elem, i, j);
    }

    //Remove a particular element in the heap, O(log(n))
    public boolean remove(T elem){
        if(elem == null) return false;

        //Logarithmic removal with map, O(log(n))
        Integer index = mapGet(elem);
        if (index!=null) removeAt(index);
        return index!=null;
    }

    //Remove a node at particular index, O(log(n))
    private T removeAt(int i){
        if (isEmpty()) return null;

        heapSize--;
        T removed_data = heap.get(i);
        swap(i,heapSize);
        swap(i, heapSize);

        //Obliterate the value
        heap.set(heapSize,null);
        mapRemove(removed_data,heapSize);

        //Remove last element
        if(i==heapSize) return removed_data;

        T elem = heap.get(i);

        //Try sinking
        sink(i);

        //If sinking did not work try swimming
        if(heap.get(i).equals(elem)){
            swim(i);
        }
        return removed_data;
    }

    //Recursively checks if this heap is a min heap
    public boolean isMinHeap(int k){
        //If we are outside the bounds of the heap return true
        if(k>=heapSize)return true;

        int left = 2*k+1; //Left Node
        int right = 2*k+2; //Right Node

        // Make sure that the current node k less than both of its children left and right if they exist
        if(left<heapSize && !less(k, left)) return false;
        if(right<heapSize && !less(k, right)) return false;

        //Recurse on both children to make sure they're also valid heaps
        return isMinHeap(left) && isMinHeap(right);
    }

    //Add a node value and its index to the map
    private void mapAdd(T value, int index){
        TreeSet<Integer> set = map.get(value);
        //New value being inserted to the map
        if(set == null){
            set = new TreeSet<>();
            set.add(index);
            map.put(value,set);
        //Value already exist in map
        }else{
            set.add(index);
        }
    }

    //Removes the index at the given value in the map, O(log(n))
    private void mapRemove(T value, int index){
        TreeSet<Integer> set = map.get(value);
        set.remove(index);
        if(set.size() == 0) map.remove(value);
    }

    //Extract an index position for the given value, if a value exists multiple times in the heap the highest index is returned
    private Integer mapGet(T value){
        TreeSet<Integer> set = map.get(value);
        if (set!=null) return set.last();
        return null;
    }

    //Exchange the index of two nodes internally within map
    private void mapSwap(T val1, T val2, int val1Index, int val2Index){
        Set<Integer> set1 = map.get(val1);
        Set<Integer> set2 = map.get(val2);

        set1.remove(val1Index);
        set2.remove(val2Index);

        set1.add(val2Index);
        set2.add(val1Index);
    }
}
