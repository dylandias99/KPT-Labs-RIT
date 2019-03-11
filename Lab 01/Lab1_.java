import java.util.*;
import java.io.*;
import java.nio.file.*;

/**
 * Lab 01 : Knowlede Processing Techniques [KPT]
 * @author Nirbhay Ashok Pherwani
 * 
 * Email: np5318@rit.edu
 */
 
public class Lab1_{

   private static ArrayList<String> docs = new ArrayList<String>();
   private static HashMap<String, Object> stopWords = new HashMap<String, Object>();
   private static HashMap<String, ArrayList<Integer>> invertedIndex = new HashMap<String, ArrayList<Integer>>();
   private static Set<String> termList = new HashSet<String>();
   private static ArrayList<String> elementName = new ArrayList<String>();
   private static ArrayList<Integer> elementsSize = new ArrayList<Integer>();
   private static HashMap<Integer, HashMap<String,String>> docMap = new HashMap<Integer,HashMap<String,String>>();         

   
   /**
   * Reads one entire file as a string.
   *
   * @param fileName - contains the path to the file
   * @return fileData - content of file as a String
   */
   private static String readFileAsString(String fileName) throws Exception { 
    String fileData = ""; 
    fileData = new String(Files.readAllBytes(Paths.get(fileName)));
    fileData = fileData.toLowerCase(); 
    return fileData; 
   } 
   
   /**
   * Adds file data to the docs ArrayList, 
   * thus assigning a unique id to each document
   *
   * @param index - contains the id where the document should be added
   * @param fileData - content of a file as a String
   */
   private static void addToDocList(int index, String fileData){
      docs.add(index, fileData);
   }
   
   /**
   * Retrieves stop words and stores them in the stopWords ArrayList
   *
   * @param fileName - contains the path to the stopwords file
   */  
   private void getStopWords(String fileName){
     
      try {
         String lines="";
         File fileData = new File(fileName);
         Scanner sc = new Scanner(fileData);
         while(sc.hasNextLine()){
           lines+=sc.nextLine().toLowerCase()+ " ";
         }
         String words[] = lines.split("[ ']+");
         for(String word : words){
            stopWords.put(word,"");            
         }

      } catch(Exception e){
            e.printStackTrace();
      }
   }
   
   /**
   * Reads in the text in each document and perform tokenization. 
   * Removes all the stop words in the documents.
   * Performs Stemming.
   *
   * @param delimiters - contains punctuations, some symbols and spaces.
   */
   private static void tokenize(String delimiters){
      for (int index = 0; index < docs.size(); index++){
         String[] terms = docs.get(index).split(delimiters);
         HashMap<String, String> termMap = new HashMap<String,String>();         
         for (String term: terms){ 
            //Adding to TermList
            if(!(stopWords.containsKey(term.trim()))){
               //Stemming
               Stemmer stemmer = new Stemmer();
               stemmer.add(term.trim().toCharArray(), term.trim().length());
               stemmer.stem();
               termList.add(stemmer.toString());
               termMap.put(stemmer.toString(),"");
            }
         }
         docMap.put(index, termMap);
      }
      }
  
   
   /**
   * Creates the Inverted Index
   */   
   private static void createInvertedIndex(){
      for(String term : termList){
         ArrayList<Integer> tempList = new ArrayList<Integer>();
         for(int index=0; index<docs.size(); index++){
           
           if(docMap.get(index).containsKey(term)){
               tempList.add(index+1);
           }   
         }
         if(tempList.size()>0)
            invertedIndex.put(term, tempList);
         
         //uncomment to print inverted index.
         //System.out.println(term + " - "+ tempList);
      }
   }
 
   /**
   * Search algorithm that can handle a query with a single keyword 
   *
   * @param keyword - the term to be searched.
   */
   private static void search_1(String keyword){
      if(invertedIndex.containsKey(keyword)){
            ArrayList<Integer> searchResult = invertedIndex.get(keyword);
            System.out.println("'"+keyword +"' is present in document(s) " + searchResult);
      }
      else{
            System.out.println("'"+keyword +"' not found ");
      }
   }
   
