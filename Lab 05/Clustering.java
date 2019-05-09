import java.util.*;

/**
 * 612 Lab 5
 * Document clustering
 * @Author : Nirbhay Pherwani
 */
public class Clustering {

	int numDocs;
	int numClusters;
	int vSize;
	Doc[] docList;
	HashMap<String, Integer> termIdMap;

	ArrayList<Doc>[] clusters;
	Doc[] centroids;
	public Clustering(int numC)
	{
		numClusters = numC;
		clusters = new ArrayList[numClusters];
		centroids = new Doc[numClusters];
		termIdMap = new HashMap<String, Integer>();
	}

	/**
	 * Load the documents to build the vector representations
	 * @param docs
	 */
	public void preprocess(String[] docs){
		numDocs = docs.length;
		docList = new Doc[numDocs];
		int termId = 0;

		//collect the term counts, build term id map and the idf counts
		int docId = 0;
		for(String doc:docs){
			String[] tokens = doc.split(" ");
			Doc docObj = new Doc(docId);
			for(String token: tokens){
				if(!termIdMap.containsKey(token)){
					termIdMap.put(token, termId);
					docObj.termIds.add(termId);
					docObj.termWeights.add(1.0);
					termId++;
				}
				else{
					Integer tid = termIdMap.get(token);
					int index = docObj.termIds.indexOf(tid);
					if (index >0){
						double tw = docObj.termWeights.get(index);
						docObj.termWeights.add(index, tw+1);
					}
					else{
						docObj.termIds.add(termIdMap.get(token));
						docObj.termWeights.add(1.0);
					}
				}
			}
			docList[docId] = docObj;
			docId++;
		}
		vSize = termId;
		//System.out.println("vSize: " + vSize);

		//compute the tf-idf weights of documents
		for(Doc doc: docList){
			double docLength = 0;
			double[] termVec = new double[vSize];
			for(int i=0;i<doc.termIds.size();i++){
				Integer tid = doc.termIds.get(i);
				double tfidf = (1+Math.log(doc.termWeights.get(i)));//Math.log(numDocs/idfMap.get(tid));
				doc.termWeights.set(i, tfidf);
				docLength += Math.pow(tfidf, 2);
			}

			//normalize the doc vector
			for(int i=0;i<doc.termIds.size();i++){
				double tw = doc.termWeights.get(i);
				doc.termWeights.set(i, tw/Math.sqrt(docLength));
				//System.out.println(doc.termIds.get(i));
				termVec[doc.termIds.get(i)] = tw/Math.sqrt(docLength);
			}
			doc.termVec = termVec;
			//doc.termIds = null;
			//doc.termWeights = null;
		}
	}

