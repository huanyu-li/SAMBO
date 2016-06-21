/*
 * ConfigListener.java
 *
 */

package se.liu.ida.sambo.ui.web.listener;

/**
 *
 * @author He Tan
 */


import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;


import java.io.File;

import se.liu.ida.sambo.ui.web.Constants;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;
import se.liu.ida.sambo.session.Commons;
//import se.liu.ida.sambo.ui.web.*;

public class ConfigListener implements ServletContextListener	{
    
   
  /*This method is invoked when the Web Application has been removed
  and is no longer able to accept requests
   */
    
    public void contextDestroyed(ServletContextEvent event) { }
    
    
    //This method is invoked when the Web Application
    //is ready to service requests
    public void contextInitialized(ServletContextEvent event) {                           
        
        ServletContext context = event.getServletContext();
        
         //Log4j configuration  
    /*    System.setProperty( "log.home", context.getRealPath(context.getInitParameter("LogFileLocation")) );                
        org.apache.log4j.xml.DOMConfigurator.configure(context.getRealPath(context.getInitParameter("Log4jConfig")));
        
        Logger logger = Logger.getRootLogger();
        
        logger.info(" ******** SAMBO Server Start ***************");        
        logger.info("configure Log4j");
    */
        System.out.println("CD=" + System.getProperty("user.dir"));
       //System.setProperty("user.dir", "D:/JavaDevelopment/Testing/xxx");  // change CD
       // System.out.println("CD=" + System.getProperty("user.dir"));

        Constants.SESSIONS = context.getRealPath(context.getInitParameter("Sessions"))+ File.separator;
        Constants.SEGMENT = context.getRealPath(context.getInitParameter("Segment"))+ File.separator;
        Constants.FILEHOME = context.getRealPath(context.getInitParameter("FileOnServer")) + File.separator;
        Commons.FILEHOME = context.getRealPath(context.getInitParameter("FileOnServer")) + File.separator;
        Commons.CONTEXT_PATH=context.getContextPath();
   //     logger.info("configure file location on server");
    
     //   try{           
          //  java.net.InetAddress addr = java.net.InetAddress.getLocalHost();
          //  Constants.defaultReasoner = "http://" + addr.getHostAddress() + ":8080/";
            
            //pellet 1.3 on UNIX
           // Constants.defaultReasoner = "http://130.236.176.135:8081/";
           Constants.defaultReasoner = context.getInitParameter("Reasoner");
            
     //   }catch(java.net.UnknownHostException e){
     //       e.printStackTrace();
     //   }
        
    //    logger.info("configure Racer on server");
        
        AlgoConstants.WORDNET_DIC = context.getRealPath(context.getInitParameter("WordnetXML"));
    //    logger.info("configure wordnet property xml");
        
        se.liu.ida.sambo.MModel.util.OntConstants.STOPWORD_FILE = context.getRealPath(context.getInitParameter("StopwordFile"));
        se.liu.ida.sambo.algos.matching.algos.AlgoConstants.STOPWORD_FILE = context.getRealPath(context.getInitParameter("StopwordFile"));

        
    //    logger.info("configure stopword file")        ;
        
    }
}

