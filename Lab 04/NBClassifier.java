import java.io.*;
import java.util.*;
import java.nio.file.*;

public class NBClassifier {

   private ArrayList<String> filesData = new ArrayList<String>();
   private ArrayList<String> labelData = new ArrayList<String>();
   private ArrayList<String> stopWords = new ArrayList<String>();
   private HashMap<String, HashMap<String, Double>> wordDict = new HashMap<String, HashMap<String, Double>>();
   private int posCount = 0, negCount = 0, positiveCounter = 0, negativeCounter = 0; 
   private double priorPositiveProb = 0.0;
   private double priorNegativeProb = 0.0;
   private ArrayList<String> testFilesData = new ArrayList<String>();
   
	/**
	 * Build a Naive Bayes classifier using a training document set
	 * @param trainDataFolder the training document folder
	 */
	public NBClassifier(String trainDataFolder) throws Exception
	{
		preprocess(trainDataFolder);
      computeProb();
	}
   
   /**
	 * getsFiles from folder and loads it into trainFiles or testFiles ArrayList
	 * @param dataFolder the documents folder
    * @param internalFolderName test/train folder
    * @processOption train/test
	 */
   public int getFiles(String dataFolder, String internalFolderName, String processOption) throws Exception{
      int counter = 0;
      File folder = new File(dataFolder+"/"+internalFolderName);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
		    if (file.isFile()) {
					String fileData = readFileAsString(file.toString()).toLowerCase();
               if(processOption.equals("train")){
               filesData.add(fileData);
               if(internalFolderName.equals("pos")){
                  labelData.add("pos");
               }
               else if(internalFolderName.equals("neg")) {
                  labelData.add("neg");
               }
               }
               else if(processOption.equals("test")){
                  testFilesData.add(fileData);
               }
               ++counter;
				}
		}
      return counter;
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
            stopWords.add(word);          
         }

      } catch(Exception e){
            e.printStackTrace();
      }
   }
   
   /**
   * Reads in the text in each document and perform tokenization. 
   * Removes all the stop words in the documents.
   *
   * @param delimiters - contains punctuations, some symbols and spaces.
   */
   private void tokenize(String delimiters){
      int pCount = 0, nCount = 0;
      for (int index = 0; index < filesData.size(); index++){
         String[] terms = filesData.get(index).split(delimiters);
         ArrayList<String> instance = new ArrayList<String>();         
         for (String term: terms){ 
            //Adding to TermList
            if(stopWords.indexOf(term.trim()) == -1){
               instance.add(term.trim());
            }
         }
        addToDict(instance, index);

         
            if(labelData.get(index).equals("pos")){
                  ++pCount;
                  positiveCounter+= instance.size();
            }
            else if(labelData.get(index).equals("neg")){
                  ++nCount;
                  negativeCounter+=instance.size();
            }
            
      }
     }

   public double probability(double m, double n) {
    long a = (long) m;
    long b = (long) n;
    return (double)((a) + 1) / ( (b) + wordDict.size());
   }
   
   //creates hashmap for words with pos and neg frequencies of each term in an instance
   private void addToDict(ArrayList<String> instance, int index){
      String label = labelData.get(index);
      for (String word : instance){
         if(!word.isEmpty()){
         if(wordDict.containsKey(word)){
            HashMap<String, Double> content = wordDict.get(word);
            double positiveCount = content.get("positiveCount");
            double negativeCount = content.get("negativeCount");
            if(label.equals("pos")){
               positiveCount += 1;
               content.put("positiveCount",positiveCount);
            }
            else if (label.equals("neg")){
               negativeCount += 1;
               content.put("negativeCount",negativeCount);
            }
            wordDict.put(word, content);
         }
         else{
            HashMap<String, Double> content = new HashMap<String, Double>();
            if(label.equals("pos")){
               content.put("positiveCount",1.0);
               content.put("negativeCount",0.0);
            }
            else if (label.equals("neg")){
               content.put("positiveCount",0.0);
               content.put("negativeCount",1.0);
            }
            wordDict.put(word, content);
         }
         }
      }
   }
   
   // iterates through hashmap to determine positive and negative Prob of each term. 
   private void computeProb(){
      Iterator it = wordDict.entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry<String, HashMap<String, Double>> pair = (Map.Entry)it.next();
        String term = pair.getKey();
        HashMap<String, Double> content = pair.getValue();
        double positiveProb = probability(content.get("positiveCount"), positiveCounter);
        double negativeProb = probability(content.get("negativeCount"), negativeCounter);
        content.put("positiveProb",positiveProb);
        content.put("negativeProb",negativeProb);
        wordDict.put(term, content);
      }
   }
   
   //tokenization for test case(s)
   public ArrayList<String> tokenizeTest(String delimiters, String doc){
         String[] terms = doc.split(delimiters);
         ArrayList<String> instance = new ArrayList<String>();         
         for (String term: terms){ 
            if(stopWords.indexOf(term.trim()) == -1){
               instance.add(term.trim());
            }
         }  
         return instance;    
   }
   
   
	/**
	 * Classify a test doc
	 * @param doc test doc
	 * @return class label
	 */
	public int classify(String doc){
		ArrayList<String> tokenizedDoc = tokenizeTest("[\" ()_,?:;%&-]+" , doc);
      double posProb = 0.0;
      double negProb = 0.0;
      for (String term : tokenizedDoc){
         if(wordDict.containsKey(term)){
            HashMap<String, Double> content = wordDict.get(term);
            double positiveProb = content.get("positiveProb");
            double negativeProb = content.get("negativeProb");
            posProb+=Math.log10(positiveProb);
            negProb+=Math.log10(negativeProb);
         }
         else{
            posProb+=Math.log10((double)1/(positiveCounter + wordDict.size()));
            negProb+=Math.log10((double)1/(negativeCounter + wordDict.size()));
         }
      }
      posProb+=Math.log10(priorPositiveProb);
      negProb+=Math.log10(priorNegativeProb);
      
      return posProb > negProb ? 1 : 0;
	}

	/**
	 * Load the training documents
	 * @param trainDataFolder
	 */
	public void preprocess(String trainDataFolder) throws Exception
	{
      posCount = getFiles(trainDataFolder, "pos", "train");
      negCount = getFiles(trainDataFolder, "neg", "train");
      
      priorPositiveProb = (double)posCount / (posCount+negCount);
      priorNegativeProb = (double)negCount / (posCount+negCount);
            
      getStopWords("stopwords_stanford.txt");
      this.tokenize("[\" ()_,?:;%&-]+");
	}

	/**
	 *  Classify a set of testing documents and report the accuracy
	 * @param testDataFolder fold that contains the testing documents
	 * @return classification accuracy
	 */
	public double classifyAll(String testDataFolder) throws Exception
	{
		int totalCount = getFiles(testDataFolder,"pos","test");
      totalCount += getFiles(testDataFolder,"neg","test");
      int index = 0, correctClassify = 0;
      for( String doc : testFilesData){
         int label = classify(doc);
         index+=1;
         if(index<100){
            if(label == 1){
               correctClassify+=1;
            }
         }
         if(index>100){
            if(label == 0){
               correctClassify+=1;
            }
         }
      }
      System.out.println("Correctly Classified Instances: "+correctClassify+" / "+totalCount);
      return (double)correctClassify/totalCount*100;
	}

   //reads a file as string
	private static String readFileAsString(String fileName) throws Exception {
    String fileData = "";
    fileData = new String(Files.readAllBytes(Paths.get(fileName)));
    fileData = fileData.toLowerCase();
    return fileData;
   }

	public static void main(String[] args) throws Exception
	{
		NBClassifier cl = new NBClassifier("data/train");
      
      System.out.println("-------------FOR CLASSIFY_ALL TEST DOCS FROM TEST FOLDER ---------------");
      System.out.println("Classification Accuracy - "+cl.classifyAll("data/test"));
      
      System.out.println("-------------FOR CLASSIFYING SOME RANDOM DOCS FROM TEST FOLDER (DOCS 0-199) ---------------");
      int randomIndex = new Random().nextInt(cl.testFilesData.size());
      System.out.println("Doc Index : "+ randomIndex + " - " + (cl.classify(cl.testFilesData.get(randomIndex))==1 ? "Positive" : "Negative"));
      
      randomIndex = new Random().nextInt(cl.testFilesData.size());
      System.out.println("Doc Index : "+ randomIndex + " - " + (cl.classify(cl.testFilesData.get(randomIndex))==1 ? "Positive" : "Negative"));

   }
}
