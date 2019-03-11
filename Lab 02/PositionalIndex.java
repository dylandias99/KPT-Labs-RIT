import java.util.ArrayList;
/**
 * ISTE-612-2185 Lab #2
 * Nirbhay Pherwani
 * Febraury 27, 2019
 * Email: np5318@rit.edu
 */

public class PositionalIndex {
	String[] myDocs;
	ArrayList<String> termDictionary;                  
	ArrayList<ArrayList<Doc>> docLists;
	
	/**
	 * Construct a positional index 
	 * @param docs List of input strings or file names
	 * 
	 */
	public PositionalIndex(String[] docs)
	{
		//TASK1: TO BE COMPLETED
		docLists = new ArrayList<ArrayList<Doc>>();
		termDictionary = new ArrayList<String>();
		ArrayList<Doc> docList;
		myDocs = docs;
		termDictionary = new ArrayList<String>();
		for(int docuId=0; docuId<myDocs.length; docuId++){
		    String words[] = myDocs[docuId].split(" ");
			for(int wordIndex = 0; wordIndex<words.length; wordIndex++){
				boolean foundTerm = false;
				if(!termDictionary.contains(words[wordIndex])){
					termDictionary.add(words[wordIndex]);	
					docList = new ArrayList<Doc>();
					Doc doc = new Doc(docuId,wordIndex);
					docList.add(doc);
					docLists.add(docList);
				}
				else{
					int index = termDictionary.indexOf(words[wordIndex]);
					docList = docLists.get(index);
					int k=0;
					for(Doc did:docList) {
					if(did.docId == docuId) {
                     did.insertPosition(wordIndex);
                     docList.set(k, did);
                     foundTerm = true;
                     break;
					}
					k++;                 
					}
					if(!foundTerm) {
						Doc doc = new Doc(docuId,wordIndex);
						docList.add(doc);
					}
				}
			}
		    
		}
	}
	
	/**
	 * Return the string representation of a positional index
	 */
	public String toString()
	{
		String matrixString = new String();
		ArrayList<Doc> docList;
		for(int i=0;i<termDictionary.size();i++){
				matrixString += String.format("%-15s", termDictionary.get(i));
				docList = docLists.get(i);
				for(int j=0;j<docList.size();j++)
				{
					matrixString += docList.get(j)+ "\t";
				}
				matrixString += "\n";
			}
		return matrixString;
	}
	
	/**
	 * 
	 * @param post1 first postings
	 * @param post2 second postings
	 * @return merged result of two postings
	 */
	public ArrayList<Doc> intersect(ArrayList<Doc> post1, ArrayList<Doc> post2, int sub)
	{
		//TASK2: TO BE COMPLETED
		ArrayList<Doc> intersectList = new ArrayList<Doc>();
		int index1 = 0, index2 =0;
		while(index1<post1.size() && index2<post2.size()){
			if(post1.get(index1).docId == post2.get(index2).docId){
				ArrayList<Integer> list1 = post1.get(index1).positionList;
				ArrayList<Integer> list2 = post2.get(index2).positionList;
				int iter1 = 0;
				while(iter1 < list1.size()){
					int iter2 = 0;
					while(iter2 < list2.size()){
						if((list1.get(iter1) - list2.get(iter2)) == sub){
							boolean match = false;
							int j = 0;
							for(Doc docu : intersectList){
								if(docu.docId == post1.get(index1).docId){
									Doc item = intersectList.get(j);
									list1.get(iter1);
									item.insertPosition(list1.get(iter1));
									intersectList.set(j, item);
									match=true;
									break;
								}
								j++;
							}
							if(!match){
								Doc doc = new Doc(post1.get(index1).docId, list1.get(iter1));
								intersectList.add(doc);
							}
						}
						++iter2;
					}
					++iter1;
				}
				index1++;
				index2++;
			}
			else if(post1.get(index1).docId < post2.get(index2).docId){
				index1++;
			}
			else {
				index2++;
			}
		}
		return intersectList;
	}
	
	/**
	 * 
	 * @param query a phrase query that consists of any number of terms in the sequential order
	 * @return ids of documents that contain the phrase
	 */
	public ArrayList<Doc> phraseQuery(String[] query)
	{
		//TASK3: TO BE COMPLETED
		String word = query[0];
		ArrayList <Doc> empty = new ArrayList<Doc>();
		ArrayList <Doc> post1,post2;
		int index = termDictionary.indexOf(word);
		if(index!=-1){
			post1 = docLists.get(index);
			int sub=-1;
			for(int i=1; i<query.length;i++){
			int index2 = termDictionary.indexOf(query[i]);
			if(index2==-1){
				return empty;
			}
			post2 = docLists.get(index2);
			post1 = intersect(post1, post2, sub);
			--sub;
			}
			return post1;
		}
		else {
			return empty;
		}
	}

	/**
	 * @param searchString contains the string to be searched. 
	 */
	public void search(String searchString){
		ArrayList<Doc> searchResult = this.phraseQuery(searchString.split(" "));
		System.out.print(searchString+"\t");
		if(searchResult.size()==0){
			System.out.print("NOT FOUND");
		}
		for(Doc doc : searchResult){
			System.out.print(doc + "\t");
		}
		System.out.println();
	}
	
	public static void main(String[] args)
	{
      String[] docs = {"data text warehousing over big data",
                       "dimensional data warehouse over big data",
                       "nlp before text mining",
                       "nlp before text classification"};
                       
		PositionalIndex pi = new PositionalIndex(docs);
		System.out.print(pi);
		//TASK4: TO BE COMPLETED: design and test phrase queries with 2-5 terms
		System.out.println("\n------------------PHRASE QUERY TESTS ---------------------\n");
		pi.search("data warehouse");
		pi.search("nlp before");
		pi.search("over big data");
		pi.search("text warehousing over big data");
		pi.search("data warehouse over big data");
		System.out.println("\n------------------PHRASE QUERY TESTS END ---------------------\n");

	}
}

/**
 * 
 * Document class that contains the document id and the position list
 */
class Doc{
	int docId;
	ArrayList<Integer> positionList;
	public Doc(int did)
	{
		docId = did;
		positionList = new ArrayList<Integer>();
	}
	public Doc(int did, int position)
	{
		docId = did;
		positionList = new ArrayList<Integer>();
		positionList.add(new Integer(position));
	}
	
	public void insertPosition(int position)
	{
		positionList.add(new Integer(position));
	}
	
	public String toString()
	{
		String docIdString = ""+docId + ":<";
		for(Integer pos:positionList)
			docIdString += pos + ",";
		docIdString = docIdString.substring(0,docIdString.length()-1) + ">";
		return docIdString;		
	}
}
