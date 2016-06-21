/*
 * demo.java
 */

package se.liu.ida.sambo.text;

/** Test
 *
 * @author  He Tan
 * @version
 */

import java.net.URL;
import java.util.*;
import java.io.*;

import com.objectspace.jgl.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import se.liu.ida.sambo.Merger.*;
import se.liu.ida.sambo.MModel.*;

import se.liu.ida.sambo.text.query.*;

import se.liu.ida.sambo.util.XlsFileHandler;
import se.liu.ida.sambo.util.FilenameListFilter;

public class ir {
    
    //PC
    //  public static String basedir = "Z:/sambo/learning/data/";
    public static String basedir = "Z:/sambo/data/EC2GO/data/";
    
    //UNIX
    //public static String basedir = "home/hetan/sambo/learning/data/";
    
    static int LIMIT_100 = 100;
    static PubMedQuerier querier;
    static PubMedFetcher fetcher;
    
    public static void main(String[] args) throws Exception{
//throws ParserConfigurationException{
        
        File[] dirs = (new File(basedir + File.separator + "ec13/ec")).listFiles();
        for(int i =0; i<dirs.length; i++){
            
            if(dirs[i].isDirectory() && !dirs[i].isHidden())
                System.out.println("EC." + dirs[i].getName() + ": " + dirs[i].listFiles().length);
            
        }
        
        //  querier = new PubMedQuerier();
        //  fetcher = new PubMedFetcher();
        
        //  String filename = "Z:/sambo/data/EC2GO/ec2go.xls";
        
        //!!!query field is EC/RN number
        //String sheetname = "ec";
        //!!!query field is Title/Abstract
        //  String sheetname = "go";
        
        //  jxl.Sheet sheet = XlsFileHandler.openXls(filename).getSheet(sheetname);
        
        // for(int i = sheet.getRows()-1; i > 0 ; i--)
        //     System.out.println(clearStr(sheet.getCell(0, i).getContents()).trim());
        //EC:1.1.2  --> 1.1.2
        // retrieve(sheet.getCell(0, i).getContents().substring(3), basedir + File.separator + sheetname);
        //  retrieve(clearStr(sheet.getCell(0, i).getContents()).trim(), basedir + File.separator + sheetname);
        
      /*  String[] dir = (new File(basedir + File.separator + sheetname)).list();
       
        for(int i = dir.length -1; i >=0 ; i--)
            System.out.println(dir[i]);*/
        
        
 /*       String filename = "Z:/sambo/data/EC2GO/ec2go.xls";
        
        //!!!query field is EC/RN number
       // String sheetname = "ec13";
        //!!!query field is Title/Abstract
        String sheetname = "ec113";
        
        ArrayList list = new ArrayList();
        
        jxl.Sheet sheet = XlsFileHandler.openXls(filename).getSheet(sheetname);
        
        for(int i = sheet.getRows()-1; i > 0 ; i--)
            //EC:1.1.2  --> 1.1.2
            list.add(sheet.getCell(0, i).getContents().substring(3));
        //list.add(clearStr(sheet.getCell(0, i).getContents()).trim());
        
        create("Z:/sambo/data/EC2GO/data/ecgo/100/ec", "Z:/sambo/data/EC2GO/data/ec113/ec", list);
        */
    }
    
    
    private static String clearStr(String input){
        
        char[] chars = input.toLowerCase().toCharArray();
        
        int l = chars.length;
        for (int i = 0; i < l; i++) {
            if(!Character.isLetterOrDigit(chars[i]))
                chars[i] = ' ';
        }
        
        return new String(chars);
    }
    
