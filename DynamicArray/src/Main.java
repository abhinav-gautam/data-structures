public class Main {

    public static void main(String[] args) {
        DynamicArray<Integer> dynamicArr = new DynamicArray<Integer>();
        dynamicArr.add(15);
        dynamicArr.add(26);
        dynamicArr.add(95);
        dynamicArr.add(69);
        System.out.println(dynamicArr.removeAt(0));
        dynamicArr.clear();
        System.out.println(dynamicArr.toString());
    }
}
