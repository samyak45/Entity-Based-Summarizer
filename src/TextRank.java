import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.parser.lexparser.Edge;


public class TextRank 
{
	
//	public static boolean[] recognizeMainEntities(Map<Integer, CorefChain> coref_chains, int num_sentences)
//	{
//		boolean [] isImportant = null;
//		//build initial graph linking entities with sentences:
//		ArrayList<EntityNode> entities = buildGraph(coref_chains);
//		//link entities with each other:
//		linkEntities(entities, num_sentences);
////		todo:
//		int[] sentenceWeight = new int[num_sentences];
//		Arrays.fill(sentenceWeight, 1);
//		
//		return isImportant;
//	}
//	private static void linkEntities(ArrayList<EntityNode> entities, int num_sentences) 
//	{
//		ArrayList<ArrayList<EntityNode> >sentenceLinks = new ArrayList<>(num_sentences);
//		for(int i = 0; i < num_sentences; i++)
//			sentenceLinks.add(i, new ArrayList<>());
//		
//		ArrayList<Map<Integer, Edge> > entityLinks = new ArrayList<>(entities.size());
//		for(int i = 0; i < entities.size(); i++)
//			entityLinks
//		for(EntityNode entity:entities)
//		{
//			for(Edge edge:entity.adj)
//			{
//				sentenceLinks.get(edge.dest_index).add(entity);
//			}
//		}
//		for(ArrayList<EntityNode> sentenceLink : sentenceLinks)
//		{
//			for(EntityNode entity1 : sentenceLink)
//			{
//				for(EntityNode entity2 : sentenceLink)
//				{
//					if(entity1.index >= entity2.index) continue;
//					if(entityLinks.get(entity1.index).containsKey(entity2.index))
//					{
//						entityLinks.get(entity1.index).get(entity2.index).weight++;
//						entityLinks.get(entity2.index).get(entity1.index).weight++;
//					}
//					else
//					{
//						entityLinks.get(entity1.index).put(entity2.index, new Edge(entity2.index, 1, true));
//						entityLinks.get(entity2.index).put(entity1.index, new Edge(entity1.index, 1, true));
//					}
//				}
//			}
//		}
//		Set<Integer> keys = null;
//		for(Map<Integer, Edge> entityLink : entityLinks)
//		{
//			
//		}
//	}
//	public static ArrayList<EntityNode> buildGraph(Map<Integer, CorefChain> coref_chains)
//	{
//		Set<Integer> keys = coref_chains.keySet();
//		ArrayList<EntityNode> entities = new ArrayList<EntityNode>(keys.size());
//		int count = 0;
//		EntityNode curr_entity = null;
//		List<CorefChain.CorefMention> corefMentions = null;
//		for(Integer key: keys)
//		{
//			curr_entity = new EntityNode(count);
//			corefMentions = coref_chains.get(key).getMentionsInTextualOrder();
//			for(CorefMention mention:corefMentions)
//			{
//				curr_entity.adj.add(new Edge(mention.sentNum, 1));	
//			}
//			entities.add(count++, curr_entity);
//		}
//		return entities;
//	}
//	private static class EntityNode
//	{
//		int index, out_weight;
//		double score;
//		ArrayList<Edge> adj;
//		EntityNode(int indx) {
//			index = indx; score = 0; out_weight = 0; adj = null;
//		}
//		void addEdge(Edge edge)
//		{
//			if(edge!=null)
//			{
//				
//			}
//		}
//	}
//	private static class Edge
//	{
//		int dest_index, weight;
//		boolean dest_isEntity;
//		Edge(int din, int wt, boolean die)
//		{
//			dest_index = din;
//			weight = wt;
//			dest_isEntity = die;
//		}
//		Edge(int din, int wt)
//		{
//			this(din, wt, false);
//		}
//		
//	}
	public static ArrayList<EntityNode> recognizeMainEntities(Map<Integer, CorefChain> coref_chains, int num_sentences)
	{
		ArrayList<EntityNode> mainEntities;
//		int num_entities = coref_chains.size();
		ArrayList<Node> graph = buildGraph(coref_chains, num_sentences);
		int num_iterations = 50;
		double d = 0.80 ;
		assignScores(graph, num_iterations, d);
		mainEntities = extractMainEntities(graph);
		return mainEntities;
	}
	private static ArrayList<EntityNode> extractMainEntities(ArrayList<Node> graph)
	{
		ArrayList<EntityNode> mainEntities = new ArrayList<EntityNode>();
		ArrayList<EntityNode> entities = new ArrayList<EntityNode>();
		for(Node node:graph)
		{
			if(node.getClass() == EntityNode.class)
				entities.add((EntityNode) node);
		}
		Collections.sort(entities, Collections.reverseOrder());
		double meanScore = 0;
		for(EntityNode entity:entities)
			meanScore += entity.score;
		meanScore /= entities.size();
		double variance = 0;
		for(EntityNode entity:entities)
			variance += (entity.score - meanScore)*(entity.score - meanScore);
		variance /= entities.size();
		double std = Math.sqrt(variance);
		double threshold = meanScore - 0.285 * std;
		for(int i = 0; i < entities.size(); i++)
		{
			if(entities.get(i).score > threshold)
				mainEntities.add(entities.get(i));
			else break;
		}
		return mainEntities;
	}
	private static void assignScores(ArrayList<Node> graph, int num_iterations, double d) 
	{
		double scoreSum;
		for(int i = 0; i < num_iterations; i++)
		{
			for(Node node:graph)
			{
				scoreSum = 0;
				for(Link link:node.adj.values())
				{
					scoreSum += ((double)link.weight/(double)link.dest.out_weight) * link.dest.score;
				}
				node.score = (1-d) + d*scoreSum;
			}
		}
	}
	private static ArrayList<Node> buildGraph(Map<Integer, CorefChain> coref_chains,
			int num_sentences) 
	{
		ArrayList<Node> graph = new ArrayList<>();
		ArrayList<SentenceNode> sentences = (ArrayList<SentenceNode>)
				Stream.generate(SentenceNode::new)
				.limit(num_sentences+1).collect(Collectors.toList());
		ArrayList<EntityNode> entities = new ArrayList<EntityNode>(coref_chains.size());
//		ArrayList<EntityNode> entities = (ArrayList<EntityNode>)
//				Stream.generate(EntityNode::new)
//				.limit(coref_chains.size()).collect(Collectors.toList());
		//NOTE: Collection.nCopies can't be used above --
		Set<Integer> keys = coref_chains.keySet();
		CorefChain coref_chain = null;
		int count = 0, curr_sentNum;
		List<CorefMention> mentions = null;
		EntityNode curr_entity;
		SentenceNode curr_sentence;
		Link tempLink;
		for(Integer key:keys)
		{
			curr_entity = new EntityNode(count);
			coref_chain = coref_chains.get(key);
			mentions = coref_chain.getMentionsInTextualOrder();
			for(CorefMention mention:mentions)
			{
//				System.err.println("count = "+count);
				curr_sentNum = mention.sentNum;
//				System.err.println(curr_sentNum);
				curr_sentence = sentences.get(curr_sentNum);
				if(curr_entity.adj.containsKey(curr_sentNum))
				{
					tempLink = curr_entity.adj.get(curr_sentNum);
					tempLink.weight++;
					curr_entity.adj.put(curr_sentNum, tempLink);
					tempLink = curr_sentence.adj.get(count);
					tempLink.weight++;
					curr_sentence.adj.put(count, tempLink);
				}
				else
				{
					tempLink = new Link(curr_sentence, 1);
					curr_entity.adj.put(curr_sentNum, tempLink);
					tempLink = new Link(curr_entity, 1);
					curr_sentence.adj.put(count, tempLink);
				}
			}
			entities.add(count++, curr_entity);
		}
		graph.addAll(entities);
		graph.addAll(sentences);
		//evaluate out_weights:
		for(Node node:graph)
		{
			count = 0;
			for(Link link:node.adj.values())
			{
				count += link.weight;
			}
			node.out_weight = count;
		}
		return graph;
	}
}