    private static void query4OWL() throws ParserConfigurationException, IOException{
        
        //int[] data = {20, 40, 60, 80};
        int[] data = {100};
        
        String[] onto = {"nose", "ear", "eye"};
        // String[] onto = {"behavior", "defense"};
        
        querier = new PubMedQuerier();
        fetcher = new PubMedFetcher();
        
        for (int i = 0; i < onto.length; i++){
            
            String name = onto[i];
            System.out.println(name);
            
            // GO vs SO, or MA vs MeSH
            String onto1 = "file:///" + basedir + name + "/" + name + "_MA.owl";
            String onto2 = "file:///" + basedir + name + "/" + name + "_MeSH.owl";
            
            //String onto1 = "file:///" + basedir + name + "/" + name + "_GO.owl";
            //String onto2 = "file:///" + basedir + name + "/" + name + "_SO.owl";
            
            
            MergeManager manager = new MergeManager();
            
            System.out.println("\nloading ontologies for " + name + "...");
            manager.loadOntologies(onto1, onto2);
            
            for (int j = 0; j < data.length; j++){
                
                
                int num = data[j];
                
                //create(basedir + name, num);
                
                // GO vs SO, or MA vs MeSH
                String prefix1 = basedir + name + "/" + num + "/MA";
                String prefix2 = basedir + name + "/" + num + "/MeSH";
                
                //String prefix1 = basedir + name + "/" + num + "/GO";
                //String prefix2 = basedir + name + "/" + num + "/SO";
                
                
                System.out.println("\nExtracting the documents from PubMed...");
                
                //  FileWriter out = openOut(basedir + name + "/" + num + "/time.txt");
                //  long time = System.currentTimeMillis();
                
                //  writeOut(out, new java.util.Date(time).toString());
                
                run(manager, Constants.ONTOLOGY_1, prefix1);
                run(manager, Constants.ONTOLOGY_2, prefix2);
                
                //  time = System.currentTimeMillis() - time;
                //  writeOut(out, "\n Execution time (milisec): "  + time);
                
                //  closeOut(out);*/
                
                System.out.println("finish");
            }
        }
    }
    
    
//perform data retrieval for each term in ontologies
    private static void run(MergeManager manager, int ontonum, String prefix)
    throws IOException{
        
        for(Enumeration e = manager.getOntology(ontonum).getClasses().elements(); e.hasMoreElements();){
            
            MClass mc = (MClass) e.nextElement();
            //   retrieve(mc.getPrettyName(), prefix);
            //retrieve the data related to synonyms
            for(Enumeration ex = mc.getSynonyms().elements(); ex.hasMoreElements();)
                retrieve((String) ex.nextElement(), prefix);
            
        }
        
    }
    
    
//create the set of data whose size is specified (20, 40, 60, 80, 100)
    private static void create(String basedir, int num) throws IOException{
        
        String src = basedir + "/100";
        String des = basedir + "/" + num;
        
        (new File(des)).mkdir();
        
        //get the directory GO vs SO, or MA vs MeSH
        File[] two = (new File(src)).listFiles();
        for(int j = two.length-1; j >= 0; j--){
            
            if(two[j].isDirectory() && !two[j].isHidden()){
                
                String onto = two[j].getName();
                
                (new File(des + "/" + onto)).mkdir();
                FileWriter out = openOut(des + "/num-" + onto + ".txt");
                
                //get the categories
                File[] cates = two[j].listFiles();
                for(int i = cates.length-1; i >=0 ; i--){
                    
                    if(cates[i].isDirectory() && !cates[i].isHidden()){
                        
                        String cate = cates[i].getName();
                        File catdir = new File(des + "/" + onto + "/" + cate);
                        catdir.mkdir();
                        
                        //get the data
                        File[] data = cates[i].listFiles();
                        for(int m = data.length-1; m >=0; m--){
                            
                            String dat = data[m].getName();
                            if((new Integer(dat)).intValue() <= num)
                                copyFile(src + "/" + onto + "/" + cate + "/" + dat,
                                        des + "/" + onto + "/" + cate + "/" + dat);
                        }
                        
                        writeOut(out, "\n" + cate + ";  "  + catdir.listFiles().length);
                    }
                }
                
                closeOut(out);
            }
        }
    }
    
    
    //create the set of data whose size is specified (20, 40, 60, 80, 100)
    private static void create(String srcdir, String desdir, ArrayList list) throws IOException{
        
        //get the src dir
        File[] src = (new File(srcdir)).listFiles(new FilenameListFilter(list));
        for(int j = src.length-1; j >= 0; j--){
            
            (new File(desdir + File.separator + src[j].getName())).mkdir();
            
            //get the categories
            File[] data = src[j].listFiles();
            for(int i = data.length-1; i >=0; i--)
                copyFile(srcdir + File.separator + src[j].getName() + File.separator + data[i].getName(),
                        desdir + File.separator + src[j].getName() + File.separator + data[i].getName());
            
        }
    }
    
//perform data retrieval
    private static void retrieve(String term, String prefix) throws IOException{
        
        FileWriter out;
        System.out.println("--------retrieve: " + term);
        (new File(prefix + File.separator + term)).mkdir();
        
        int start = 0, limit = LIMIT_100;
        Vector id = null, doc = new Vector();
        int query, fetch;
        
        do{
            System.out.println("Start: " + start + " Limit: " + limit);
            do{
                query = querier.sendQuery(term, start, limit);
                if(query == PubMedConstants.QUERY_SUCCESS){
                    id = querier.getPMIDList();
                    System.out.println("query success : " + id.size());
                }
            }while(query == PubMedConstants.CONNECT_FAIL);
            
            //no more result from PubMed
            if(query == PubMedConstants.QUERY_FAIL || query == PubMedConstants.NOT_FOUND) {
                System.out.println("NO MORE RESULT");
                break;
            }
            
            do{
                fetch = fetcher.sendQuery(id);
                if(query == PubMedConstants.QUERY_SUCCESS){
                    doc.addAll(fetcher.getAbstractText());
                    System.out.println("fetch success : " + doc.size());
                }
            }while(fetch == PubMedConstants.CONNECT_FAIL);
            
            start += id.size();
            limit = LIMIT_100 - doc.size();
            
        }while(doc.size() < LIMIT_100 && id.size() >= 100);
        
        int len = doc.size();
        for(int index = 1; index <= len; index++ ){
            out = openOut(prefix + File.separator + term + File.separator + index);
            writeOut(out, (String)doc.get(index-1));
            closeOut(out);
        }
    }
    
    
    public static void writeOut(FileWriter out, String s) {
        try {
            out.write(s);
        } catch (Exception e) {
            System.err.println("java.io.IOException" + e.getMessage());
        }
    }
    
    
    public static void copyFile(String src, String des) throws IOException{
        
        FileReader in = new FileReader(new File(src));
        FileWriter out = new FileWriter(new File(des));
        int c;
        
        while ((c = in.read()) != -1)
            out.write(c);
        
        in.close();
        out.close();
    }
    
// open a new file to output intermediate results
    public static FileWriter openOut(String fileName) {
        try {
            return new FileWriter(fileName);
        } catch (Exception e) {
            System.err.println("java.io.IOException" + e.getMessage());
        }
        return null;
    }
    
    public static void closeOut(FileWriter out){
        try {
            out.close();
        } catch (Exception e) {
            System.err.println("java.io.IOException" + e.getMessage());
        }
    }
    
    
}
