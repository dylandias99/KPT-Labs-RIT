import java.util.*;

public class BTreeIndex {
	String[] myDocs;
	BinaryTree termList;
	BTNode root;

	/**
	 * Construct binary search tree to store the term dictionary
	 * @param docs List of input strings
	 *
	 */
	public BTreeIndex(String[] docs)
	{
		//TO BE COMPLETED
		ArrayList<String> terms = new ArrayList<String>();
		myDocs = docs;
		for (String doc : docs){
			String words[] = doc.split(" ");
			for(String word : words){
				if(terms.indexOf(word)==-1){
					terms.add(word);
				}
			}
		}
    Collections.sort(terms);
    System.out.println("\n----------- Terms printed in sorted order -------------------");
    System.out.println(terms);
    System.out.println("----------- Terms display ends -------------------\n");

    termList = new BinaryTree();

    root = makeBalancedTree(terms, 0, terms.size()-1);
    System.out.println("\n----------- Result for print in order -------------------");
		termList.printInOrder(root);
    System.out.println("----------- Result for print in order ends -------------------\n");

    System.out.println("\n----------- Result for pretty print balanced binary tree -------------------");
    termList.printBinaryTree(root, 0);
    System.out.println("----------- Result for pretty print balanced binary tree ends -------------------\n");

	}
 
  public BTNode makeBalancedTree(ArrayList<String> terms, int start, int end){
        /* Base Case */
        if (start > end) { 
            return null; 
        } 
        /* Get the middle element and make it root */
        int mid = (start + end) / 2; 
        ArrayList<Integer> docList = new ArrayList<Integer>();
			  BTNode node = new BTNode(terms.get(mid), docList);
			  int docCounter = 0;
			  for(String doc: myDocs){
				  if(doc.indexOf(terms.get(mid))!=-1){
					node.docLists.add(docCounter);
				  }
				  ++docCounter;
			  }
        
        termList.add(null, node);
        /* Recursively construct the left subtree and make it 
         left child of root */
        BTNode left = makeBalancedTree(terms, start, mid - 1);
        if(left!=null){
        termList.add(node , left); 
        }
        /* Recursively construct the right subtree and make it 
         right child of root */
        BTNode right = makeBalancedTree(terms, mid + 1, end);
        if(right!=null){
        termList.add(node , right); 
        }
        return node;
      }

	/**
	 * Single keyword search
	 * @param query the query string
	 * @return doclists that contain the term
	 */
	public ArrayList<Integer> search(String query)
	{
			BTNode node = termList.search(root, query);
			if(node==null)
				return null;
			return node.docLists;
	}

	/**
	 * conjunctive query search
	 * @param query the set of query terms
	 * @return doclists that contain all the query terms
	 */
	public ArrayList<Integer> search(String[] query)
	{
		ArrayList<Integer> result = search(query[0]);
		int termId = 1;
		while(termId<query.length)
		{
			ArrayList<Integer> result1 = search(query[termId]);
			result = merge(result,result1);
			termId++;
		}
		return result;
	}

	/**
	 *
	 * @param wildcard the wildcard query, e.g., ho (so that home can be located)
	 * @return a list of ids of documents that contain terms matching the wild card
	 */
	public ArrayList<Integer> wildCardSearch(String wildcard)
	{
		//TO BE COMPLETED
    ArrayList<Integer> result = new ArrayList<Integer>();
    ArrayList<BTNode> results = termList.wildCardSearch(root, wildcard, new ArrayList<BTNode>());
    /* To see the words in which wildcard appeared!
    for(BTNode node : results){
      System.out.println(node.term);
    }
    */
    if(results.size()>0){
    BTNode start = results.get(0);
    result = start.docLists;
    if(results.size()>1){
      for(BTNode node : results){
        result = union(result, node.docLists);
      }
     }
    }
    return result;
    
	}

  public ArrayList<Integer> union(ArrayList<Integer> arr1, ArrayList<Integer> arr2){
        ArrayList<Integer> result = new ArrayList<Integer>();
        int m = arr1.size();
        int n = arr2.size();
        int i = 0, j = 0; 
      while (i < m && j < n) 
      { 
        if (arr1.get(i) < arr2.get(j)) 
          result.add(arr1.get(i++));
        else if (arr2.get(j) < arr1.get(i)) 
          result.add(arr2.get(j++)); 
        else
        { 
          result.add(arr2.get(j++)); 
          i++; 
        } 
      } 
       
      /* Print remaining elements of  
         the larger array */
      while(i < m) 
       result.add(arr1.get(i++)); 
      while(j < n) 
       result.add(arr2.get(j++)); 
         
    return result;
  }



	public ArrayList<Integer> merge(ArrayList<Integer> l1, ArrayList<Integer> l2)
	{
		ArrayList<Integer> mergedList = new ArrayList<Integer>();
		int id1 = 0, id2=0;
		while(id1<l1.size()&&id2<l2.size()){
			if(l1.get(id1).intValue()==l2.get(id2).intValue()){
				mergedList.add(l1.get(id1));
				id1++;
				id2++;
			}
			else if(l1.get(id1)<l2.get(id2))
				id1++;
			else
				id2++;
		}
		return mergedList;
	}

	/**
	 * Test cases
	 * @param args commandline input
	 */
	public static void main (String[]args)
	{
		String[] docs = {"nlp text warehousing newover big data",
                       "dimensional data nlp warehouse over big nlp data",
                       "nlp before text nlp mining news go",
                       "nlp before text classification newark warade"};
		//TO BE COMPLETED with testcases
		BTreeIndex bt = new BTreeIndex(docs);
    System.out.println("\n----------- Results for single term search -------------------");
    System.out.println("nlp: " + bt.search("nlp"));
    System.out.println("data: " + bt.search("data"));
    System.out.println("text: " + bt.search("text"));
    System.out.println("----------- Results for single term search ends -------------------\n");

    System.out.println("\n----------- Results for conjunctive terms search -------------------");
    System.out.println("nlp before text: "+bt.search(new String[]{"nlp", "before", "text"}));
    System.out.println("newover big data: "+bt.search(new String[]{"newover", "big", "data"}));
    System.out.println("----------- Results for conjunctive terms search ends -------------------\n");

    System.out.println("\n----------- Results for wildcard search -------------------");
    System.out.println("new: "+bt.wildCardSearch("new"));
    System.out.println("war: "+bt.wildCardSearch("war"));
    System.out.println("b: "+bt.wildCardSearch("b"));
    System.out.println("----------- Results for wildcard search ends -------------------\n");
	}
}
