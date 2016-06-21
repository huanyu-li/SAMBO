/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.algos.matching.algos;

import se.liu.ida.sambo.MModel.util.NameProcessor;
import se.liu.ida.sambo.algos.matching.Matcher;

/**
 *
 * @author Rajaram
 */
public class TestMatchers {
    
    public static void main(String args[])
    {
        Matcher[] matchers =new Matcher[5];
        matchers[0] =  new EditDistance();
        matchers[1] =  new NGram();
        matchers[2] =  new Porter_WordNet(false);
        matchers[3] =  new Porter_WordNet(true);
        matchers[4] =  new UMLSKSearch_V6();
        
        NameProcessor clnstr=new NameProcessor();
        
        String str1="all sites";
        //String str2=clnstr.advCleanName(str1);
        String str2="sites";
        
        
        for(Matcher m:matchers)
        {
            System.out.println("Sim value for "+m.getClass()+" = "+m.getSimValue(str1, str2));
        }
    }
    
}
