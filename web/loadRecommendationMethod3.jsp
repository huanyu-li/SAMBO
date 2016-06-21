<%-- 
    Document   : loadRecommendatioMethod3
    Created on : Feb 9, 2012, 7:35:48 PM
    Author     : Rajaram
--%>

<%@page import="java.util.HashMap"%>
<%@page import="se.liu.ida.sambo.Recommendation.RecommendationConstants"%>
<%@page import="se.liu.ida.sambo.jdbc.recommendation.AccessRecommendationDB"%>
<%@page import="java.util.ArrayList"%>
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
int SavedStrategyId = 0;



%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
        <title>Saved Recommendations 3</title>
        <script type="text/javascript">

            function redirectToMain()
            {
               
                var matchers_list = "";
                var weight_list = "";
                var totalstrategies = document.getElementById("hdnlength").value;
                var contextpath = document.getElementById("hdnPath").value;
                for(i=0;i< totalstrategies;i++)
                {
                    if (document.results.group[i].checked)
                    {
                        var matchers = document.getElementById("matcher"+i).value;
                        var weights = document.getElementById("weight"+i).value;
                        var combination = document.getElementById("combination"+i).value;
                        var threshold = document.getElementById("threshold"+i).value;
                        
                        var threshold_array = threshold.split(";");
                        
                        
                        var Single_threshold=true;
                        var upper_threshold=0,lower_threshold=0,threshold=0;
                        
                        var threshold_type="single";
                        
                        if(threshold_array.length>1)
                            {
                                Single_threshold=false;
                                threshold_type="double"
                                lower_threshold=threshold_array[0];
                                upper_threshold=threshold_array[1];
                                
                            }
                            else
                                threshold=threshold_array[0];
                            
                            
                
                
                        
                        
                        var matcher_array = matchers.split(";");
                        for(j=0; j < matcher_array.length; j++)
                            matchers_list += matcher_array[j] +"=on&";

                        var weights_enum = {"EditDistance":0,"NGram":1,"WL":2,"WN":3,"TermBasic":4,"TermWN":5,"UMLSKSearch":6,"Hierarchy":7,"BayesLearning":8};
                        var weight_array = weights.split(";");
                        
                        for(k=0; k < weight_array.length; k++)
                        {
                            if(matcher_array[k] == "EditDistance")
                                weight_list += "weight"+weights_enum.EditDistance+"="+weight_array[k] + "&";
                            if(matcher_array[k] == "NGram")
                                weight_list += "weight"+weights_enum.NGram+"="+weight_array[k] + "&";
                            if(matcher_array[k] == "WL")
                                weight_list += "weight"+weights_enum.WL+"="+weight_array[k] + "&";
                            if(matcher_array[k] == "WN")
                                weight_list += "weight"+weights_enum.WN+"="+weight_array[k] + "&";
                            if(matcher_array[k] == "TermBasic")
                                weight_list += "weight"+weights_enum.TermBasic+"="+weight_array[k] + "&";
                            if(matcher_array[k] == "TermWN")
                                weight_list += "weight"+weights_enum.TermWN+"="+weight_array[k] + "&";
                            if(matcher_array[k] == "UMLSKSearch")
                                weight_list += "weight"+weights_enum.UMLSKSearch+"="+weight_array[k] + "&";
                            if(matcher_array[k] == "Hierarchy")
                                weight_list += "weight"+weights_enum.Hierarchy+"="+weight_array[k] + "&";
                            if(matcher_array[k] == "BayesLearning")
                                weight_list += "weight"+weights_enum.BayesLearning+"="+weight_array[k] + "&";
                        }
                        
                        
                        var GOTO_Link="";
                        
                        if(Single_threshold==true)
                            {
                                
                            GOTO_Link=("http://"+ window.location.hostname + ":"+ window.location.port + 
                                contextpath + "/Main?step=4&"+matchers_list+"threshold="+threshold+"&"+weight_list
                                +"start=Start"+"&combination="+combination+"&threshold_flag="+threshold_type);
                            
                            
                            //window.opener.opener.location.href="http://"+ window.location.hostname + ":"+ window.location.port + contextpath + "/Main?step=4&"+matchers_list+"threshold="+threshold+"&"+weight_list+"start=Start"+"&combination="+combination+"&threshold_flag="+threshold_type;
                            }
                        else
                             {
                                 
                             GOTO_Link=("http://"+ window.location.hostname + ":"+ window.location.port + contextpath + "/Main?step=4&"+matchers_list+"threshold="+threshold+"&"+weight_list+"start=Start"+"&combination="+combination+"&threshold_flag="+threshold_type+"&double_threshold_upper="+upper_threshold+"&double_threshold_lower="+lower_threshold);
                            //window.opener.opener.location.href="http://"+ window.location.hostname + ":"+ window.location.port + contextpath + "/Main?step=4&"+matchers_list+"threshold="+threshold+"&"+weight_list+"start=Start"+"&combination="+combination+"&threshold_flag="+threshold_type+"&double_threshold_upper="+upper_threshold+"&double_threshold_lower="+lower_threshold;
                    
                            }
                            
                        //document.write(GO_Link);
                            
                        
                        
                        window.opener.opener.location.href=GOTO_Link;    
                        window.opener.close();
                        //System.out.println(window.opener.opener.location.href);
                        self.close();
                    }
                }
                return true;
            }

        </script>
    </head>
    <body>
        <%

            try
            {
                

                String Ontology1 = Commons.OWL_1, Ontology2 = Commons.OWL_2; 

            }
            catch (Exception _e) { 
                    _e.printStackTrace();
            }
        %>
        <table border="0" align="center" width="100%" class="border_table">
            <tbody>
                <tr>
                    <td align="center">
                    <% out.print(getRecommendationsFromDB()); %>
                    </td>
                </tr>
            </tbody>
        </table>
    </body>
