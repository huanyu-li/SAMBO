<%-- 
    Document   : recommendationMethod2
    Created on : Feb 8, 2012, 9:12:19 PM
    Author     : Rajaram
--%>


<%@page import="se.liu.ida.sambo.util.testing.GenerateAlignmentStrategies"%>
<%@page import="se.liu.ida.sambo.Recommendation.RecommendationMethod2"%>
<%@page import="java.util.HashMap"%>
<%@page import="se.liu.ida.sambo.jdbc.recommendation.AccessRecommendationDB"%>
<%@page import="se.liu.ida.sambo.Recommendation.RecommendationConstants"%>
<%@page import="se.liu.ida.sambo.jdbc.ResourceManager"%>
<%@page import="java.util.ArrayList"%>
<%@page import="se.liu.ida.sambo.algos.matching.algos.AlgoConstants"%>
<%@page import="se.liu.ida.sambo.MModel.MOntology"%>
<%@page import="se.liu.ida.sambo.ui.SettingsInfo"%>
<%@page import="se.liu.ida.sambo.Merger.MergeManager"%>
<%@page import="se.liu.ida.sambo.session.Commons"%>
<%@page import="se.liu.ida.sambo.Merger.Constants"%>
<%@page contentType="text/html" pageEncoding="windows-1252"%>
<%
session = request.getSession(false);
MergeManager merge = (MergeManager)session.getAttribute(session.getId());
SettingsInfo settings = (SettingsInfo)session.getAttribute("settings");
AlgoConstants.ISRECOMMENDATION_PROCESS = true;
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
        <title>Recommendation method 2</title> 
        <script language="JavaScript">
         function closeParent() {            
             
             window.opener.close();
}
            
           </script> 
    </head>
    <body onload="closeParent()">
        <%

            try
            {
                ArrayList<String> result=new ArrayList();
                
                ArrayList<String> INSERT_STATEMENTS=new ArrayList();

                GenerateAlignmentStrategies test=new GenerateAlignmentStrategies();
                result=test.getStrategies();
                
                
                

                String Ontology1 = Commons.OWL_1, Ontology2 = Commons.OWL_2;                
                String ontologies=(Ontology1+AlgoConstants.SEPERATOR+Ontology2);
                
               AccessRecommendationDB storeINDB= new AccessRecommendationDB(RecommendationConstants.RECOMMENDATION_METHOD2);
                
                storeINDB.clearTable(ontologies);
                
                
                int numOfSegmPairs = 0;
                
                ArrayList<String> RecommendationType1= new ArrayList();
                
                ArrayList<String> RecommendationType2= new ArrayList();
                
                
                 // Recommendation Process
                 RecommendationMethod2 recommendation = new 
                         RecommendationMethod2(settings.getURL
                         (Constants.ONTOLOGY_1),settings.getURL
                         (Constants.ONTOLOGY_2), 5, Commons.SEGMENT+"SubG/");
                   
                
                for (int i=0; i<result.size(); i++ )
                {


                   
                   /*   Condition - 1
                    *   Matchers (Condition)= true - int(0)
                    *   Ontology Pairs (Condition) = false - int(-ve)
                    */

                    
                   
                    if(i == 0)
                    {
                        /*
                         *  Segment Pair Generator
                         *  Step 1: Generate segment pair files from the given ontology files.
                         */
                         numOfSegmPairs = recommendation.generateSegmentPairs();
                         System.out.println("Number of segment pairs : " + numOfSegmPairs);
                        /*
                         *  Segment Pair Alignment Generator
                         *  Step 2: Generate alignment pair for segment pairs generated in Step 1
                         *          Using UMLS as Oracle for generating alignment
                         *          Alignment generated will be used as expected results
                         */
                        if(numOfSegmPairs == 0) break;
                        
                    }

                    
                     /*
                      *  KitAMO Alignment Generator
                      *  Step 3: KitAMO alignment generator uses
                      *            Segments
                      *            Results from alignment generator
                      *            Matchers
                      *            Combination algorithm
                      *            Filter method
                      */
                    
                    String strategie=result.get(i);
                
                    String Straparams[]=strategie.split(",");
                    
                    String matchers = Straparams[0];
                    String weights = Straparams[1];
                    String subweights = "";
                    String combination = Straparams[2];
                    String thresholds = Straparams[3];
                    
                    
                    double[] scoreParam = recommendation.getParams
                            (Straparams[0], Straparams[1], Straparams[2],
                            Straparams[3]);
                    
                    if(scoreParam != null) {
                        float p = (float)Math.pow(10,2);
                    
                    double precisionC=(scoreParam[0]/(scoreParam[0]+scoreParam[1]));
                    float precisioncorr=((float)Math.round((precisionC*p))/100); 
                    
                    double recallC=(scoreParam[0]/(scoreParam[0]+scoreParam[2]));
                    float recallcorr=((float)Math.round((recallC*p))/100); 
                           
                    double fmeasureC=(recallC*precisionC*2)/(recallC+precisionC);   
                    float fmeasurecorr=((float)Math.round((fmeasureC*p))/100); 
                           
                           
                           
                    
                    double precisionW=(scoreParam[3]/(scoreParam[2]+scoreParam[3]));
                    float precisionwrog=((float)Math.round((precisionW*p))/100); 
                           
                    double recallW=(scoreParam[3]/(scoreParam[1]+scoreParam[3]));
                    float recallwrog=((float)Math.round((recallW*p))/100);
                            
                    double fmeasureW=(recallW*precisionW*2)/(recallW+precisionW);
                    float fmeasurewrog=((float)Math.round((fmeasureW*p))/100);  
                            
                            
                    double score1= (scoreParam[0]+scoreParam[3])/(scoreParam[0]+scoreParam[1]+scoreParam[2]+scoreParam[3]);
                    float score1Rnd=((float)Math.round((score1*p))/100);  
                    
                    double score2= scoreParam[0]/(scoreParam[0]+scoreParam[1]+scoreParam[2]);
                    float score2Rnd=((float)Math.round((score2*p))/100); 
                    
                    float [] recommParams = {recallcorr, precisioncorr, 
                    fmeasurecorr, recallwrog, precisionwrog, fmeasurewrog,
                    score1Rnd, score2Rnd};
                    
                    String statement = storeINDB.generateInsertStatement(
                            ontologies, Straparams, scoreParam, recommParams);
                    
                    INSERT_STATEMENTS.add(statement);
                    }
                    
                    
                                       
                    if(INSERT_STATEMENTS.size()>100)
                    {
                        storeINDB.insert(INSERT_STATEMENTS);
                        INSERT_STATEMENTS.clear();
                        System.out.println("Data inserted into DB");
                    }
                }

                
                if(INSERT_STATEMENTS.size()>0)
                    {
                        storeINDB.insert(INSERT_STATEMENTS);
                        System.out.println("Data inserted into DB");
                        
                    }
                
                ResourceManager.close(RecommendationConstants.SQL_CONN);
                storeINDB.closeConnection();
                
                
            }
            catch (Exception _e) { 
                    _e.printStackTrace();
            }
        %>
        
        
        <strong><center>Results for Expected Recommendations</center><br><br></strong>
        
        <table border="0" align="center" width="100%" class="border_table">
                               
                    <% out.print(getQualityAndExecutionMeasures()); %>
                    
        </table>
    </body>
    <%
    AlgoConstants.ISRECOMMENDATION_PROCESS=false;
    RecommendationConstants.DO_RECOMMENDATION_MTH2=false;  
    %>
