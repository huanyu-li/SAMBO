package se.liu.ida.sambo.ui;

import se.liu.ida.sambo.ui.web.Constants;
import java.net.URL;

/**
 * A specialized class used for holding session information
 *
 * @author Anna Edberg, modified by He Tan
 * @version 
 */
public class SettingsInfo implements java.io.Serializable {

    private String name1;
    private String name2;
    private String name3;
    
    private String color1;
    private String color2;
    
    private URL url1;
    private URL url2;
    
    private int step=-1;
    
     /**
     * Constructor
     */
    public SettingsInfo() {
    }    
           
    /**
     * Return the name of the ontologies
     *
     * @param param a constant representing the ontology
     * @return the name
     */ 
    public String getName(int param){
         switch(param){
            case Constants.ONTOLOGY_1: 
                return name1;
            case Constants.ONTOLOGY_2:  
                return name2;
            case Constants.ONTOLOGY_NEW:
                return name3;
            default:
                throw new NullPointerException();
        }
    }
    
    
    /**
     * Return the color of the ontologies
     *
     * @param param a constant representing the ontology
     * @return the color
     */ 
     public String getColor(int param){
        switch(param){
            case Constants.ONTOLOGY_1: 
                return color1;
            case Constants.ONTOLOGY_2:  
                return color2;
            default:
                throw new NullPointerException();
        }
    }
    
    /**
     * Return the URL of the ontologies
     *
     * @param param a constant representing the ontology
     * @return the url
     */ 
     public URL getURL(int param){
        switch(param){
            case Constants.ONTOLOGY_1: 
                return url1;
            case Constants.ONTOLOGY_2:  
                return url2;
            default:
                throw new NullPointerException();
        }
    }
    
    /**
     * Return the step terminated of the merge
     *
     * @return the step
     */ 
     public int getStep(){
         if(step==-1)
             throw new NullPointerException();
         return step;
     }
     

    
    /**
     * Set the step terminated of the merge
     */ 
     public void setStep(int s){
         step = s;
     }
     
     
     /**
     * Set the url of the ontologies
     * 
     *@param URL url1 the url of the ontology-1
     *@param ULR url2 the url of the ontology-2
     *
     */ 
     public void setURLs(URL url1, URL url2){
         this.url1 = url1;
         this.url2 = url2;
     }
     
    /**
     * Set the name of the ontologies
     * 
     *@param String name1 the name of the ontology-1
     *@param String name2 the name of the ontology-2
     * @param name3 the name of the merged ontology
     */ 
     public void setNames(String name1, String name2, String name3){
         this.name1 = name1;
         this.name2 = name2;
         this.name3 = name3;
     }
     
     
    /**
     * Set the color of the ontologies
     *
     * @param color1 the hexidecimal representation of the color for the first ontology
     * @param color2 the hexidecimal representation of the color for the second ontology
     */
     public void setColors(String color1, String color2){
         this.color1 = color1;
         this.color2 = color2;
     }
     
}
