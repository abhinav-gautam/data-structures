public class Main {

    public static void main(String[] args) {
        DoublyLinkedList<Integer> doublyLinkedList = new DoublyLinkedList<Integer>();
        for (int i = 0; i <= 10; i++) {
            doublyLinkedList.add(i);
        }
        doublyLinkedList.add(15);
        doublyLinkedList.remove(6);
        System.out.println(doublyLinkedList.toString());
    }
}
