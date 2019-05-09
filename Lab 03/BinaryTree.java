import java.util.*;

/**
 *
 * @author Nirbhay Pherwani Kknowledge Processing Techniques Lab 3 a node in a
 *         binary search tree
 */
class BTNode {
  BTNode left, right;
  String term;
  ArrayList<Integer> docLists;

  /**
   * Create a tree node using a term and a document list
   * 
   * @param term    the term in the node
   * @param docList the ids of the documents that contain the term
   */
  public BTNode(String term, ArrayList<Integer> docList) {
    this.term = term;
    this.docLists = docList;
  }
}

/**
 *
 * Binary search tree structure to store the term dictionary
 */
public class BinaryTree {
  BinaryTree() {

  }

  /**
   * insert a node to a subtree
   * 
   * @param node  root node of a subtree
   * @param iNode the node to be inserted into the subtree
   */
  public void add(BTNode node, BTNode iNode) {
    // TO BE COMPLETED
    BTNode pointer = node;
    BTNode trail = null;

    while (pointer != null) {
      trail = pointer;
      if (iNode.term.compareTo(pointer.term) < 0) {
        pointer = pointer.left;
      } else {
        pointer = pointer.right;
      }
    }

    if (trail == null) {
      trail = node;
    }

    else if (iNode.term.compareTo(trail.term) < 0) {
      trail.left = iNode;
    }

    else {
      trail.right = iNode;
    }
  }

  /**
   * Search a term in a subtree
   * 
   * @param n   root node of a subtree
   * @param key a query term
   * @return tree nodes with term that match the query term or null if no match
   */
  public BTNode search(BTNode n, String key) {
    // TO BE COMPLETED
    BTNode pointer = n;
    while (pointer != null) {
      if (pointer.term.equals(key)) {
        return pointer;
      } else {
        if (pointer.term.compareTo(key) > 0) {
          pointer = pointer.left;
        } else {
          pointer = pointer.right;
        }
      }
    }
    return null;
  }

  /**
   * Do a wildcard search in a subtree
   * 
   * @param n   the root node of a subtree
   * @param key a wild card term, e.g., ho (terms like home will be returned)
   * @return tree nodes that match the wild card
   */
  public ArrayList<BTNode> wildCardSearch(BTNode n, String key, ArrayList<BTNode> result) {
    if (n == null) {
      return result;
    }
    if(n.term.startsWith(key)){
    wildCardSearch(n.left,key, result);
    if (n.term.startsWith(key)) {
        result.add(n);
    }
    wildCardSearch(n.right,key, result);
    return result;
    }
    else{
      if(n.term.compareTo(key)<0){
           wildCardSearch(n.right,key, result);
      }
      else{
          wildCardSearch(n.left,key, result);
      }
      return result;
    }
  }

  /**
   * Print the inverted index based on the increasing order of the terms in a
   * subtree
   * 
   * @param node the root node of the subtree
   */
  public void printInOrder(BTNode node) {
    if (node == null) {
      return;
    }
    printInOrder(node.left);
    System.out.println(node.term + " " + node.docLists);
    printInOrder(node.right);
    // TO BE COMPLETED
  }

  public static void printBinaryTree(BTNode root, int level){
    if(root==null)
         return;
    printBinaryTree(root.right, level+1);
    if(level!=0){
        for(int i=0;i<level-1;i++)
            System.out.print("|\t");
            System.out.println("|-------"+root.term);
    }
    else
        System.out.println(root.term);
    printBinaryTree(root.left, level+1);
}    
}
