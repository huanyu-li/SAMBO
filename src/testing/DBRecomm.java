/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import se.liu.ida.sambo.Merger.Constants;
import se.liu.ida.sambo.Recommendation.RecommendationConstants;
import se.liu.ida.sambo.algos.matching.algos.SimValueConstructorUserListPair;
import se.liu.ida.sambo.jdbc.simvalue.SaveSimValuesDBAccess;
import testing.AccessSuggBasedRecomm;

/**
 *
 * @author rajka62
 */
public class DBRecomm implements Runnable{
    
  public double a,b,c,d;
//    protected java.sql.Connection userConn;  
    

    
    
    
    //final boolean isConnSupplied = (userConn != null);  
    Connection Selectconn=null;
    Connection Updateconn=null;
    List<String> strategy=new ArrayList();
    
    ArrayList<String> INSERT_STATEMENT=new ArrayList();
    
    
    AccessSuggBasedRecomm storeTODB=new AccessSuggBasedRecomm(); 
    
    
    
    
    
     // To get table name from SaveSimValuesDBAccess class
    SaveSimValuesDBAccess simvalu = new SaveSimValuesDBAccess();
    String simValueTablename=simvalu.getTableName();//"dbsambo.savesimvalues";
    
       
    //*
    
    
    public DBRecomm (Connection Updateconn, Connection Selectconn , List<String> strategy)
    {
        
        this.Selectconn=Selectconn;
        this.Updateconn=Updateconn;
        this.strategy=strategy;
        
        
    }
    
    
    
    public void run() {
        
        for (String s:strategy)
                {
                    
                    if(!storeTODB.select(s, Selectconn))
                            {
                 
                    String Straparams[]=s.split(",");
                    //out.println(_result[i].getId()+" "+_result[i].getThreshold()+" "+_result[i].getMatchers()+" "+_result[i].getWeights()+" "+_result[i].getSubweights());
                    
                    double[] scoreParam=calculateRecommendationScore(Straparams[0], Straparams[1], Straparams[2], Straparams[3]);
                    
                    
                    
                    double precisionP=(scoreParam[0]/(scoreParam[0]+scoreParam[1]));
                    double recallP=(scoreParam[0]/(scoreParam[0]+scoreParam[2]));
                    
                    double precisionM=(scoreParam[3]/(scoreParam[2]+scoreParam[3]));
                    double recallM=(scoreParam[3]/(scoreParam[1]+scoreParam[3]));
                    
                    double score1= (scoreParam[0]+scoreParam[3])/(scoreParam[0]+scoreParam[1]+scoreParam[2]+scoreParam[3]);
                    
                    double score2= scoreParam[0]/(scoreParam[0]+scoreParam[1]+scoreParam[2]);
                    
                    
                     float p = (float)Math.pow(10,2);
                    
                    
                    
                    
                    String statement=("INSERT INTO dbsambo.recommendationSuggBased VALUES('"+Straparams[0]+"', '"+Straparams[1]+"', '"+Straparams[2]+"', '"+Straparams[3]+"', '"+scoreParam[0]+"', '"+scoreParam[1]+"', '"+scoreParam[2]+"', '"+scoreParam[3]+"', '"+recallP+"', '"+precisionP+"', '"+recallM+"', '"+precisionM+"', '"+score1+"', '"+score2+"')");
                    
                    INSERT_STATEMENT.add(statement);
                    
                    
                    if(INSERT_STATEMENT.size()>50)
                    {
                      
                        INSERT_STATEMENT.clear();
                    }
                   
                
                
                }                    
                    
                    else
                        System.out.println("Strategy "+s+" is in DB");
                
                }
        
        
        if(INSERT_STATEMENT.size()>0)
                {
//                
                }
                
//                storeTODB.closeConnection();
    }
    
    
    
    
    
    
    
    
    
