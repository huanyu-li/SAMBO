/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package se.liu.ida.sambo.MModel.util;

import java.io.FileNotFoundException;
import java.util.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Administrator
 */
public class NameProcessor {
    private Vector<String> stopwords;
    private String STOPWORD_FILE = null;
    private static NameProcessor m_instance = null;


    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(NameProcessor.getInstance().advCleanName("right ttt"));
        System.out.println(NameProcessor.getInstance().basicCleanName("manual ttt"));
    }

    /**
     *
     * @return
     */
    public static NameProcessor getInstance(){
        if (m_instance == null)
            m_instance = new NameProcessor();
        return m_instance;
    }

    /**
     *
     */
    public NameProcessor() {
        try {
            if (STOPWORD_FILE == null)
                STOPWORD_FILE = OntConstants.STOPWORD_FILE;
            if (STOPWORD_FILE == null)
                STOPWORD_FILE = System.getProperty("user.dir") + "/web/config/stopwords";
            this.loadStopWords();
        } catch (FileNotFoundException ex) {
            String msg = "Could not load stopwords file: " + STOPWORD_FILE;
            Logger.getLogger(NameProcessor.class.getName()).log(Level.SEVERE, msg, ex);
            System.exit(0);
        } catch (IOException ex){
            String msg = "Something wrong with reading the file: " + STOPWORD_FILE;
            Logger.getLogger(NameProcessor.class.getName()).log(Level.SEVERE, msg, ex);
            System.exit(0);
        }
    }

    /** Load the stopwords from file to the hashtable where they are indexed. */
    private void loadStopWords() throws FileNotFoundException, IOException {
        // Initialize hashtable to proper size given known number of
        // stopwords in the file and a default 75% load factor with
        // 10 extra slots for spare room.
        stopwords = new Vector<String>();
        BufferedReader in = new BufferedReader(new FileReader(STOPWORD_FILE));
        String line;
        while ((line = in.readLine()) != null) {
            stopwords.add(line.trim());
        }
        in.close();
    }

    /**
     *
     * @param text
     * @return
     */
    public String basicCleanName(String text){
        text = text.replace("_"," ");
        text = this.splitWord_atCap(text);
        return text.replace("- ","-").replace(" -","-").replace("( ","(").replace("[ ","[").replace("  ", " ").toLowerCase();
    }

    /**
     *
     * @param text
     * @return
     */
    public String advCleanName(String text){
        text = this.basicCleanName(text);
        char[] chars = text.toLowerCase().toCharArray();
        for (int i = 0; i < chars.length; i++)
            if(!Character.isLetterOrDigit(chars[i]))  chars[i] = ' ';//** + and - signs will be REMOVED here
        if(chars.length <= 0)
            return text;
        String out = removeStopwords(chars);
        
//        if(!text.equalsIgnoreCase(out))
//            System.out.println(text+"                "+out);
        return out;
    }

    private String splitWord_atCap(String input){
        char[] chars = input.toCharArray();
        ArrayList list = new ArrayList();

        for(int i=1; i < chars.length-1; i++)
        {
            //separte the string by upcase character
            boolean condition1 = Character.isUpperCase(chars[i]);
            boolean condition2 = !Character.isUpperCase(chars[i-1]);
            boolean condition3 = !Character.isUpperCase(chars[i+1]);
            boolean condition4 = !Character.isWhitespace(chars[i-1]);
            if ( condition1 && condition2 && condition3 && condition4 )
                list.add(new Integer(i));
        }
        String out = "";
        if(list.size() != 0)
        {
            Iterator it = list.iterator();
            int start = ((Integer)it.next()).intValue();
            out += input.subSequence(0, start);
            while(it.hasNext()){
                int index = ((Integer)it.next()).intValue();
                out +=  " " + input.substring(start, index);
                start = index;
            }
            out = out + " " + input.substring(start);
        }
        else
            out = input;
        return out;
    }

    private String removeStopwords(char[] chars){
        String out = "";
        if(stopwords != null ){
            StringTokenizer st = new StringTokenizer(new String(chars));
            while(st.hasMoreTokens()){
                String token = st.nextToken().trim();
                if(!stopwords.contains(token))
                    out += token + " ";
            }
            if(out.length() <= 0)
                return (new String(chars)).trim();
            return out.trim();
        }else
            return (new String(chars)).trim();
    }

}
