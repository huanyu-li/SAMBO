/*
 * OWLFileFilter.java
 *
 */

package se.liu.ida.sambo.util;

/**
 *
 * @author hetan
 */
public class OWLFileFilter implements java.io.FileFilter{
    
  
    public boolean accept(java.io.File file) {        
        
        return !file.isDirectory() && !file.isHidden() 
               && file.getName().toLowerCase().endsWith(".owl");
    }
    
    
}