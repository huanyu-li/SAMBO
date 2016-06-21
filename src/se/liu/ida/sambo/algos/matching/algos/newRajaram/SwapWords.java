/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.algos.matching.algos.newRajaram;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import se.liu.ida.sambo.algos.matching.Matcher;

/**
 * <p>
 * Finds similarity (probability of match) between strings by using a simple
 * word swapping algorithm.
 * </p>
 *
 * @author Rajaram
 * @version 1.0
 */
public class SwapWords extends Matcher {

    /**
     * <p>
     * Calculates the similarity between any two strings by swapping words.
     * </p>
     *
     * <p>
     * Example: For the terms "hair shaft" and "shaft hair", the similarity
     * value will be 1 (Note: This algorithm heavily depends on the stop words).
     * </p>
     *
     * @param term1     The first string.
     * @param term2     The second string.
     *
     * @return          similarity value between two given strings.
     */
    @Override
    public double getSimValue(final String term1, final String term2) {

        /**
         * If both the inputs are same value then return highest.
         * similarity value which is 1
         */
        if (term1.equalsIgnoreCase(term2)) {
            return MatcherConstants.MAX_SIMVALUE;
        }

        // Spliting strings into list of words.
        List<String> term1List = splitTerms(term1);
        List<String> term2List = splitTerms(term2);

        /**
         *If the number of words in both the inputs are different then return
         * the minimum similarity value which is 0.
         */
        if (term1List.size() != term2List.size()) {
            return MatcherConstants.MIN_SIMVALUE;
        } else {

            for (String word:term1List) {
                if (!term2List.contains(word)) {
                    return MatcherConstants.MIN_SIMVALUE;
                }
            }
        }

        return MatcherConstants.MAX_SIMVALUE;
    }

    /**
      * <p>
      * Split the words in the given sentence/group of words.
      * </p>
      *
      * @param term     Group of words.
      *
      * @return         List with the list of words, returns empty list if the
      *                 input is empty.
      */
    private List<String> splitTerms(final String term) {

        List<String> wordList = new ArrayList<String> ();        
        String[] words = term.split(" ");
        wordList = Arrays.asList(words);

        return wordList;
    }
}
