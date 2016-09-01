import java.util.ArrayList;
import java.util.HashMap;


class Pair implements Comparable<Pair>{
	Double first;
	Integer second;
	Pair(Double fst,Integer scd){
		first=fst;
		second=scd;
	}
	Pair(){
		this(null,null);
	}
	public int compareTo(Pair p){
		return first.compareTo(p.first);
	}
}

public class Summarizer {
	SentenceTest similarityChecker;
	final int passes=10;
	final double D=0.8;
	//Map<Integer, CorefChain> coref_chain;
	String[] sentences;
	ArrayList<HashMap<Integer,Double > > comparisonStore;  //Int=sentenceID, Double=compared val
	//boolean []isImportant;
	public Summarizer(String[] sent){
		sentences=sent;
		comparisonStore=new ArrayList<HashMap<Integer,Double > >(sentences.length);
		for(HashMap<Integer,Double> h:comparisonStore){
			h=new HashMap<Integer,Double>();
		}
		similarityChecker=new SentenceTest();
	}
	public ArrayList<Integer> summarize(ArrayList<Integer> sentPool){
		//Summarizes a given sentence pool
		ArrayList<HashMap<Integer,Double>> graph=buildgraph(sentPool);
		double score[]=rank(graph);
		ArrayList<Pair> pii=new ArrayList<Pair>(score.length); 
		for(int i=0;i<score.length;i++){
			pii.add(new Pair(score[i],i));
		}
		//Collections.sort(pii,Collections.reverseOrder());
		ArrayList<Integer> summary=new ArrayList<>();
		double avg=0;
		for(double x:score)avg+=x;
		double sigma=0;
		for(double x:score){
			sigma+=((x-avg)*(x-avg));
		}
		sigma=Math.sqrt(sigma);
		double threshold=avg+sigma;
		avg=avg/score.length;
		for(int i=0;i<score.length;i++){
			if(pii.get(i).first>threshold-0.01)
				summary.add(pii.get(i).second);
		}
		return summary;
		
	}
	public double[] rank(ArrayList<HashMap<Integer,Double > > graph){
		//Applies textrank to the graph created above
		double [] score=new double[graph.size()];
		double [] outweight =new double[graph.size()];
		//Calculating outweights
		for(int i=0;i<score.length;i++){
			score[i]=1;
			outweight[i]=0;
			for(Double d:graph.get(i).values()){
				outweight[i]+=d;
			}
			
		}
		
		
		for(int i=0;i<passes;i++){
			for(int j=0;j<score.length;j++){
				double temp=0;
				for(int k=0;k<score.length;k++){
					if(j==k)continue;
					else{
						temp=temp+((graph.get(j).get(k))/outweight[k])*score[k];
						
					}
				}
				score[j]=(1-D)+D*temp;
			}
		}
		return score;
	}
	public ArrayList<HashMap<Integer,Double>> buildgraph(ArrayList<Integer> sentpool){
		ArrayList<HashMap<Integer,Double>> graph=new ArrayList<HashMap<Integer,Double > >(sentpool.size());
		double sim;
		for(HashMap<Integer,Double> h:graph){
			h=new HashMap<Integer,Double>();
		}
		for(Integer i:sentpool){
			for(Integer j:sentpool){
				if(i==j)continue;
				else{
					if(comparisonStore.get(i).containsKey(j)){
						graph.get(i).put(j,comparisonStore.get(i).get(j));
					}
					else{
						sim=similarityChecker.getSim(sentences[i],sentences[j]);
						comparisonStore.get(i).put(j, sim);
						comparisonStore.get(j).put(i, sim);
						graph.get(i).put(j, sim);
					}
				}
			}
		}
		return graph;
	}
	
}
