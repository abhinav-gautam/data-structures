public class Main {
    public static void main(String[] args) {
        Queue<Integer> queue = new Queue<>(5);
        queue.offer(6);
        queue.offer(3);
        System.out.println(queue.toString());
        queue.poll();
        System.out.println(queue.toString());
        queue.poll();
        System.out.println(queue.toString());
    }
}
