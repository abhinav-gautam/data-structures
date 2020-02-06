import java.util.Iterator;

public class DoublyLinkedList<T> implements Iterable<T> {

    private int size = 0;
    private Node <T> head = null;
    private Node <T> tail = null;

    // Internal node class to represent data
    private class Node<T>{
        T data;
        Node <T> prev, next;
        public Node(T data, Node<T> prev, Node<T> next){
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
        @Override
        public String toString() {
            return data.toString();
        }
    }

    // Empty this linked list, O(n)
    public void clear(){
        Node<T> trav = head;
        while(trav!=null){
            Node<T> next = trav.next;
            trav.prev = trav.next = null;
            trav = next;
        }
        head = trav = tail = null;
        size = 0;
    }

    //Return size of the linked list
    public int size(){
        return size;
    }

    //Is this linked list empty?
    public boolean isEmpty(){
        return size()==0;
    }

    //Add an element to the tail of linked list, O(1)
    public void add(T elem){
        addLast(elem);
    }

    //Add an element to the beginning of the linked list, O(1)
    public void addFirst(T elem){
        if (isEmpty()){
            head = tail = new Node<T>(elem, null, null);
        }else{
            head.prev = new Node<T>(elem, null, head);
            head = head.prev;
        }
        size++;
    }

    //Add a node to the tail of linked list, O(1)
    public void addLast(T elem){
        if (isEmpty()){
            head = tail = new Node<T>(elem, null, null);
        }else{
            tail.next = new Node<T>(elem, tail, null);
            tail = tail.next;
        }
        size++;
    }

    //Check the value of the first node if it exists, O(1)
    public T peekFirst(){
        if (isEmpty()) throw new RuntimeException("Empty List");
        return head.data;
    }

    //Check the value of the first node if it exists, O(1)
    public T peekLast(){
        if (isEmpty()) throw new RuntimeException("Empty List");
        return tail.data;
    }

    //Remove the first value at the head of the linked list, O(1)
    public T removeFirst(){
        if (isEmpty()){
            throw new RuntimeException("Empty List");
        }
        T data = head.data;
        head = head.next;
        --size;
        if (isEmpty()){
            tail = null;
        }else{
            head.prev = null;
        }
        return data;
    }

    //Remove the last value at the tail of the linked list, O(1)
    public T removeLast(){
        if (isEmpty()) throw new RuntimeException("Emptty List");

        T data = tail.data;
        tail = tail.prev;
        --size;

        if (isEmpty()){
            head = null;
        }else {
            tail.next = null;
        }
        return data;
    }

    //Remove an arbitrary node from the linked list. O(1)
    private T remove(Node<T> node){
        if(node.prev == null) return removeFirst();
        if(node.next == null) return removeLast();

        node.next.prev = node.prev;
        node.prev.next = node.next;

        T data = node.data;

        node.data = null;
        node = node.prev = node.next = null;

        --size;

        return data;
    }

    // Remove a node at a particular index, O(n)
    public T removeAt(int index){
        if(index<0 || index>=size) throw new IllegalArgumentException();

        int i;
        Node<T> trav;

        if(index<size/2){
            for (i=0,trav=head; i!=index;i++){
                trav = trav.next;
            }
        }else{
            for (i=size-1,trav=tail; i!=index; i++){
                trav = trav.prev;
            }
        }
        return remove(trav);
    }

    // Remove a particular value in the linked list, O(n)
    public boolean remove(Object obj){
        Node<T> trav;

        if(obj == null){
            for (trav = head; trav!=null; trav = trav.next){
                if(trav.data == null){
                    remove(trav);
                    return true;
                }
            }
        }else{
            for (trav = head; trav!=null; trav = trav.next){
                if (obj.equals(trav.data)){
                    remove(trav);
                    return true;
                }
            }
        }
        return false;
    }

    //Find the index of a particular value in the linked list, O(n)
    public int indexOf(Object obj) {
        int index;
        Node<T> trav;

        if (obj == null) {
            for (trav = head, index = 0; trav != null; trav = trav.next, index++) {
                if (trav.data == null) {
                    return index;
                }
            }
        } else {
            for (trav = head, index = 0; trav != null; trav = trav.next, index++) {
                if (obj.equals(trav.data)) {
                    return index;
                }
            }
        }
        return -1;
    }

    //Check if a value is contained in the linked list
    public boolean contains(Object obj){
        return indexOf(obj)!=-1;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> trav = head;
            @Override
            public boolean hasNext() {
                return trav != null;
            }

            @Override
            public T next() {
                T data = trav.data;
                trav = trav.next;
                return data;
            }
        };
    }

    @Override
    public String toString() {
       StringBuilder sb = new StringBuilder();
       sb.append("[");
       Node<T> trav = head;
       while(trav!=null){
           sb.append(trav.data+",");
           trav = trav.next;
       }
       sb.append("]");
       return sb.toString();
    }
}
