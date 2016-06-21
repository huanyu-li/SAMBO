<%-- 
    Document   : FindMappableConceptPairs
    Created on : May 8, 2012, 4:44:07 PM
    Author     : Rajaram
--%>

<%@page import="se.liu.ida.sambo.jdbc.MappableConceptPairsDB"%>
<%@page import="se.liu.ida.sambo.algos.matching.algos.AlgoConstants"%>
<%@page import="java.sql.SQLException"%>
<%@page import="se.liu.ida.sambo.jdbc.ResourceManager"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.util.ArrayList"%>
<%@page import="se.liu.ida.PRAalg.mgPRA"%>
<%@page import="se.liu.ida.sambo.MModel.MOntology"%>
<%@page import="se.liu.ida.sambo.util.History"%>
<%@page import="se.liu.ida.sambo.MModel.MClass"%>
<%@page import="java.util.regex.Matcher"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="se.liu.ida.sambo.MModel.util.NameProcessor"%>
<%@ page import="org.w3c.dom.*" %>
<%@ page import="javax.xml.parsers.DocumentBuilder" %>
<%@ page import="javax.xml.parsers.DocumentBuilderFactory" %>
<%@ page import="org.w3c.dom.Document" %>
<%@ page import="org.w3c.dom.Element"%>
<%@ page import="java.io.File"%>
<%@ page import="java.io.FileInputStream"%>
<%@ page import="java.util.Properties"%>
<%@ page import="se.liu.ida.sambo.PRA.PRA"%>
<%@ page contentType="text/html" pageEncoding="windows-1252"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="se.liu.ida.sambo.session.Commons"%>
<%@page language="java" import="se.liu.ida.sambo.ui.web.*, se.liu.ida.sambo.ui.SettingsInfo, se.liu.ida.sambo.Merger.MergeManager, se.liu.ida.sambo.util.Pair, se.liu.ida.sambo.MModel.MElement, java.util.Vector, java.util.Enumeration" %>



<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; 
              charset=windows-1252">
        <title>Find mappable concept pairs</title>
    </head>
    <body>
        <%
          PRA op = new PRA();                  
          MOntology monto1 = op.monto1, monto2 = op.monto2;
          Vector praSugg = op.praSugg;                                        
          mgPRA mappableGrp = new mgPRA(monto1, monto2, praSugg);
          String ontologyPair = AlgoConstants.settingsInfo.getName
                  (Constants.ONTOLOGY_1).concat(AlgoConstants.SEPERATOR).
                  concat(AlgoConstants.settingsInfo.
                  getName(Constants.ONTOLOGY_2));
          ArrayList<String> insertStatements = new ArrayList<String>();      
          Connection sqlConn = makeConnection();
          
          MappableConceptPairsDB mappableGrpDB = new MappableConceptPairsDB
                  (sqlConn);
          String tableName = mappableGrpDB.getTableName();          
          mappableGrpDB.clearTable(ontologyPair);          
          Vector<Pair> mgResult = mappableGrp.getResults();          
          int numOfMappableSuggestions = mgResult.size();
          
          for (Pair p : mgResult) {
              
              String c1 = (((MClass) p.getObject1()).OntClass().
                      getLocalName()).toString();            
              String c2 = (((MClass) p.getObject2()).OntClass().
                      getLocalName()).toString();
            
              String suggestion = c1.concat(AlgoConstants.SEPERATOR).concat(c2);            
              insertStatements.add("INSERT INTO " + tableName + " VALUES('"
                      + ontologyPair +"', '" + suggestion + "')");
            
              if (insertStatements.size() > 100000) {
                  
                  mappableGrpDB.multipleInsert(insertStatements);                
                  insertStatements.clear();
              }
          }
          
          if (insertStatements.size() > 0) {
              mappableGrpDB.multipleInsert(insertStatements);
          }
          
          out.println("No. of mappable concept pairs are "
                  + numOfMappableSuggestions);
        
          ResourceManager.close(sqlConn);
        %>
                        
        <%!
        
        public Connection makeConnection() {
            Connection conn = null;
            try {
                conn = ResourceManager.getConnection();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return conn;
        }
        %> 
    </body>
</html>
