import java.io.Serializable;



public class Link implements Serializable {
	Node dest;
	int weight;
	public Link(Node d, int wt)
	{
		weight = wt; 
		dest = d;
	}
	
}
