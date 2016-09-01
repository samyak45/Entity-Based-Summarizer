import java.util.ArrayList;


public class EntityNode extends Node
{
	int index;
	public EntityNode(int scr, int idx)
	{
		super(scr);
		index = idx;
	}
	public EntityNode(int idx)
	{
		this(1,idx);
	}
	public ArrayList<Integer> summarize(Summarizer summarizer)
	{
		if(summarizer == null) return null;
		ArrayList<Integer> summarySentenceIndices = new ArrayList<>();
		return summarizer.summarize(new ArrayList<Integer>().addAll(adj.keySet()));
	}
	
}