</html>

<%!

    synchronized String getRecommendationsFromDB(){

            String formStr = "<center> <strong>Results for Expected Recommendations <strong></center>";

            //out table
            formStr += "<br><TABLE  width=\"85%\" class =\"border_table\" align=\"center\">"
                    + "<tr><td valign=\"top\">";

            //form and table
            formStr += "<FORM name='results' method=POST action=\"Class\">"
                    + "<TABLE border=\"0\" width=\"100%\">";
                    
            try {
                    //PRAAsOracleSavedPredefinedStrategiesDao _dao = getSavedPredefinedstrategiesDao();
                    //PRAAsOracleSavedPredefinedStrategies _result[] = _dao.findByDynamicWhere("ontology1='"+Commons.OWL_1.trim()+"' and ontology2='"+Commons.OWL_2.trim()+"'",null);
                
                AccessRecommendationDB storeINDB= new AccessRecommendationDB(RecommendationConstants.RECOMMENDATION_METHOD3);
                
   
                String Ontology1 = Commons.OWL_1, Ontology2 = Commons.OWL_2;                
                
                String ontologies=(Ontology1+"#"+Ontology2);               
                                
                ArrayList<HashMap>_result= storeINDB.select(ontologies,RecommendationConstants.DISPLAY_PARAMS);
                
                
                    if(_result.size() > 0)
                    {
                       formStr += "<tr><td> <strong>S.No </strong></td>";
                       
                       for(String s:RecommendationConstants.DISPLAY_PARAMS)                       
                       formStr += "<td> <strong>"+s+"</strong></td>";
                       
                       
                               
                        formStr += "</tr>";
                    
                        for (int i=0; i<_result.size(); i++ ) {
                            formStr +=  "<tr>" +
                                        "<td>" + (i+1) + "</td>" ;
                            
                            
                            HashMap data =_result.get(i);
                            
                            for(String parms : RecommendationConstants.DISPLAY_PARAMS)
                            formStr +="<td><input type=hidden id='"+parms+i+"' value='" +data.get(parms)+ "' />"+data.get(parms)+"</td>";
                             
                            formStr +="<td><input type='radio' name='group' value='Strategy "+i+"'></td>";
                            
                                 formStr +="</tr>";
                        }
                        
                        
                        
                        
                        formStr += "<tr><td>&nbsp;<input type=hidden id='hdnlength' value='"+_result.size()+"'><input type=hidden id='hdnPath' value='"+Commons.CONTEXT_PATH+"'><td></tr>"+
                                "<tr><input type='button' value='Start' onclick='javascript:return redirectToMain();'></tr>";
                    }
                    else
                        formStr += "<tr><td align='center' style='color:red'>Cannot find saved recommendations 3 for the selected pair of ontolgoies! </td></tr>";

		storeINDB.closeConnection();
            }
		catch (Exception _e) {
			_e.printStackTrace();
		}

            formStr += " </table>"
                    + "</tr></table></td> </tr>";

          
            // Close table and form
            formStr += "</TABLE></FORM>";


            // Close outer table
            formStr += "</td></tr></TABLE>";            
            

            return formStr;
    }

%>
