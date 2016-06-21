<%-- 
    Document   : strategies
    Created on : Jun 29, 2012, 4:13:34 PM
    Author     : Rajaram
--%>

<%@page import="se.liu.ida.sambo.util.testing.GenerateAlignmentStrategies"%>
<%@page import="se.liu.ida.sambo.Recommendation.RecommendationConstants"%>
<%@page import="java.util.ArrayList"%>
<%@page import="se.liu.ida.sambo.algos.matching.algos.AlgoConstants"%>
<%@page import="se.liu.ida.sambo.Merger.Constants"%>
<%@page contentType="text/html" pageEncoding="windows-1252"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
       
        <title>Recommendations</title>
    </head>
    <body>
        
            
                
                    <% out.print(getPredefinedStrategies()); %>
                   
                
                
            
<!--        </table>-->
    </body>
</html>

<%!

synchronized String getPredefinedStrategies(){

    
            //form and table
    
    
            String formStr ="<table border=\"0\" align=\"center\" width=\"50%\" class=\"border_table\" frame=\"box\">";
            formStr += "<FORM method=POST action=\"Class\">";            
            
            
         
            /**
             * If the user want to generate recommendation 1 
             * the method which uses segment pairs and UMLS as an oracle.
             */
            if(!RecommendationConstants.DO_RECOMMENDATION_MTH2 && !RecommendationConstants.DO_RECOMMENDATION_MTH3)
            {
                
            formStr += "<center><strong><br><br><h2>Use recommendations / Generate recommendation 1 (segment pairs & UMLS oracle)<br></h2><strong></strong></strong></center>";
                   
            formStr += "<tr><td align=\"center\"><input type='button' name='Recommendations' value='Generate recommendation 1 (segment pairs & UMLS oracle)' onclick=\"javascript:window.open('recommendationMethod1.jsp','_blank','toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width=1200, height=400');\" /></td>";
            
            
            formStr += "<td><input type='button' name='SavedRecommendationsSegPairANDUMLS' value='Use recommendation 1 (segment pairs & UMLS oracle)' onclick=\"javascript:window.open('loadRecommendationMethod1.jsp','_blank','toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width=1200, height=400');\" />";
            formStr += "<br><input type='button' name='SavedRecommendationsSegPairANDDeci' value='Use recommendation 2 (segment pairs & validated suggestions)' onclick=\"javascript:window.open('loadRecommendationMethod2.jsp','_blank','toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width=1200, height=400');\" />";
            formStr += "<br><input type='button' name='SavedRecommendationsDeci' value='Use recommendation 3 (validated suggestions alone)' onclick=\"javascript:window.open('loadRecommendationMethod3.jsp','_blank','toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width=1200, height=400');\" /></td></tr>";
            
           
           
            }
            
                     
            
            /**
             * If the user want to generate recommendation 2, the method which 
             * uses segment pairs and decision on mapping suggestions as an 
             * oracle.
             */
            else if(RecommendationConstants.DO_RECOMMENDATION_MTH2)
            {
                
                
             formStr += "<center><strong><br><br><h2>Generate recommendation 2 (segment pairs & validated suggestions)<br></h2><strong></strong></strong></center>";
                
                
             formStr += "<tr><td align=\"center\"><input type='button' name='Recommendations' value='Generate recommendation 2' onclick=\"javascript:window.open('recommendationMethod2.jsp','_blank','toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width=1200, height=400');\" /><br><br></td></tr>";
            
             
                                    
            }
                        
            
            /**
             * If the user want to generate recommendation 3 
             * the method which uses decision on mapping suggestions alone.
             */
            else if(RecommendationConstants.DO_RECOMMENDATION_MTH3)
            {
                formStr += "<center><strong><br><br><h2>Generate recommendation 3 (validated suggestions alone)<br></h2><strong></strong></strong></center>"; 
                
                
                formStr += "<tr><td align=\"center\"><input type='button' name='Recommendations' value='Generate recommendation 3' onclick=\"javascript:window.open('recommendationMethod3.jsp','_blank','toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width=1200, height=400');\" /><br><br></td></tr>";
            
                
            }
            
            formStr +="</form>";
            
            formStr+="</table>";
             
            
            formStr += "<center><strong><br><br><h2>List of Predefined Strategies <br><br></h2><strong></strong></strong></center>";

            
            
            
            formStr +="<table border=\"0\" align=\"center\" width=\"50%\" class=\"border_table\">";          
            
            
                    formStr += "<tr><td> <strong>s.no </strong></td><td><strong> strategy </strong> </td><td><strong> weights </strong></td><td><strong>combination</strong></td><td><strong>threshold</strong></td></tr>";
            try {
                    //PredefinedStrategiesDao _dao = getPredefinedstrategiesDao();
                    //PredefinedStrategies _result[] = _dao.findAll();
                
                ArrayList<String> result=new ArrayList();
          
                GenerateAlignmentStrategies test=new GenerateAlignmentStrategies();
                result=test.getStrategies();
                
                    for (int i=0; i<result.size(); i++ ) {
                        
                        
                        String strategie=result.get(i);
                
                    String Straparams[]=strategie.split(",");
                    
                    String matchers = Straparams[0];
                    String weights = Straparams[1];
                   
                    String combination = Straparams[2];
                    String thresholds = Straparams[3];
                        
                        
                        formStr +=  "<tr>" +
                                    "<td>" + (i+1) + "</td>" +
                                    "<td>" + matchers + "</td>" +
                                    "<td>(" + weights + ")</td>" +
                                    "<td>" + combination + "</td>"+
                                    "<td>" + thresholds + "</td>"+
                                    "</tr>";
                    }
                formStr += "</table>";
		}
		catch (Exception _e) {
			_e.printStackTrace();
		}
            
            
                   

           



            



            return formStr;
}
%>