</html>

<%!

    synchronized String getQualityAndExecutionMeasures(){

           
            String formStr="";   
                    
            try {
                    
   AccessRecommendationDB storeINDB= new AccessRecommendationDB(RecommendationConstants.RECOMMENDATION_METHOD2);
                
   
   String Ontology1 = Commons.OWL_1, Ontology2 = Commons.OWL_2;                
                
                String ontologies=(Ontology1+AlgoConstants.SEPERATOR+Ontology2);
                
                 String[] display_params={"ontologies","matcher","weight","combination","threshold","recallCorrect","precisionCorrect","fmeasureCorrect","score1","score2"};
                    
   
                 
   
                                  
                ArrayList<HashMap>_result= storeINDB.select(ontologies,RecommendationConstants.DISPLAY_PARAMS);
                
                
                if(_result.size() > 0)
                    {
                    
                   
                       formStr += "<tr><td> <strong>S.No </strong></td>";
                       
                       for(String s:display_params)                       
                       formStr += "<td> <strong>"+s+"</strong></td>";
                       
                       
                               
                        formStr += "</tr>";
                    
                        for (int i=0; i<_result.size(); i++ ) {
                            formStr +=  "<tr>" +
                                        "<td>" + (i+1) + "</td>" ;
                            
                            
                             HashMap data =_result.get(i);
                            
                            for(String parms : RecommendationConstants.DISPLAY_PARAMS)
                                formStr +="<td>"+data.get(parms)+"</td>";
                            
                                 formStr +="</tr>";
                        }
                        }
                    else
                        formStr += "<tr><td align='center' style='color:red'>Cannot generate recommendations, since no segment pairs can be generated for the selected pair of ontolgoies! </td></tr>";

		}
		catch (Exception _e) {
			_e.printStackTrace();
		}         

            

            return formStr;
    }

     
%>