	/**
	 * Cluster the documents
	 * For kmeans clustering, use the first and the ninth documents as the initial centroids
	 */
	public void cluster(){
    /*
    fDistance - docWise distance from first centroid
		sDistance - docWise distance from second centroid
    cluster1 - docs in cluster 1
    cluster2 - docs in cluster 2
    newFirst - newCentroid for first Cluster
    newSecond - newCentroid for second Cluster
    value1 - calculates value after each iteration from first centroid to doc and adds to fDistance
    value2 - calculates value after each iteration from second centroid to doc and adds to sDistance
    fStop - enabled if first cluster reaches stopping criteria
    sStop - enabled if second cluster reaches stopping criteria
    stop - enabled if stopping criteria reached
    counter - to track iterations
    */
    //TO BE COMPLETED
		int stop = 0;
		ArrayList<Double> first = new ArrayList<Double>();
		for(Double item: docList[0].termVec){
			first.add(item);
		}
		ArrayList<Double> second = new ArrayList<Double>();
		for(Double item: docList[8].termVec){
			second.add(item);
		}
		System.out.println("Initalization");
		System.out.println(first);
		System.out.println(second);
		System.out.println("--------------");
		int counter = 1;

		while(stop != 1){
		System.out.println("Iteration: "+counter++);
		ArrayList<Double> fDistance = new ArrayList<Double>();
		ArrayList<Double> sDistance = new ArrayList<Double>();
		double value1 = 0.0;
		double value2 = 0.0;
		for(Doc doc : docList){
			value1 = 0.0;
			value2 = 0.0;
			for (int index = 0; index<first.size(); index++){
				value1+=first.get(index)*doc.termVec[index];
				value2+=second.get(index)*doc.termVec[index];
			}
			fDistance.add(value1);
			sDistance.add(value2);
		}

		ArrayList<Integer> cluster1 = new	ArrayList<Integer>();
		ArrayList<Integer> cluster2 = new	ArrayList<Integer>();

		for(int index = 0; index<fDistance.size(); index++){
			if(fDistance.get(index)>=sDistance.get(index)){
				cluster1.add(index);
			}else{
				cluster2.add(index);
			}
		}
		ArrayList<Double> newFirst = new ArrayList<Double>();
		ArrayList<Double> newSecond = new ArrayList<Double>();

		//computing tws for new cluster
		int count = 1;
		for(int item : cluster1){
			ArrayList<Double> temp = new ArrayList<Double>();
			for(Double i: docList[item].termVec){
				temp.add(i);
			}
			if(count==1){
				for(Double i: temp){
					newFirst.add(i);
				}
			}
			for(int index=0; index<temp.size(); index++){
				if(count == 1){
					continue;
				}
				newFirst.set(index, (newFirst.get(index)+temp.get(index)));
			}
			++count;
		}


		//dividing each index by the cluster's length to compute the centroid's element at an index
		int fStop = 0;
		for(int index=0; index<newFirst.size(); index++){
			newFirst.set(index, newFirst.get(index)/cluster1.size());
			if(Double.compare(first.get(index),newFirst.get(index)) == 0){
				fStop += 1;
			}
		}

		//computing tws for second new cluster
		count = 1;
		for(int item : cluster2){
			ArrayList<Double> temp = new ArrayList<Double>();
			for(Double i: docList[item].termVec){
				temp.add(i);
			}
			if(count==1){
				for(Double i: temp){
					newSecond.add(i);
				}
			}
			for(int index=0; index<temp.size(); index++){
				if(count == 1){
					continue;
				}
				newSecond.set(index, newSecond.get(index)+temp.get(index));
			}
			++count;
		}
		int sStop = 0;
		//dividing each index by the cluster's length to compute the centroid's element at an index
		for(int index=0; index<newSecond.size(); index++){
			newSecond.set(index, newSecond.get(index)/cluster2.size());
			if(Double.compare(second.get(index), newSecond.get(index)) == 0){
				sStop += 1;
			}
		}

		System.out.println("Cluster 1");
		System.out.println(cluster1);
		System.out.println("Centroid 1");
		System.out.println(newFirst);
		System.out.println("Cluster 2");
		System.out.println(cluster2);
		System.out.println("Centroid 2");
		System.out.println(newSecond);
		System.out.println("---------------");

		if((sStop == newSecond.size()) && (fStop == newFirst.size())){
			stop = 1;
		}
		else{
			stop = 0;
			first = newFirst;
			second = newSecond;
		}
	}
}

	public static void main(String[] args){
		String[] docs = {"hot chocolate cocoa beans",
				 "cocoa ghana africa",
				 "beans harvest ghana",
				 "cocoa butter",
				 "butter truffles",
				 "sweet chocolate can",
				 "brazil sweet sugar can",
				 "suger can brazil",
				 "sweet cake icing",
				 "cake black forest"
				};
		Clustering c = new Clustering(2);
		c.preprocess(docs);
		System.out.println("Vector space representation:");
		for(int i=0;i<c.docList.length;i++){
			System.out.println(c.docList[i]);
		}
		c.cluster();

	}
}

/**
 *
 * Document id class that contains the document id and the term weight in tf-idf
 */
class Doc{
	int docId;
	ArrayList<Integer> termIds;
	ArrayList<Double> termWeights;
	double[] termVec;
	public Doc(){

	}
	public Doc(int id){
		docId = id;
		termIds = new ArrayList<Integer>();
		termWeights = new ArrayList<Double>();
	}
	public void setTermVec(double[] vec){
		termVec = vec;
	}

	public String toString()
	{
		String docString = "[";
		for(int i=0;i<termVec.length;i++){
			docString += termVec[i] + ",";
		}
		return docString+"]";
	}


}
