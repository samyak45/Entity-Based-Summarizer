

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.CoreMap;

public class MAIN {

	public static void main(String[] args) throws ClassNotFoundException, IOException 
	{
		//get the input file:
		if(args.length <= 0) return;
		String filename = args[0];
		File file = new File(filename);
		
		//Create a StanfordCoreNLP object:
		Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, parse, pos, lemma, ner, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        
        //read the input file into String:
		String document_text = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));;
		
		//load tagger model:
//		MaxentTagger tagger = new MaxentTagger("tagger/english-left3words-distsim.tagger");
				
		// create an empty Annotation just with the given text
		Annotation document = new Annotation(document_text);

		// run all Annotators on this text
		pipeline.annotate(document);
		
		//get the coreference chains:
		Map<Integer, CorefChain> coref_chains = document.get(CorefChainAnnotation.class);
		System.err.println("The coref chains :");
		for(CorefChain cc : coref_chains.values())
			System.err.println(cc);
		//get the sentences
		List<CoreMap> sentences_CoreMap = document.get(SentencesAnnotation.class);
		int num_sentences = sentences_CoreMap.size();
		String[] sentences = new String[num_sentences];
		for(int i = 0; i < num_sentences; i++)
		{
			sentences[i] = sentences_CoreMap.get(i).toString();
			System.out.println(sentences[i]);
		}
		
	
		//get main entities present:
		ArrayList<EntityNode> mainEntities = TextRank.recognizeMainEntities(coref_chains, num_sentences);
		System.out.println("NO. of main entities are " + mainEntities.size());
		//save all my work to a file
		ObjectOutputStream savefile = new ObjectOutputStream(new FileOutputStream("S:\\javaObjects.txt"));
		savefile.writeObject(mainEntities);
		savefile.writeObject(sentences);
		
		
		
		
		//build summaries for the main entities:
		Summarizer summarizer = new Summarizer(sentences);
		List<ArrayList<Integer>> summarySentences = new LinkedList<ArrayList<Integer>>();
		for(EntityNode entity:mainEntities)
			summarySentences.add(entity.summarize(summarizer));
		
		//output the summaries:
		BufferedWriter bw;
		if(args.length < 1) 
			bw = new BufferedWriter(new OutputStreamWriter(System.out), 102400);
		else
		{
			File outputFile = new File(args[1]);
			if (!outputFile.exists()) 
				outputFile.createNewFile();
			bw = new BufferedWriter(new FileWriter(file.getAbsolutePath()), 102400);
		}
		PrintWriter out = new PrintWriter(bw);
		int entity_count = 0;
		for(List<Integer> importantSentences: summarySentences)
		{
			out.println("entity_"+ ++entity_count);
			for(Integer index:importantSentences)
				out.print(sentences[index]);
			out.println("--------------------------------------------------------------------");
		}
	}
}