   /**
   * Search algorithm that can handle a query with two keywords.
   * Assuming that the keywords are connected using the OR operator.
   *
   * @param searchString - the string containing terms to be searched.
   */
   private static void search_2_OR(String searchString){
   
    try{
      String term_1 = searchString.split(" ")[0];
      String term_2 = searchString.split(" ")[1];
      ArrayList<Integer> searchResult1 = new ArrayList<Integer>();
      ArrayList<Integer> searchResult2 = new ArrayList<Integer>();
      if(invertedIndex.containsKey(term_1) && invertedIndex.containsKey(term_2)){
            searchResult1 = invertedIndex.get(term_1);
            searchResult2 = invertedIndex.get(term_2);
      }
      else{
            System.out.println("'"+searchString +"' not found ");
      }
      Integer index1=0,index2=0;
      ArrayList<Integer> commonDocuments = new ArrayList<Integer>();
      while(index1<searchResult1.size() && index2<searchResult2.size()){
            if(searchResult1.get(index1)==searchResult2.get(index2)){
               commonDocuments.add(searchResult1.get(index1));
               index1++;index2++;
             }else if(searchResult1.get(index1) < searchResult2.get(index2)){
                  commonDocuments.add(searchResult1.get(index1));
                  index1++;
             }else{
                  commonDocuments.add(searchResult2.get(index2));
                  index2++;
            }
         }
      while(index1 < searchResult1.size()){
                           commonDocuments.add(searchResult1.get(index1));
                           index1++;
      }
      while(index2 < searchResult2.size()){
                           commonDocuments.add(searchResult2.get(index2));
                           index2++;
      }
      
      System.out.println("'"+searchString +"' found in document(s) "+commonDocuments);
     }
     catch (Exception e){
      e.printStackTrace();
      System.out.println("Invalid Input may have been given");
     }
      
   }
   
   /**
   * Search algorithm that can handle a query with two keywords.
   * Assuming that the keywords are connected using the AND operator.
   *
   * @param searchString - the string containing terms to be searched.
   */
   private static void search_2_AND(String searchString){
     try{
      String term_1 = searchString.split(" ")[0];
      String term_2 = searchString.split(" ")[1];
      if(invertedIndex.containsKey(term_1) && invertedIndex.containsKey(term_2)){
         int size_1 = invertedIndex.get(term_1).size();
         int size_2 = invertedIndex.get(term_2).size();
         ArrayList<Integer> first = new ArrayList<Integer>();
         ArrayList<Integer> second = new ArrayList<Integer>();
         String firstTerm, secondTerm;
         if(size_1 >= size_2){
            first = invertedIndex.get(term_1);
            firstTerm = term_1;
            second = invertedIndex.get(term_2);
            secondTerm= term_2;
         }
         else{
            firstTerm = term_2;
            first = invertedIndex.get(term_2);
            secondTerm = term_1;
            second = invertedIndex.get(term_1);
         }
         Integer index1=0,index2=0;
         ArrayList<Integer> intersection = new ArrayList<Integer>();
         while(index1<first.size() && index2<second.size()){
            if(first.get(index1)==second.get(index2)){
               intersection.add(first.get(index1));
               index1++;index2++;
             }else if(first.get(index1) < second.get(index2)){
                  index1++;
             }else{
                  index2++;
            }
         }
         System.out.println("'"+searchString+"' present in document(s) "+intersection);
      }
     }
     catch (Exception e){
      e.printStackTrace();
      System.out.println("Invalid Input may have been given");
     }
      
   }
   
   /**
   * Sort method to arrange terms in the order of size of postings List.
   *
   * @param elementName - ArrayList containing the terms under consideration
   * @param elementsSize - ArrayList containing the respective posting's List size of the term.
   */
   private static void sort(ArrayList<String> elementName, ArrayList<Integer> elementsSize){
         int n = elementsSize.size(); 
  
        // One by one move boundary of unsorted subarray 
        for (int i = 0; i < n-1; i++) 
        { 
            // Find the minimum element in unsorted array 
            int min_idx = i; 
            for (int j = i+1; j < n; j++) 
                if (elementsSize.get(j) < elementsSize.get(min_idx)) 
                    min_idx = j; 
  
            // Swap the found minimum element with the first 
            // element 
            int temp = elementsSize.get(min_idx); 
            elementsSize.set(min_idx, elementsSize.get(i)); 
            elementsSize.set(i, temp);
            String temp2 = elementName.get(min_idx); 
            elementName.set(min_idx, elementName.get(i)); 
            elementName.set(i, temp2);
        } 
      }
   
