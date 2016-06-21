/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 *
 * @author rajka62
 */
public class testingSmallCodes {
    
    
    public static void main(String args[]) {
        
        double median = 0.55;
        
        DecimalFormat df = new DecimalFormat("##.#");
        df.setRoundingMode(RoundingMode.DOWN);
        String medianStr = df.format(median);
        /**
         * In Swedish char "." will be written as "," so we need to replace 
         * this.
         */        
        medianStr = medianStr.replace(',', '.');
        median = Double.parseDouble(medianStr);
        
        System.out.println(median);        
    }
    
}