    public double[] calculateRecommendationScore(String matchers, String weights, String combination, String thresholds)
    {
        
            double score=0.0;
            
            String[] matcher=matchers.split("\\;");
            String[] weightstr=weights.split("\\;");
            
            boolean threshold_Single=true;
            String[] thresholdstr=thresholds.split("\\;");
            
            
            double Singlethreshold=0, upperthreshold=0, lowerthreshold=0;
            
            
            if(thresholdstr.length>1)
            {
                threshold_Single=false;                
                lowerthreshold=Double.valueOf(thresholdstr[0]).doubleValue();
                upperthreshold=Double.valueOf(thresholdstr[1]).doubleValue();
            }
            else
                Singlethreshold=Double.valueOf(thresholdstr[0]).doubleValue();
            //int num = AlgoConstants.NO_OF_MATCHERS;
            
            double[] weight=new double[weightstr.length];

            
            
            
            
            
            for(int i=0;i<weightstr.length;i++)weight[i]=Double.valueOf(weightstr[i]).doubleValue();
            
                        a=0; b=0; c=0; d=0;

                      
                      
                      
                      if(threshold_Single)
                          findScoreParamUsingList(matcher, weight, Singlethreshold, RecommendationConstants.VALIDATED_SUGGESTIONS, combination);
                      else
                          findScoreParamUsingList(matcher, weight, upperthreshold, lowerthreshold, RecommendationConstants.VALIDATED_SUGGESTIONS, combination);
                      
                      
                      
                      
                      System.out.println("A=" +a);
                      System.out.println("B=" +b);
                      System.out.println("C=" +c);
                      System.out.println("D=" +d);
                      
                      score=(a+d)/(a+b+c+d);
                      
                      System.out.println("Score for (a+d)/(a+b+c+d) = " +score);
                      
                      score=a/(a+b+c);
                      
                      System.out.println("Score for a/(a+b+c) = " +score);
                      

            
        double[] scoreParam={a,b,c,d}; 
        
        return scoreParam;
    }
    
    

//    
    
    
    
    
    private void findScoreParamUsingList(String[] matcher, double[] weight, double upperthreshold, double lowerthreshold, ArrayList ConceptPairs, String combination) {
        
        
        
        int []IntMatcher=new int[matcher.length];
        
        
        double[] weightsAll=new double[Constants.singleMatchers.length];
        
        
        for(int i=0;i<matcher.length;i++){
            
           // System.out.print(" "+matcher[i]);
            
            for(int j=0;j<Constants.singleMatchers.length;j++)
            {
                if(matcher[i].equalsIgnoreCase(Constants.singleMatchers[j]))
                {
                     IntMatcher[i]=j;
                     weightsAll[j]=weight[i];
                 }
                 
            }
        }
            

                SimValueConstructorUserListPair simValueConstructor2= new SimValueConstructorUserListPair(ConceptPairs);                
                
                
                ArrayList<String> suggestions=simValueConstructor2.getSuggestions(IntMatcher, combination, weightsAll, upperthreshold, lowerthreshold);
    
                simValueConstructor2.closeAllConnections(); 
                
                
                ArrayList<String> Acceptedmapping=RecommendationConstants.ACCEPTED_SUGGESTIONS;
                ArrayList<String> Rejectedmapping=RecommendationConstants.REJECTED_SUGGESTIONS;
                        
     
        
        
        
        for(String sug:suggestions)
        {
            if(Acceptedmapping.contains(sug))
                a++;
            else if(Rejectedmapping.contains(sug))
                b++;
        }
        
        
        
        for(String accept:Acceptedmapping)
        {
            if(!suggestions.contains(accept))
                c++;
        }
        
        d=ConceptPairs.size()-(a+b+c);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private void findScoreParamUsingList(String[] matcher, double[] weight, double threshold, ArrayList ConceptPairs, String combination) {
        
        
        
        int []IntMatcher=new int[matcher.length];
        
        double[] weightsAll=new double[Constants.singleMatchers.length];
        
        
        for(int i=0;i<matcher.length;i++){
            
           // System.out.print(" "+matcher[i]);
            
            for(int j=0;j<Constants.singleMatchers.length;j++)
            {
                if(matcher[i].equalsIgnoreCase(Constants.singleMatchers[j]))
                {
                     IntMatcher[i]=j;
                     weightsAll[j]=weight[i];
                 }
                 
            }
        }
            

                SimValueConstructorUserListPair simValueConstructor2= new SimValueConstructorUserListPair(ConceptPairs);                
                
                
                ArrayList<String> suggestions=simValueConstructor2.getSuggestions(IntMatcher, combination, weightsAll, threshold);
    
                simValueConstructor2.closeAllConnections(); 
                
                
                ArrayList<String> Acceptedmapping=RecommendationConstants.ACCEPTED_SUGGESTIONS;
                ArrayList<String> Rejectedmapping=RecommendationConstants.REJECTED_SUGGESTIONS;
                        
     
        
        
        
        for(String sug:suggestions)
        {
            if(Acceptedmapping.contains(sug))
                a++;
            else if(Rejectedmapping.contains(sug))
            {
                b++;
                
                String [] str=sug.split("\\;");
                //System.out.println("select  *  from savesimvalues where ontologies='eye_MA_1#eye_MeSH_2' and concept1='"+str[0]+"' and concept2='"+str[1]+"';");
            }
        }
        
        
        
        for(String accept:Acceptedmapping)
        {
            if(!suggestions.contains(accept))
                c++;
        }
        
        d=ConceptPairs.size()-(a+b+c);
    }
    
}
