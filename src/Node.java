import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class Node implements Comparable<Node>,Serializable
{
	double score;
	int out_weight;
	Map<Integer, Link> adj;//sentence(or Entity) Index -> edgeWeight
	public Node(int scr)
	{
		score = scr;
		out_weight = 0;
		adj = new HashMap<Integer, Link>();
	}
	public int compareTo(Node x)
	{
		return (int)(-x.score + score);
	}
}
