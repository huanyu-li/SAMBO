
package se.liu.ida.sambo.Merger;


import java.io.*;

import se.liu.ida.sambo.ui.SettingsInfo;

/** Project Manager
 *
 * @author He Tan
 */
public class ProjectManager implements Serializable {
    
    private SettingsInfo settings;
    private MergeManager merge;
    
    /**
     * Constructor
     *
     *@param settgins information of setting
     *@param merge manager
     */
    public ProjectManager(SettingsInfo settings, MergeManager merge ){
        
        this.settings = settings;
       // this.merge = merge;
    }
    
    /**Write this project manager object
     *
     *@param file the object file
     */
    public void write(String file){
        
        try{
            FileOutputStream out = new FileOutputStream(file);
            ObjectOutputStream stream = new ObjectOutputStream(out);
            stream.writeObject(this);
            stream.flush();
        }catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }
    
    /**Get Setting information
     *
     *@return settings
     */     
    public SettingsInfo getSettings(){
        return settings;
    }
    
    /**Get merge manager
     *
     *@return merge
     */
    public MergeManager getMergeManager(){
        return merge;
    }
    
    /*Read a project manager object 
     *
     *@param file the object file
     */
    public static ProjectManager read(String file){
        
        try{
            FileInputStream in = new FileInputStream(file);
            ObjectInputStream stream = new ObjectInputStream(in);
            return (ProjectManager)stream.readObject();
            
        }catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }catch(IOException ex){
            System.out.println(ex.getMessage());
        }catch(ClassNotFoundException ec){
            ec.printStackTrace();
        }
        
        return null;
    }     
    
    
     public static void main(String args[]) throws Exception
    {
         
     }
}


