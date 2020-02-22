import java.util.ConcurrentModificationException;

public class BinarySearchTree<T extends Comparable<T>> {
    //Track the number of nodes in this BST
    private int nodeCount = 0;

    //This is a rooted node to maintain the handle on the root node
    private Node root = null;

    //Internal node class containing node references and the actual node data
    private class Node{
        T data;
        Node left, right;
        public Node(Node left, Node right, T elem){
            this.data = elem;
            this.left = left;
            this.right = right;
        }
    }

    //Get the number of nodes in this binary tree
    public int size(){
        return nodeCount;
    }

    //Check is this binary tree is empty
    public boolean isEmpty(){
        return size()==0;
    }

    //Add an element to this binary tree. Returns true on successful insertion
    public boolean add(T elem){
        //Check if the value already exists in this BT, if it does ignore adding it
        if (contains(elem)){
            return false;
        //Otherwise add this element to the BT
        }else{
            root = add(root,elem);
            nodeCount++;
            return true;
        }
    }

    //Private method to recursively add a value in the binary tree
    private Node add(Node node, T elem){
        //Base case: found a leaf node
        if(node==null){
            node = new Node(null,null,elem);
        }else{
            //Place lower element's value in the left subtree
            if(elem.compareTo(node.data)<0){
                node.left = add(node.left,elem);
            }else{
                node.right = add(node.right,elem);
            }
        }
        return node;
    }

    //Remove a value from this BT is it exists
    public boolean remove(T elem){
        if(contains(elem)){
            root = remove(root, elem);
            nodeCount--;
            return true;
        }
        return false;
    }

    private Node remove(Node node, T elem){
        if(node==null) return null;

        int cmp = elem.compareTo(node.data);

        if(cmp<0){
            node.left = remove(node.left, elem);
        }else if(cmp>0){
            node.right = remove(node.right, elem);
        }else{
            if(node.left==null){
                Node rightChild = node.right;

                node.data = null;
                node = null;

                return rightChild;
            }else if(node.right == null){
                Node leftChild = node.left;

                node.data = null;
                node = null;

                return leftChild;
            }else{
                Node tmp = digLeft(node.right);

                node.data = tmp.data;

                node.right = remove(node.right, tmp.data);
            }
        }
        return node;
    }

    //Method to find the leftmost node
    private Node digLeft(Node node){
        Node cur = node;
        while(cur.left!=null){
            cur = cur.left;
        }
        return cur;
    }

    //Returns true if the element exists in the BT
    public boolean contains(T elem){
        return contains(root, elem);
    }

    private boolean contains(Node node, T elem){
        //Base case: reached bottom, value not found
        if (node == null) return false;

        int cmp = elem.compareTo(node.data);

        if(cmp<0) return contains(node.left, elem);

        else if(cmp>0) return contains(node.right, elem);

        else return true;
    }

    //Computes the height of the tree, O(n)
    public int height(){
        return height(root);
    }

    private int height(Node node){
        if(node == null) return 0;
        return Math.max(height(node.left),height(node.right)+1);
    }
}

