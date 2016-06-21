/*
 * XlsFileHandler.java
 *
 * Created on den 15 maj 2006, 17:12
 */

package se.liu.ida.sambo.util;


import java.io.*;
import jxl.*;
import jxl.write.*;


import java.sql.*;

/**XLF file handler
 *
 * @author He Tan
 */
public class XlsFileHandler {

    
    /*Open a new excel file
     *
     *@param filename the file name
     */
    public static WritableWorkbook createXls(String filename) {
        try {
            return Workbook.createWorkbook(new File(filename));
        } catch (Exception e){
            e.printStackTrace();
        } 
        
        return null;
    }
    
   
    /*Open the excel file
     *
     *@param filename the file name
     */
    public static Workbook openXls(String filename) {
        try {
            return Workbook.getWorkbook(new File(filename));
        } catch (Exception e){
            e.printStackTrace();
        } 
        
        return null;
    }
    
    
    public static WritableSheet addSheet(WritableWorkbook book, String sheetname, int index){
        
        try {
            return book.createSheet(sheetname, index);
        } catch (Exception e){
            e.printStackTrace();
        }
        
        return null;
    }
    
    
    
    public static void addlabel(WritableSheet sheet, String label, int i, int j) {
        try {
            sheet.addCell(new Label(i, j, label));
        } catch (JXLException e){
            System.err.print(e.toString());
        } 
    }
    
    public static void addnumber(WritableSheet sheet, double number, int i, int j) {
        try {
            sheet.addCell(new jxl.write.Number(i, j, number));
        } catch (JXLException e) {
            System.err.print(e.toString());
        }
    }    
    
    
    
    /**Close the excel file
     */
    public static void closeXls(WritableWorkbook book) {
        try {
            book.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
    
    /**Close the excel file
     */
    public static void writeXls(WritableWorkbook book) {
        try {
            book.write();
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        
        String filename = "C:/Documents and Settings/hetan/My Documents/projects/EC2GO/ec2go.xls";
        
        Workbook book = XlsFileHandler.openXls(filename);        
        
        //String sheetname = "ec";
        String sheetname = "go";
        
        Sheet sheet = book.getSheet(sheetname);
        
        for(int i = sheet.getRows()-1; i > 0; i--)
            System.out.println(sheet.getCell(0, i).getContents());
    
    }
    
}
