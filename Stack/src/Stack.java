import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.LinkedList;

public class Stack<T> implements Iterable<T> {

    private LinkedList<T> list = new LinkedList<>();

    // Create an empty list
    public Stack(){}

    // Create a Stack with an initial element
    public Stack(T firstElem){
        push(firstElem);
    }

    // Return the number of elements in the stack
    public int size(){
        return list.size();
    }

    //Check if stack is empty
    public boolean isEmpty(){
        return size() == 0;
    }

    // Push an element on the stack
    public void push(T elem){
        list.addLast(elem);
    }

    //Pop an element off the stack
    public T pop(){
        if(isEmpty()) throw new EmptyStackException();
        return list.removeLast();
    }

    //Peek the top of the stack without removing the element
    public T peek(){
        if(isEmpty()) throw new EmptyStackException();
        return list.peekLast();
    }

    @Override
    public String toString() {
        return list.toString();
    }
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}
