public class Main {
    public static void main(String[] args) {
        Stack<Integer> stack = new Stack<>(5);
        System.out.println(stack.toString());
        stack.push(6);
        stack.push(52);
        System.out.println(stack.toString());
        stack.pop();
        System.out.println(stack.toString());
    }
}
