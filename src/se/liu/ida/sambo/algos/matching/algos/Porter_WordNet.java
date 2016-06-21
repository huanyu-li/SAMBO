/*
 * wordNetLookUp.java
 *
 * Created on August 25, 2003, 2:39 PM
 */

package se.liu.ida.sambo.algos.matching.algos;


import net.didion.jwnl.*;
import net.didion.jwnl.data.*;
import net.didion.jwnl.data.relationship.*;
import net.didion.jwnl.dictionary.Dictionary;


import java.io.FileInputStream;
import java.util.*;
import se.liu.ida.sambo.MModel.MElement;
import se.liu.ida.sambo.algos.matching.Matcher;


/** The class implements liguistic matching using electronic dictionary wordNet
 * @author He Tan
 */
public class Porter_WordNet extends Matcher{
    
    private  Porter porter;
    private Dictionary dict;
    
    // The Hashtable contains words with their senses.
    //It takes time to lookup word in WordNet at first time.
    private Hashtable ht;
    
    boolean WORDNET_ON;
    
    
    /** Creates Porter matcher
     *
     * @param on indicates whether to look up WordNet
     */
    public Porter_WordNet( boolean on){
        
        this.porter = new Porter();
        this.WORDNET_ON = on;
        
        if(WORDNET_ON){
            
            ht = new Hashtable();
            
            try{
                if (AlgoConstants.WORDNET_DIC == null)
                   JWNL.initialize(new FileInputStream(System.getProperty("user.dir") + "/web/config/wordnet.xml"));
                else
                   JWNL.initialize(new FileInputStream(AlgoConstants.WORDNET_DIC));
                dict = Dictionary.getInstance();
                
            }catch (Exception e) {
                throw (RuntimeException) e.fillInStackTrace();
            }
        }
    }
    
    /**
     * Calculates the similarity (probability of match) between
     * any two strings.
     * @param input1 The first string.
     * @param input2 The second string.
     * @return The similarity score
     */
    public double getSimValue(String input1, String input2) {
        
        Vector values= new Vector();
        
        //compute the similarity score of each word in the two strings
        for(StringTokenizer st1 = new StringTokenizer(input1); st1.hasMoreTokens();){
            
            String wordOne = st1.nextToken();
            for(StringTokenizer st2 = new StringTokenizer(input2); st2.hasMoreTokens();){
                
                String wordTwo = st2.nextToken();
                values.add(new Tuple(wordOne, wordTwo, generateSimilarityScore(wordOne, wordTwo)));
            }
        }
        
        //sort similarityList according with the similarity scores
        Handler.mergesort(values, 0, values.size()-1);
        
        Vector matchedWords = new Vector();
        double simValue = 0.0;
        //compute matching score of the two string
        for(Enumeration e = values.elements(); e.hasMoreElements();){
            
            Tuple tuple = (Tuple)e.nextElement();
            if(!matchedWords.contains((String) tuple.getObjectOne()) ||
                    !matchedWords.contains((String) tuple.getObjectTwo())){
                simValue += tuple.getScore();
                
                matchedWords.add((String) tuple.getObjectOne());
                matchedWords.add((String) tuple.getObjectTwo());
            }
            
        }
        
        return simValue/Handler.max((new StringTokenizer(input1)).countTokens(),(new StringTokenizer(input2)).countTokens());
    }
    
    
    
    
    /**
     * Calculates the similarity (probability of match) between
     * any two words.
     * @param wordOne The first word.
     * @param wordTwo The second word.
     * @return The similarity score
     */
    private double generateSimilarityScore(String wordOne, String wordTwo){
        
        if(porter.stripAffixes(wordOne).equals(porter.stripAffixes(wordTwo)))
            return 1.0;
        
        if(WORDNET_ON)
            return lookUpWordNet(wordOne, wordTwo);
        
        return 0.0;
    }
    
    
    
    private double lookUpWordNet(String wordOne, String wordTwo){
        
        double simValue = 0.0;
        
        try{
            
            double temp;
            
            IndexWordSet indexWordSetOne;
            IndexWordSet indexWordSetTwo;
            
            if(ht.containsKey(wordOne))
                
                //if the word is not in Hashtable, lookup it in WordNet
                indexWordSetOne = (IndexWordSet) ht.get(wordOne);
            else{
                indexWordSetOne = dict.lookupAllIndexWords(wordOne);
                ht.put(wordOne, indexWordSetOne);
            }
            
            
            if(ht.containsKey(wordTwo))
                indexWordSetTwo = (IndexWordSet) ht.get(wordTwo);
            else{
                indexWordSetTwo= dict.lookupAllIndexWords(wordTwo);
                ht.put(wordTwo, indexWordSetTwo);
            }
            
            //compare the relationship of the two words in all senses,
            //and return the hightest score.
            if((indexWordSetOne!=null)&&(indexWordSetTwo !=null)){
                
                Set validPosOne = indexWordSetOne.getValidPOSSet();
                Set validPosTwo = indexWordSetTwo.getValidPOSSet();
                
                for (Iterator itrOne = validPosOne.iterator(); itrOne.hasNext();){
                    
                    POS posOne = (POS)itrOne.next();
                    for(Iterator itrTwo = validPosTwo.iterator(); itrTwo.hasNext();){
                        
                        POS posTwo = (POS) itrTwo.next();
                        
                        if(posOne.equals(posTwo)){
                            
                            temp = getAsymmetricRelationship(indexWordSetOne.getIndexWord(posOne),
                                    indexWordSetTwo.getIndexWord(posTwo));
                            if(temp>simValue)
                                simValue = temp;
                        }
                    }
                }
            }
            
            
        }catch (JWNLException e) {
            
            throw (RuntimeException) e.fillInStackTrace();
        }
        
        return simValue;
    }
    
    
    
    
    private double getAsymmetricRelationship(IndexWord start, IndexWord end) throws JWNLException {
        
        double score = 0;
        double temp = 0;
        
        Synset[] startSynsets = start.getSenses();
        Synset[] endSynsets  = end.getSenses();
        
        if((startSynsets.length + endSynsets.length)>11)
            return 0;
        
        for(int i=0; i < startSynsets.length; i++){
            for(int j =0; j < endSynsets.length; j++){
                
                RelationshipList list = RelationshipFinder.getInstance().findRelationships(startSynsets[i], endSynsets[j], PointerType.HYPERNYM);
                
                if((list !=null) && (!list.isEmpty())){
                    
                    temp = 1.0- ((Relationship)list.get(0)).getDepth()*AlgoConstants.WORDNET_WEIGHT;
                    
                    if ((((AsymmetricRelationship)list.get(0)).getCommonParentIndex() == 0) && (temp>score))
                        
                        score = temp;
                }
            }
        }
        
        return score;
    }
        
    public static void main(String[] args) {
        //  for (int i = 0; i < 18; i++) {
        Porter_WordNet test = new Porter_WordNet(true);
        System.out.println(test.getSimValue("nasal cavity", "paranasal sinus"));
        System.out.println("\n");
        // }
    }
}
