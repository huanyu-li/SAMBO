/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import se.liu.ida.sambo.MModel.MProperty;
import se.liu.ida.sambo.Merger.testMergerManager;
import se.liu.ida.sambo.dao.UserSessionsDao;
import se.liu.ida.sambo.dto.UserSessions;
import se.liu.ida.sambo.dto.UserSessionsPk;
import se.liu.ida.sambo.factory.UserSessionsDaoFactory;
import se.liu.ida.sambo.session.Commons;
import se.liu.ida.sambo.ui.SettingsInfo;
import se.liu.ida.sambo.ui.web.Constants;
import se.liu.ida.sambo.ui.web.testFormHandler;
import se.liu.ida.sambo.ui.web.testPageHandler;
import se.liu.ida.sambo.util.History;
import se.liu.ida.sambo.util.Pair;
import se.liu.ida.sambo.util.QueryStringHandler;
import se.liu.ida.sambo.util.Suggestion;

/**
 *
 * @author huali50
 */
public class testSlotServlet extends HttpServlet {
 
}
