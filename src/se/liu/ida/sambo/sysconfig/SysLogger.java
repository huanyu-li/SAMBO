/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.sysconfig;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Qiang Liu
 */
public class SysLogger {

    public static void openConsoleLogger(Level lev) {

        // only start logging under namespace "se"
        Logger logger = Logger.getLogger("se");
        SysLogger.commonSpecify(lev);
        SysLogger.userSpecify();
        // Open the console handler
        Handler handler = new ConsoleHandler();
        handler.setLevel(lev);
        logger.addHandler(handler);
        logger.log(lev, "Console logging has been ready.");
    }

    private static void commonSpecify(Level lev) {
        // only start logging under namespace "se"
        Logger logger = Logger.getLogger("se");
        // set not to send msg to parent handler
        logger.setUseParentHandlers(false);
        logger.setLevel(lev);
    }

    private static void userSpecify(){
        Logger.getLogger("prefuse").setLevel(Level.OFF);
        Logger.getLogger("se.liu.ida.sambo.component.matcher.domain.RelationFinder.CombRelationFinder").setLevel(Level.OFF);
    }


}