      /**
      * Search algorithm that can handle a query with three or more keywords.
      * Assuming that the keywords are connected using the AND operator.
      *
      * @param searchString - the string containing terms to be searched.
      */
      private static void search_3_AND_MORE(String searchString){
         try{
          
           elementName = new ArrayList <String>();
           elementsSize = new ArrayList <Integer>();
           String searchTerms_[] = searchString.split(" ");
           ArrayList<String> searchTerms = new ArrayList<String>();
            Stemmer stemmer = new Stemmer();
            for(String token:searchTerms_) {
               stemmer.add(token.toCharArray(), token.length());
               stemmer.stem();
               searchTerms.add(stemmer.toString());
               stemmer = new Stemmer();
             }
            
           int continueFlag = 0;
           for(String term : searchTerms){
            if(!invertedIndex.containsKey(term)){
               continueFlag=1;
               System.out.println("'"+searchString+"' not found");
               break;
            }
            else{
               elementName.add(term);
               elementsSize.add(invertedIndex.get(term).size());
            }
           }
           /* After performing the sort function, initializes the smallest array to 
           result and inspects it with every next array of postings List. 
           Removes the element from the result ArrayList if fails to find it 
           in any one of the postings List under consideration.
           */
           
           if(continueFlag==0 && searchTerms.size() >=3 ){
            sort(elementName, elementsSize);
            ArrayList<Integer> result = new ArrayList<Integer>();
            result = invertedIndex.get(elementName.get(0));
            for(int index = 1; index<elementName.size(); index++){
                  int removeFlag=1;
                  for(int resultIndex = 0; resultIndex < result.size(); resultIndex++){
                     for (int i : invertedIndex.get(elementName.get(index))){
                        if(result.get(resultIndex) == i){
                           removeFlag = 0;
                        }
                     }
                  if(removeFlag==1)
                     result.remove(resultIndex); 
                  }
            }
           if(result.size()>0){
           System.out.println("Order of search: "+ elementName);
           System.out.println("'"+ searchString + "' found in document(s) "+ result);
           System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++");
           }
           else{
           System.out.println(searchString + " not found ");
           }
           }
           }
          catch (Exception e){
            e.printStackTrace();
            System.out.println("Invalid Input may have been given");
          }
      }

      public static void main (String args[]) throws Exception{
      String[] docNames={"cv000_29416.txt","cv001_19502.txt","cv002_17424.txt","cv003_12683.txt","cv004_12641.txt"};
      String data;
      int index = 0;
      for (String fileName : docNames){
            String fileData = readFileAsString("Lab1_Data/"+fileName); 
            addToDocList(index++, fileData);
      }
      String stopWordsFileName = "stopwords.txt";
      new Lab1_().getStopWords(stopWordsFileName);
      tokenize("[ '.,&#?!:;$%+()\\-\\/*\"]+"); 
      createInvertedIndex();
      for(int i = 0; i<docNames.length; i++){
         System.out.println(docNames[i]+" - Document Id: - "+(i+1)); 
      }
      System.out.println("\n--------------1-KEYWORD-SEARCH------------------");
      search_1("hair");
      search_1("spend");
      System.out.println("\n------------2-KEYWORD-SEARCH-AND----------------");
      search_2_AND("car frequent");
      search_2_AND("jade hero");        
      System.out.println("\n------------2-KEYWORD-SEARCH-OR----------------");
      search_2_OR("studio film");
      search_2_OR("person hair");
      System.out.println("\n---------3-AND-MORE KEYWORD-SEARCH-AND---------");
      search_3_AND_MORE("find plot mind");
      search_3_AND_MORE("error line steal person");    
   }
}
 
 