package com.abhinavgautam;
import com.abhinavgautam.TreePrinter;
import com.abhinavgautam.TreePrinter.PrintableNode;

public class AVLTree<T extends Comparable<T>> implements Iterable<T> {

    class Node implements TreePrinter.PrintableNode{
        // bf is short for Balance Factor
        int bf;
        // The value data contained within the node
        T value;
        // The height of this node in the tree
        int height;
        // The left and right children of this node
        Node left, right;
        // Constructor
        public Node(T value){
            this.value = value;
        }
        @Override
        public PrintableNode getLeft() {
            return left;
        }

        @Override
        public PrintableNode getRight() {
            return right;
        }

        @Override
        public String getText() {
            return value.toString();
        }
    }

    // The root node of the AVL tree
    private Node root;

    // Tracks the number of nodes inside the tree
    private int nodeCount = 0;

    // The height of the rooted tree is the number of the edges between the tree's root and the furthest leaf.
    // This means that a tree containing a single node has a height of 0.
    public int height() {
        if(root == null) return 0;
        return root.height;
    }

    // Returns the number of nodes in the tree.
    public int size(){
        return nodeCount;
    }

    // Returns whether or not tree is empty
    public boolean isEmpty(){
        return size() == 0;
    }

    // Returns true/false depending on whether or not a value exists in the tree
    public boolean contains(T value){
        return contains(root,value);
    }

    // Recursive contains helper method
    private boolean contains(Node node, T value){
        if(node == null) return false;

        // Compare current value to the value in the node
        int cmp = value.compareTo(node.value);

        // Dig into left subtree
        if(cmp<0) return contains(node.left,value);

        // Dig into right subtree
        if(cmp>0) return contains(node.right,value);

        // Value found in tree
        return true;
    }

    // Insert a value to the AVL tree. The value must not be null. O(log(n))
    public boolean insert(T value){
        if (value == null) return false;
        if(!contains(root,value)){
            root = insert(root,value);
            nodeCount++;
            return true;
        }
        return false;
    }
    // Insert a value inside the AVL tree.
    private Node insert(Node node, T value){
        // Base case
        if (node == null) return new Node(value);

        // Compare current value with the value in the node.
        int cmp = value.compareTo(node.value);

        // Insert node in the left subtree
        if(cmp<0){
            node.left = insert(node.left,value);
        // Insert node in the right subtree
        } else{
            node.right = insert(node.right,value);
        }

        // Update the balance factor and height values
        this.update(node);

        // Re-balance the tree
        return this.balance(node);
    }

    // Updates the balance factor and height values
    private void update(Node node){
        // Variables for left/right subtree height
        int leftNodeHeight = -1;
        int rightNodeHeight = -1;

        if(node.left != null) leftNodeHeight = node.left.height;
        if(node.right != null) rightNodeHeight = node.right.height;

        // Update this node's height
        node.height = 1 + Math.max(leftNodeHeight,rightNodeHeight);

        // Update the balance factor
        node.bf = rightNodeHeight-leftNodeHeight;
    }

    // Re-balance the tree
    private Node balance(Node node){
        // Left heavy subtree
        if(node.bf == -2){
            if(node.left.bf <= 0) return leftLeftCase(node);
            else return leftRightCase(node);
        // Right heavy subtree
        } else if(node.bf == 2){
            if(node.right.bf >= 0) return rightRightCase(node);
            else return rightLeftCase(node);
        }
        // Node has a balance factor of -1, 0 or +1. No need to balance
        return node;
    }

    private Node leftLeftCase(Node node){
        return rightRotation(node);
    }
    private Node leftRightCase(Node node){
        node.left = leftRotation(node.left);
        return leftLeftCase(node);
    }
    private Node rightRightCase(Node node){
        return leftRotation(node);
    }
    private Node rightLeftCase(Node node){
        node.right = rightRotation(node.right);
        return rightRightCase(node);
    }

    private Node rightRotation(Node node){
        Node newParent = node.left;
        node.left = newParent.right;
        newParent.right = node;

        // After rotation update balance factor and height values
        update(node);
        update(newParent);
        return newParent;
    }

    private Node leftRotation(Node node){
        Node newParent = node.right;
        node.right = newParent.left;
        newParent.left =node;

        update(node);
        update(newParent);
        return newParent;
    }

    // Remove a value from this binary tree if it exists, O(log(n))
    public boolean remove(T elem){
        if(elem == null) return false;

        if(contains(root,elem)){
            root = remove(root,elem);
            nodeCount--;
            return true;
        }
        return false;
    }

    private Node remove(Node node, T elem){
        if(node == null) return null;

        int cmp = elem.compareTo(node.value);
        if(cmp<0){
            node.left = remove(node.left,elem);
        }else if(cmp>0){
            node.right = remove(node.right,elem);
        // Found the node we wish to remove
        }else{
            if(node.left == null){
                return node.right;
            }else if(node.right == null){
                return node.left;
            }else{
                // Choose to remove from left subtree
                if(node.left.height > node.right.height){
                    // Swap the value of the successor into the node
                    T successorValue = findMax(node.left);
                    node.value = successorValue;
                    // Remove the largest node in the left subtree
                    node.left = remove(node.left,successorValue);
                // Choose to remove from right subtree
                }else{
                    // Swap the value of the successor into the node
                    T successorValue = findMin(node.right);
                    node.value = successorValue;
                    // Remove the largest node in the left subtree
                    node.right = remove(node.right,successorValue);
                }
            }
        }

        // Update balance factor and height values
        update(node);

        // Re-balance tree
        return balance(node);
    }

    // Helper method to find the leftmost node (which has the smallest value)
    private T findMin(Node node) {
        while (node.left != null) node = node.left;
        return node.value;
    }

    // Helper method to find the rightmost node (which has the largest value)
    private T findMax(Node node) {
        while (node.right != null) node = node.right;
        return node.value;
    }

    // Returns as iterator to traverse the tree in order.
    public java.util.Iterator<T> iterator() {

        final int expectedNodeCount = nodeCount;
        final java.util.Stack<Node> stack = new java.util.Stack<>();
        stack.push(root);

        return new java.util.Iterator<T>() {
            Node trav = root;

            @Override
            public boolean hasNext() {
                if (expectedNodeCount != nodeCount) throw new java.util.ConcurrentModificationException();
                return root != null && !stack.isEmpty();
            }

            @Override
            public T next() {

                if (expectedNodeCount != nodeCount) throw new java.util.ConcurrentModificationException();

                while (trav != null && trav.left != null) {
                    stack.push(trav.left);
                    trav = trav.left;
                }

                Node node = stack.pop();

                if (node.right != null) {
                    stack.push(node.right);
                    trav = node.right;
                }

                return node.value;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    // Prints the tree
    public void display(){
        TreePrinter.getTreeDisplay(root);
    }

    // Example usage of AVL Tree
    public static void main(String[] args) {
        AVLTree<Integer> tree = new AVLTree<>();
        for (int i = 0; i < 100; i++) {
            tree.insert((int)(Math.random()*100));
        }
        tree.display();
    }
}
