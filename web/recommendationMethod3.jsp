<%-- 
    Document   : recommendationMethod3
    Created on : Feb 20, 2012, 2:10:51 PM
    Author     : Rajaram
--%>



<%@page import="se.liu.ida.sambo.session.Commons"%>
<%@page import="se.liu.ida.sambo.util.testing.GenerateAlignmentStrategies"%>
<%@page import="se.liu.ida.sambo.Recommendation.RecommendationMethod3"%>
<%@page import="se.liu.ida.sambo.jdbc.recommendation.AccessRecommendationDB"%>
<%@page import="se.liu.ida.sambo.Recommendation.RecommendationConstants"%>
<%@page import="java.util.ArrayList"%>
<%@page import="se.liu.ida.sambo.algos.matching.algos.AlgoConstants"%>
<%@page import="se.liu.ida.sambo.jdbc.ResourceManager"%>
<%@page import="java.sql.Connection"%>
<%@page import="se.liu.ida.sambo.ui.SettingsInfo"%>
<%@page import="se.liu.ida.sambo.Merger.MergeManager"%>
<%@page contentType="text/html" pageEncoding="windows-1252"%>



<%
session = request.getSession(false);
AlgoConstants.ISRECOMMENDATION_PROCESS=true;
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
        <title>Recommendation method 3</title> 
        <script language="JavaScript">
         function closeParent() {           
             
             window.opener.close();
     }
     
     closeParent();
           </script> 
         </head>
        
            <body>
                
                <table border="0" align="center" width="100%" class="border_table">
            <tbody>
        <%

                //int size=0;   
                String Ontology1 = Commons.OWL_1, Ontology2 = Commons.OWL_2;
                ArrayList<String> result=new ArrayList();
                
                ArrayList<String> INSERT_STATEMENT=new ArrayList();
                
                String ontologies=(Ontology1+AlgoConstants.SEPERATOR+Ontology2);
                
               AccessRecommendationDB storeINDB = 
                       new AccessRecommendationDB
                       (RecommendationConstants.RECOMMENDATION_METHOD3);
                
                storeINDB.clearTable(ontologies);
          
                // while(size!=210)
                //{
                    //result 
                    GenerateAlignmentStrategies test 
                            = new GenerateAlignmentStrategies();
                    result = test.getStrategies();
                    
                    result = test.loadFromDB();
                   // size=result.size();
                // }
                
                
                RecommendationMethod3 calculateScore
                        = new RecommendationMethod3();

                


                
                out.println("<br><br><h3>Score</h3><br>");
                out.println("<br><br><h3>"+result.size()+"</h3><br>");
               // out.println("<table border='1' cellspacing='0' width='300' align='center'>"+
                        out.println("<tr><b>"+  
                        "<td>S.No</td>"+
                        "<td>Strategies</td>"+        
                        "<td>A</td>"+ 
                        "<td>B</td>"+ 
                        "<td>C</td>"+
                        "<td>D</td>"+                        
                        "<td>RecallC</td>"+
                        "<td>PrecisionC</td>"+
                        "<td>FmeasureC</td>"+                                       
                        "<td>Score1</td>"+
                        "<td>Score2</td>"+
                        "</b></tr>");
                
                
                
                long t1 = System.currentTimeMillis();
                
                int i = 1;
                for (String s:result)
                {
                    
                    out.println("<tr><td>"+(i)+"</td>");
                    out.println("<td>"+s+"</td>");
                    i++;
                    String Straparams[]=s.split(",");
                    //out.println(_result[i].getId()+" "+_result[i].getThreshold()+" "+_result[i].getMatchers()+" "+_result[i].getWeights()+" "+_result[i].getSubweights());
                    
                    double[] scoreParam = calculateScore.getParams(Straparams[0], Straparams[1], Straparams[2], Straparams[3]);
                    
                    for(int j=0;j<scoreParam.length;j++)
                                               {
                        out.println("<td>"+(int)scoreParam[j]+"</td>");
                    }
                    
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
                    
                    
                     
                    
                     
                    out.println("<td>"+recallcorr+"</td>");
                    out.println("<td>"+precisioncorr+"</td>"); 
                    out.println("<td>"+fmeasurecorr+"</td>");                     
                    out.println("<td>"+score1Rnd+"</td>");
                    out.println("<td>"+score2Rnd+"</td></tr>");                    
                    
                    
                    
                    
                    
                    String statement = storeINDB.generateInsertStatement(
                            ontologies, Straparams, scoreParam, recommParams);
                    
                    INSERT_STATEMENT.add(statement);
                    
                    
                    if(INSERT_STATEMENT.size()>25)
                    {
                        storeINDB.insert(INSERT_STATEMENT);
                        INSERT_STATEMENT.clear();
                    }
                    
                    //out.println("Score"+calculateScore.calculateRecommendationScore(_result[i].getMatchers(), _result[i].getWeights(), _result[i].getThreshold()));
                    
                   
                    
                    //out.println("<br>");
                }
                
                out.println("<tbody></table>");
                
                //updateDB();
                
                if(INSERT_STATEMENT.size()>0)
                {
                storeINDB.insert(INSERT_STATEMENT);
                }
                
                storeINDB.closeConnection();
            
            long t2 = System.currentTimeMillis();
            System.out.println( "Time Taken is " + (t2-t1) + " ms" );
            
            
            RecommendationConstants.DO_RECOMMENDATION_MTH3=false;
            AlgoConstants.ISRECOMMENDATION_PROCESS=false;
            
            //out.println("<h1> Recommedation in done!!!</h1>");
            
            
       %>

        

     </body>
     </html>
