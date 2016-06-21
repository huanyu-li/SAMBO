/*
 * FileInListFilter.java
 *
 */

package se.liu.ida.sambo.util;


import java.util.ArrayList;

/**Filter the list of files from the filename list
 *
 * @author hetan
 */
public class FilenameListFilter implements java.io.FileFilter{
    
    ArrayList list;
    
    /** Creates a new instance of MyFileFilter
     *
     *@param list the list of filename
     */
    public FilenameListFilter(ArrayList list) {
        
        this.list = list;
    }
    
    
    public boolean accept(java.io.File file){
        
        return list.contains(file.getName());
    }
}
