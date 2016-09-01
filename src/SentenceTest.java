

import semilar.config.ConfigManager;
import semilar.data.Sentence;
import semilar.sentencemetrics.BLEUComparer;
import semilar.sentencemetrics.CorleyMihalceaComparer;
import semilar.sentencemetrics.DependencyComparer;
import semilar.sentencemetrics.GreedyComparer;
import semilar.sentencemetrics.LSAComparer;
import semilar.sentencemetrics.LexicalOverlapComparer;
import semilar.sentencemetrics.MeteorComparer;
import semilar.sentencemetrics.OptimumComparer;
import semilar.sentencemetrics.PairwiseComparer.NormalizeType;
import semilar.sentencemetrics.PairwiseComparer.WordWeightType;
import semilar.tools.preprocessing.SentencePreprocessor;
import semilar.tools.semantic.WordNetSimilarity;
import semilar.wordmetrics.LDAWordMetric;
import semilar.wordmetrics.LSAWordMetric;
import semilar.wordmetrics.WNWordMetric;

/**
 * Examples using various sentence to sentence similarity methods. Please note that due to huge models of similarity
 * measures (wordnet files, LSA model, LDA models, ESA, PMI etc) and preprocessors - Standford/OpenNLP models, you may
 * not be able to run all the methods in a single pass. Also, for LDA based methods, we have to estimate the topic
 * distributions for the candidate pairs before calculating similarity. So, The examples for LDA based sentence to
 * sentence similarity and document level similarity are provided in the separate file as they have special
 * requirements.
 *
 * Some methods calculate the similarity of sentences directly or some use word to word similarity expanding to the
 * sentence level similarity. See the examples + documentation for more details.
 *
 * @author Rajendra
 */
public class SentenceTest {

    SentencePreprocessor preprocessor;
    GreedyComparer greedyComparerWNLin; //greedy matching, use wordnet LIN method for Word 2 Word similarity

    public SentenceTest() {
    	
        /* Word to word similarity expanded to sentence to sentence .. so we need word metrics */
    	boolean wnFirstSenseOnly = false; //applies for WN based methods only.
        WNWordMetric wnMetricLin = new WNWordMetric(WordNetSimilarity.WNSimMeasure.LIN, wnFirstSenseOnly);
        ConfigManager.setSemilarDataRootFolder("C:/Users/Shivam/data/semilar-data/");
        greedyComparerWNLin = new GreedyComparer(wnMetricLin, 0.3f, false);
        preprocessor = new SentencePreprocessor(SentencePreprocessor.TokenizerType.STANFORD, SentencePreprocessor.TaggerType.STANFORD, SentencePreprocessor.StemmerType.PORTER, SentencePreprocessor.ParserType.STANFORD);
        }

    public void printSimilarities(Sentence sentenceA, Sentence sentenceB) {
        System.out.println("Sentence 1:" + sentenceA.getRawForm());
        System.out.println("Sentence 2:" + sentenceB.getRawForm());
        System.out.println("------------------------------");
        for(int i = 0; i< 100; i++)
        System.out.println("greedyComparerWNLin : " + greedyComparerWNLin.computeSimilarity(sentenceA, sentenceB));
     
        
    }
    public double getSim(String s1,String s2){
    	Sentence sent1,sent2;
    	sent1=preprocessor.preprocessSentence(s1);
    	sent2=preprocessor.preprocessSentence(s2);
    	return greedyComparerWNLin.computeSimilarity(sent1, sent2);
    	
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // first of all set the semilar data folder path (ending with /).
        

        Sentence sentence1;
        Sentence sentence2;

       // String text1 = "\"Senator Clinton should be ashamed of herself for playing politics with the important issue of homeland security funding,\" he said.";
//        String text2 = "\"She should be ashamed of herself for playing politics with this important issue,\" said state budget division spokesman Andrew Rush.";
        String text1 = "I love you";
        String text2 = "I ljkhkj .";
        SentenceTest s2sSimilarityMeasurer = new SentenceTest();
       System.out.println(s2sSimilarityMeasurer.getSim(text1,text2)); 
       
        
        //s2sSimilarityMeasurer.printSimilarities(sentence1, sentence2);

        System.out.println("\nDone!");
    }
}
