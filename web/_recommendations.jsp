<%-- 
    Document   : recommendations
    Created on : Jan 5, 2011, 8:00:59 PM
    Author     : Shahab
--%>



<%@page import="se.liu.ida.sambo.factory.SavedPredefinedStrategiesSuggestionsDaoFactory"%>
<%@page import="se.liu.ida.sambo.dto.SavedPredefinedStrategiesSuggestionsPk"%>
<%@page import="se.liu.ida.sambo.dto.SavedPredefinedStrategiesSuggestions"%>
<%@page import="se.liu.ida.sambo.dao.SavedPredefinedStrategiesSuggestionsDao"%>
<%@page import="se.liu.ida.sambo.dto.SavedPredefinedStrategiesPk"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.util.Properties"%>
<%@page import="se.liu.ida.sambo.MModel.MOntology"%>
<%@page import="se.liu.ida.sambo.dto.SavedPredefinedStrategies"%>
<%@page import="se.liu.ida.sambo.factory.SavedPredefinedStrategiesDaoFactory"%>
<%@page import="se.liu.ida.sambo.dao.SavedPredefinedStrategiesDao"%>
<%@page import="se.liu.ida.sambo.factory.PredefinedStrategiesDaoFactory"%>
<%@page import="se.liu.ida.sambo.dao.PredefinedStrategiesDao"%>
<%@page import="se.liu.ida.sambo.dto.PredefinedStrategies"%>
<%@page import="org.apache.axis.utils.StringUtils"%>
<%@page import="javax.naming.spi.DirStateFactory.Result"%>
<%@page import="se.liu.ida.sambo.ui.SettingsInfo"%>
<%@page import="se.liu.ida.sambo.Merger.MergeManager"%>
<%@page import="se.liu.ida.sambo.session.Commons"%>
<%@page import="se.liu.ida.sambo.Merger.Constants"%>
<%@page import="se.liu.ida.sambo.Recommendation.Recommendation"%>
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
        <title>Recommendations</title>
        <script type="text/javascript">

            function redirectToMain()
            {
               
                var matchers_list = "";
                var weight_list = "";
                var totalstrategies = document.getElementById("hdnlength").value;
                System.out.println("in redirectToMain()");
                for(i=0;i< totalstrategies;i++)
                {
                    if (document.results.group[i].checked)
                    {
                        var matchers = document.getElementById("matchers"+i).value;
                        var weights = document.getElementById("weights"+i).value;
                        var threshold = document.getElementById("threshold"+i).value;
                        var matcher_array = matchers.split(";");
                        for(j=0; j < matcher_array.length; j++)
                            matchers_list += matcher_array[j] +"=on&";

                        var weights_enum = {"EditDistance":0,"NGram":1,"WL":2,"WN":3,"TermBasic":4,"WordNet":5,"UMLS":6,"Hierarchy":7,"Bayes":8};
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
                            if(matcher_array[k] == "WordNet")
                                weight_list += "weight"+weights_enum.WordNet+"="+weight_array[k] + "&";
                            if(matcher_array[k] == "UMLS")
                                weight_list += "weight"+weights_enum.UMLS+"="+weight_array[k] + "&";
                            if(matcher_array[k] == "Hierarchy")
                                weight_list += "weight"+weights_enum.Hierarchy+"="+weight_array[k] + "&";
                            if(matcher_array[k] == "Bayes")
                                weight_list += "weight"+weights_enum.Bayes+"="+weight_array[k] + "&";
                        }
                        System.out.println("http://"+ window.location.hostname + ":"+ window.location.port + Commons.CONTEXT_PATH + "/Main?step=4&"+matchers_list+"threshold="+threshold+"&"+weight_list+"start=Start");
                        window.opener.opener.location.href="http://"+ window.location.hostname + ":"+ window.location.port + Commons.CONTEXT_PATH + "/Main?step=4&"+matchers_list+"threshold="+threshold+"&"+weight_list+"start=Start";
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
                
                //Commons.PROPERTIES_NAME = request.getRealPath( "/" ).replace("\\build", "")+"WEB-INF\\lib\\sambo.properties";
                PredefinedStrategiesDao _dao = getPredefinedstrategiesDao();
                PredefinedStrategies _result[] = _dao.findAll();
                //Properties configFile = new Properties();
                //FileInputStream file = new FileInputStream(Commons.PROPERTIES_NAME );
                //configFile.load(file);
                //int count = 0;
                String Ontology1 = Commons.OWL_1, Ontology2 = Commons.OWL_2; 
                ClearSavedPredefinedStrategies();
                ClearSavedPredefinedStrategiesSuggestions();
                int numOfSegmPairs = 0;
                for (int i=0; i<_result.length; i++ )
                {
                    

                    SavedPredefinedStrategiesDao _daoS = getSavedPredefinedstrategiesDao();
                    SavedPredefinedStrategies _resultS[] = _daoS.findByDynamicWhere("ontology1='"+Commons.OWL_1.trim()+"' and ontology2='"+Commons.OWL_2.trim()+"' and matchers ='"+_result[i].getMatchers().trim()+"' and weights ='"+_result[i].getWeights().trim()+"' and threshold ='"+_result[i].getThreshold()+"'", null);
                   
                    // Step 1 and Step 2 will be performed only once because they are responsible for generating correct suggestions
                    /*if((_resultS[i].getMatchers().equals(_result[i].getMatchers()) && (!(_resultS[i].getOntology1().equals(Commons.OWL_1)) && !(_resultS[i].getOntology2().equals(Commons.OWL_2)))) ||
                       (!_resultS[i].getMatchers().equals(_result[i].getMatchers())) ||
                       (!_resultS[i].getMatchers().equals(_result[i].getMatchers()) && (_resultS[i].getOntology1().equals(Commons.OWL_1)) && (_resultS[i].getOntology2().equals(Commons.OWL_2))) ||
                       (!(_resultS[i].getOntology1().equals(Commons.OWL_1)) && !(_resultS[i].getOntology2().equals(Commons.OWL_2))) &&
                       count == 0)*/

                    // Recommendation Process
                    Recommendation recommendation = new Recommendation(settings.getURL(Constants.ONTOLOGY_1),settings.getURL(Constants.ONTOLOGY_2), 5, Commons.SEGMENT+"SubG/");
                    //Recommendation recommendation = new Recommendation(settings.getURL(Constants.ONTOLOGY_1),  settings.getURL(Constants.ONTOLOGY_2), Constants.NoOfSegmentPairs, Commons.SEGMENT + "SubG/");
                    
                   /*   Condition - 1
                    *   Matchers (Condition)= true - int(0)
                    *   Ontology Pairs (Condition) = false - int(-ve)
                    */

                    //if(LocateMatcherInDb(_result[i].getMatchers(),_resultS).equals("No") || Commons.LocatedSavedStrategy == 0)

                    //if(!recommendation.StrategyExist(_resultS.length) && i == 0) // if-statement should be executed once to generate segment pairs.
                   
                    if(i == 0)
                    {
                        /*
                         *  Segment Pair Generator
                         *  Step 1: Generate segment pair files from the given ontology files.
                         */
                         numOfSegmPairs = recommendation.GenerateSegmentPairs();
                         System.out.println("Number of segment pairs : " + numOfSegmPairs);
                        /*
                         *  Segment Pair Alignment Generator
                         *  Step 2: Generate alignment pair for segment pairs generated in Step 1
                         *          Using UMLS as Oracle for generating alignment
                         *          Alignment generated will be used as expected results
                         */
                        if(numOfSegmPairs == 0) break;
                        recommendation.SegmentPairsAlignmentGenerator(numOfSegmPairs);
                    }
                    
                    /*if(_resultS[i].getMatchers().compareTo(_result[i].getMatchers()) == 0)
                    {
                        if((_resultS[i].getOntology1().compareTo(Ontology1) < 0) && (_resultS[i].getOntology2().compareTo(Ontology2) < 0))
                        {
                            if(count == 0)
                            {
                                /*
                                 *  Segment Pair Generator
                                 *  Step 1: Generate segment pair files from the given ontology files.
                                 */
                                //recommendation.GenerateSegmentPairs();

                                /*
                                 *  Segment Pair Alignment Generator
                                 *  Step 2: Generate alignment pair for segment pairs generated in Step 1
                                 *          Using UMLS as Oracle for generating alignment
                                 *          Alignment generated will be used as expected results
                                 */
                                //recommendation.SegmentPairsAlignmentGenerator();

                                //count++;
                      /*      }
                        }
                    }
                   
                    else if(_resultS[i].getMatchers().compareTo(_result[i].getMatchers()) < 0)
                    {
                       /*   Condition - 2
                        *   Matchers (Condition)= false - int(-ve)
                        *   Ontology Pairs (Condition) = false - int(-ve)
                        */
                            // OR
                       /*  Condition - 3
                        *   Matchers (Condition)= false - int(-ve)
                        *   Ontology Pairs (Condition) = true - int(0)
                        */
                        /*if(count == 0)
                        {
                             /*
                             *  Segment Pair Generator
                             *  Step 1: Generate segment pair files from the given ontology files.
                             */
                            //recommendation.GenerateSegmentPairs();

                            /*
                             *  Segment Pair Alignment Generator
                             *  Step 2: Generate alignment pair for segment pairs generated in Step 1
                             *          Using UMLS as Oracle for generating alignment
                             *          Alignment generated will be used as expected results
                             */
                            /*recommendation.SegmentPairsAlignmentGenerator();

                            count++;
                        }
                    }*/
                    
                     /*
                      *  KitAMO Alignment Generator
                      *  Step 3: KitAMO alignment generator uses
                      *            Segments
                      *            Results from alignment generator
                      *            Matchers
                      *            Combination algorithm
                      *            Filter method
                      */
                    recommendation.KitAMOAlignmentGenerator(Constants.STEP_CLASS, numOfSegmPairs, _result[i].getId(),_result[i].getThreshold(),_result[i].getMatchers(),_result[i].getWeights(),_result[i].getSubweights());
                }
            }
            catch (Exception _e) { 
                    _e.printStackTrace();
            }
        %>
        <table border="0" align="center" width="100%" class="border_table">
            <tbody>
                <tr>
                    <td align="center">
                    <% out.print(getQualityAndExecutionMeasures()); %>
                    </td>
                </tr>
            </tbody>
        </table>
    </body>
</html>

<%!

    synchronized String getQualityAndExecutionMeasures(){

            String formStr = "<center> <strong>Results for Expected Recommendations <strong></center>";

            //out table
            formStr += "<br><TABLE  width=\"85%\" class =\"border_table\" align=\"center\">"
                    + "<tr><td valign=\"top\">";

            //form and table
            formStr += "<FORM name='results' method=POST action=\"Class\">"
                    + "<TABLE border=\"0\" width=\"100%\">";
                    
            try {
                    SavedPredefinedStrategiesDao _dao = getSavedPredefinedstrategiesDao();
                    SavedPredefinedStrategies _result[] = _dao.findByDynamicWhere("ontology1='"+Commons.OWL_1.trim()+"' and ontology2='"+Commons.OWL_2.trim()+"'",null);
                    if(_result.length > 0)
                    {
                        formStr += "<tr><td> <strong>S.No </strong></td><td> <strong>Ontology 1 </strong></td><td> <strong>Ontology 2 </strong></td><td><strong>Strategy </strong></td><td> <strong>Weights </strong></td><td><strong>Threshold </strong></td><td><strong> Precision </strong></td><td><strong> Recall </strong></td><td><strong> F-measure </strong></td><td><strong> Execution (E) </strong></td><td><strong>Recommendation Score</strong></td></tr>";
                    
                        for (int i=0; i<_result.length; i++ ) {
                            formStr +=  "<tr>" +
                                        "<td>" + (i+1) + "</td>" +
                                        "<td>"+_result[i].getOntology1()+"</td>"+
                                        "<td>"+_result[i].getOntology2()+"</td>"+
                                        "<td><input type=hidden id='matchers"+i+"' value='" + _result[i].getMatchers().trim() + "' />"+_result[i].getMatchers().trim()+"</td>" +
                                        //"<td>" + _result[i].getSubmatchers().trim() + "</td>" +
                                        "<td>(<input type='hidden' id='weights"+i+"' value='" + _result[i].getWeights().trim() + "' />"+_result[i].getWeights().trim()+")</td>" +
                                        //"<td>(" + _result[i].getSubweights().trim() + ")</td>" +
                                        "<td align=center><input type='hidden' id='threshold"+i+"' value='" + _result[i].getThreshold() + "' />"+_result[i].getThreshold()+"</td>" +
                                        "<td align=center><input type='hidden' id='precision"+i+"' value='" + _result[i].getPrecision1() + "' />"+_result[i].getPrecision1()+"</td>" +
                                        "<td align=center><input type='hidden' id='recall"+i+"' value='" + _result[i].getRecall() + "' />"+_result[i].getRecall()+"</td>" +
                                        "<td align=center><input type='hidden' id='fmeasure"+i+"' value='" + _result[i].getFmeasure()+ "' />"+_result[i].getFmeasure()+"</td>" +
                                        //"<td align=center>" + Commons.qualityMeasureList.get(i).trim() + "</td>" +
                                        "<td align=center><input type='hidden' id='execution"+i+"' value='" + _result[i].getQuality() + "' />"+_result[i].getQuality()+"</td>"+
                                        "<td align=center><input type='hidden' id='recommendation_score"+i+"' value ='" +  _result[i].getFmeasure() + "' />"+ _result[i].getFmeasure()+"</td>"+
                                        //"<td align=center><input type='button' name='precision & recall' value='View Precision & Recall' onclick=\"javascript:window.open('precision_recall.jsp?id="+i+"','Precision and Recall','toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width=800, height=400');\" /></td>"+
                                        "<td><input type='radio' name='group' value='Strategy "+i+"'></td>"+
                                        "</tr>";
                        }
                        formStr += "<tr><td>&nbsp;<input type=hidden id='hdnlength' value='"+_result.length+"'><td></tr>"+
                                "<tr><input type='button' value='Start' onclick='return redirectToMain();'></tr>";
                    }
                    else
                        formStr += "<tr><td align='center' style='color:red'>Cannot generate recommendations, since no segment pairs can be generated for the selected pair of ontolgoies! </td></tr>";

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

     /**
     * Method 'getSavedPredefinedstrategiesDao'
     *
     * @return SavedPredefinedstrategiesDao
     */
    public static SavedPredefinedStrategiesDao getSavedPredefinedstrategiesDao()
    {
         return SavedPredefinedStrategiesDaoFactory.create();
    }


    /**
     * Method 'getSavedPredefinedstrategiesSuggestionsDao'
     *
     * @return SavedPredefinedstrategiesDao
     */
    public static SavedPredefinedStrategiesSuggestionsDao getSavedPredefinedstrategiesSuggestionsDao()
    {
         return SavedPredefinedStrategiesSuggestionsDaoFactory.create();
    }



    /**
     * Method 'getPredefinedstrategiesDao'
     *
     * @return PredefinedstrategiesDao
     */
    public static PredefinedStrategiesDao getPredefinedstrategiesDao()
    {
         return PredefinedStrategiesDaoFactory.create();
    }

    /*
     *  Method: Locate Matchers in Database
     */

    public static String LocateMatcherInDb(String _result,SavedPredefinedStrategies[] _resultS)
    {
        String Ontology1 = Commons.OWL_1, Ontology2 = Commons.OWL_2;
        int signalCountNo = 0;
        int signalCountYes = 0;
        int signal = 0;
        String answer = "Yes";

        for(int i=0; i < _resultS.length; i++)
        {
            // To catch "no" signals
            if(_result.compareTo(_resultS[i].getMatchers()) < 0)
            {
                signalCountNo++;
            }
            // To catch "yes" signals
            else
            {
                signalCountYes++;
                Commons.LocatedSavedStrategy = LocateSavedStrategyInDbwrtOntology(Ontology1,Ontology2,_resultS[i].getMatchers());
                break;
            }
        }

        if(signalCountNo == _resultS.length)
            answer = "No"; // it means that there is no such matcher in the database

        return answer;
    }

    /*
     *  Method: Clear Saved Predefined Strategies
     */
    public static void ClearSavedPredefinedStrategies()
    {
     
        try
        {
            SavedPredefinedStrategiesDao _daoS = getSavedPredefinedstrategiesDao();
            SavedPredefinedStrategies _result[] = _daoS.findAll();
            for (int i=0; i<_result.length; i++ )
            {
                SavedPredefinedStrategiesPk _dpk = new SavedPredefinedStrategiesPk(_result[i].getId());
                _daoS.delete(_dpk);
            }
        }
        catch (Exception _e)
        {
            _e.printStackTrace();
        } 
    }

    /*
     *  Method: Clear Saved Predefined Strategies Suggestions
     */
    public static void ClearSavedPredefinedStrategiesSuggestions()
    {
       try
        {
            SavedPredefinedStrategiesSuggestionsDao _daoS = getSavedPredefinedstrategiesSuggestionsDao();
            SavedPredefinedStrategiesSuggestions _result[] = _daoS.findAll();
            for (int i=0; i<_result.length; i++ )
            {
                SavedPredefinedStrategiesSuggestionsPk _dpk = new SavedPredefinedStrategiesSuggestionsPk(_result[i].getId());
                _daoS.delete(_dpk);
            }
        }
        catch (Exception _e)
        {
            _e.printStackTrace();
        }

    }


    /*
     *  Method: Locate Saved Strategy in Database wrt Ontology
     */
    public static int LocateSavedStrategyInDbwrtOntology(String Ontology1,String Ontology2,String Matchers)
    {
        int located = 0;
        try
        {
            SavedPredefinedStrategiesDao _daoS = getSavedPredefinedstrategiesDao();
            SavedPredefinedStrategies _resultS[] = _daoS.findByDynamicWhere("ontology1='"+Ontology1+"' and ontology2='"+Ontology2+"' and matchers ='"+Matchers+"'", null);
            if((_resultS[0].getOntology1().compareTo(Ontology1) == 0) && (_resultS[0].getOntology2().compareTo(Ontology2) ==0))
            {
                located = _resultS[0].getId();
            }
        }
        catch (Exception _e)
        {
            _e.printStackTrace();
        }
        return located;
    }
%>