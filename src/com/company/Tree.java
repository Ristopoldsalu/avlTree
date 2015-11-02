package com.company;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by mikeb on 10/27/2015.
 */
public class Tree {
    private Node root; // root of the tree

    public static void main(String[] args) {
        Tree tree = new Tree(); // create new tree
        // work with file and reader
        FileReader inputFile = null;
        try {
            inputFile = new FileReader("avl.in");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputFile);
        String input = null;
        // read first line
        try {
            input = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // split line into tokens
        String[] temp = input.split(" ");
        int i = 0;
        // parse tokens to Integers
        ArrayList<Integer> inputArray = new ArrayList<Integer>();
        while (i < temp.length) {
            inputArray.add(Integer.parseInt(temp[i]));
            i++;
        }
        // insert integers
        for (Integer k : inputArray) {
            tree.insert(k);
        }
        // read second line
        try {
            input = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // split into tokens
        temp = input.split(" ");
        // clear array for second use
        inputArray.clear();
        // parse tokens
        i = 0;
        if (input != null && !input.equals("")) {
            while (i < temp.length) {
                inputArray.add(Integer.parseInt(temp[i]));
                i++;
            }
            // delete values
            for (Integer k : inputArray) {
                tree.delete(k);
            }
        }
        // clear array
        inputArray.clear();
        // read next line
        try {
            input = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // split into tokens
        String output = ""; // string for printing
        if (input != null && !input.equals("")) {
            temp = input.split(" ");
            i = 0;
            // parse tokens
            while (i < temp.length) {
                inputArray.add(Integer.parseInt(temp[i]));
                i++;
            }
            Node node; // node for finding
            // search every value
            for (Integer k : inputArray) {
                node = tree.find(k);
                if (node == null) { // if not found - null
                    output += "null ";
                } else {
                    if (node.getRight() == null) { // if doesn't have right child - null
                        output += "null ";
                    } else { // else print right child value
                        output += node.getRight().getData() + " ";
                    }
                }

            }
        }

            // writing to file
            PrintWriter writer = null;
            try {
                writer = new PrintWriter("avl.out");
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer.print(output);
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                writer.close();
            }
    }

    /**
     * method inserts a value, if there is no such value in tree
     *
     * @param key value to insert
     */
    public void insert(int key) {
        if (root == null) { // if tree is empty, make value the root
            root = new Node(key);
            return;
        }
        Node temp = root; // reference to root to go through tree
        while (temp != null) { // iteratively looking for a place to incert the node
            if (temp.getData() == key) { // if such value already exists, do nothing
                return;
            } else if (key < temp.getData()) { // if value is less then current node, go to left  subtree
                if (temp.getLeft() == null) { // if there is no left subtree, then put value to left child
                    temp.setLeft(new Node(key, null, null, temp));
                    temp = temp.getLeft();
                    break;
                } else {  // go through subtree
                    temp = temp.getLeft();
                }
            } else { // else go to right subtree
                if (temp.getRight() == null) {
                    temp.setRight(new Node(key, null, null, temp));
                    temp = temp.getRight();
                    break;
                } else {
                    temp = temp.getRight();
                }
            }
        }
        restructure(temp); // restructure the tree from temp node
    }

    /**
     * restructures tree from given node
     * @param fromThis node to start with, goes from this to root by getting node parent's
     */
    public void restructure(Node fromThis) {
        Node temp = fromThis;
        while (temp != null) {
            balance(temp);
            temp = temp.getParent();
        }
    }

    /**
     * rebalance node, decides what rotation to make, if it's needed
     * @param node
     * @return new
     */
    public Node balance(Node node) {
        node.changeHeight(); // fix height of node
        if (node.countBalance() == 2) { // if height of right subtree is more than left by two
            if (node.getRight().countBalance() < 0) { // and right child is unbalanced
                node.setRight(rotateRight(node.getRight())); // make small-right rotation
            }
            return rotateLeft(node); // make left rotation
        }
        if (node.countBalance() == -2) { // if height of left subtree is more than right by two
            if (node.getLeft().countBalance() > 0) { // and left child is unbalanced
                node.setLeft(rotateLeft(node.getLeft())); // make small-left rotation
            }
            return rotateRight(node); // make right rotation
        }
        return node; // then rebalance is not needed
    }

    /**
     * method for right rotation of node
     * @param node
     * @return
     */
    public Node rotateRight(Node node) {
        Node temp = node.getLeft(); // temp variable to store left child of node which becomes root of subtree
        node.setLeft(temp.getRight());
        if (temp.getRight() != null) {
            temp.getRight().setParent(node);
        }
        temp.setRight(node); // node becomes right child of new root
        temp.setParent(node.getParent()); // make parent of node parent of temp
        if (node == root) { // if node is a root, then change root
            temp.setParent(null);
            root = temp;
        } else { // else fix node parent links
            if (node.getData() < node.getParent().getData()) {
                node.getParent().setLeft(temp);
            } else {
                node.getParent().setRight(temp);
            }
        }
        node.setParent(temp); // fix node parent
        node.changeHeight();
        temp.changeHeight();
        return temp; // return new subtree root
    }

    /**
     * makes left rotation for node
     * @param node
     * @return
     */
    public Node rotateLeft(Node node) {
        Node temp = node.getRight(); // temp variable for storing right child, which becomes root of this subtree
        node.setRight(temp.getLeft());
        if (temp.getLeft() != null) {
            temp.getLeft().setParent(node);
        }
        temp.setLeft(node); // node becomes the right child
        temp.setParent(node.getParent()); // change parent of new subtree root
        if (node == root) { // if it was a root make temp new root
            temp.setParent(null);
            root = temp;
        } else { // else fix links from parent to temp
            if (node.getData() < node.getParent().getData()) {
                node.getParent().setLeft(temp);
            } else {
                node.getParent().setRight(temp);
            }
        }
        node.setParent(temp); // change parent of node
        node.changeHeight();
        temp.changeHeight();
        return temp; // return new subtree root
    }

    /**
     * @param key value to delete  from tree
     * @return true if successfull, false if not
     */
    public boolean delete(int key) {
        Node temp = root; // reference to root
        while (temp != null) { // iteratively go through tree to find node for deletion
            if (temp.getData() == key) { // if match, break from cycle
                break;
            } else if (key < temp.getData()) { // if less, go to left subtree
                temp = temp.getLeft();
            } else { // else go to right subtree
                temp = temp.getRight();
            }
        }
        if (temp == null) { // if not found return false
            return false;
        }
        if (temp.getLeft() == null && temp.getRight() == null) { // case 1, no children
            if (temp.getData() < temp.getParent().getData()) { // if it's left child, remove left
                temp.getParent().setLeft(null);
            } else { // else remove right
                temp.getParent().setRight(null);
            }
        } else if (temp.getLeft() == null && temp.getRight() != null) { // case 2, has right child
            if (temp == root) {
                root = temp.getLeft();
                temp.setParent(null);
            } else if (temp.getData() < temp.getParent().getData()) {  // if it's a left node, remove left
                temp.getParent().setLeft(temp.getRight());
            } else { // else remove right
                temp.getParent().setRight(temp.getRight());
            }
            temp.getRight().setParent(temp.getParent()); // set new parent for node, which took deleted node place
        } else if (temp.getLeft() != null && temp.getRight() == null) { // case 3, has left child
            if (temp == root) {
                root = temp.getRight();
                temp.setParent(null);
            } else if (temp.getData() < temp.getParent().getData()) { // if it's a left node, remvoe left
                temp.getParent().setLeft(temp.getLeft());
            } else { // else remove right
                temp.getParent().setRight(temp.getLeft());
            }
            temp.getLeft().setParent(temp.getParent()); // set new parent for node, which took deleted node place
        } else { // case 4, 2 children
            Node predecessor = temp.getLeft(); // node to find predecessor
            while (predecessor.getRight() != null) { // iteratively get predecessor
                predecessor = predecessor.getRight();
            }
            // create new node with predecessor data, it will take position of deleted node
            temp = new Node(predecessor.getData(), temp.getLeft(), temp.getRight(), temp.getParent());
            predecessor.getParent().setRight(null); // remove predecessor from tree
            //error here for root node

            if (temp.getParent() == null) { // if it's root node
                root = temp;
                return true;
            }
            if (temp.getData() < temp.getParent().getData()) { // if it's not a root, nodify parent's references
                temp.getParent().setLeft(temp);
            } else {
                temp.getParent().setRight(temp);
            }
        }
        restructure(temp); // method to balance the tree
        return true; // if deleted
    }

    /**
     * @param key looks for a value in tree
     * @return returns a node, which contains this value or null if not exists
     */
    public Node find(int key) {
        Node temp = root; // reference to root
        while (temp != null) { // iteratively looking for node
            if (temp.getData() == key) { // if match return node
                return temp;
            } else if (key < temp.getData()) { // if less then current, go to left subtree
                temp = temp.getLeft();
            } else { // else go to right
                temp = temp.getRight();
            }
        }
        return null; // if not found
    }

    /**
     * method for returning height of node and 0 if node is null
     * @param node
     * @return
     */
    public int nodeHeight(Node node) {
        if (node != null) {
            return node.height;
        } else {
            return 0;
        }
    }

    private class Node {
        private int data; // data stored in node
        private Node left; // left child
        private Node right; // right child
        private Node parent; // parent node
        private int height; // height of node

        // constructor for adding only data to node
        public Node(int data) {
            this.data = data;
            this.height = 1;
        }

        // full constructor
        public Node(int data, Node left, Node right, Node parent) {
            this.data = data;
            this.left = left;
            this.right = right;
            this.parent = parent;
            this.height = 1;
        }

        /**
         * recalculates height of node
         */
        public void changeHeight() {
            if (nodeHeight(getLeft()) > nodeHeight(getRight())) {
                height = nodeHeight(getLeft()) + 1;
            } else {
                height = nodeHeight(getRight()) + 1;
            }
        }

        public int countBalance() {
            return nodeHeight(getRight()) - nodeHeight(getLeft());
        }

        public int getData() {
            return data;
        }

        public void setData(int data) {
            this.data = data;
        }

        public Node getLeft() {
            return left;
        }

        public void setLeft(Node left) {
            this.left = left;
        }

        public Node getRight() {
            return right;
        }

        public void setRight(Node right) {
            this.right = right;
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }
    }
}
