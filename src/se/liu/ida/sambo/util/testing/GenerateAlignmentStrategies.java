/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.util.testing;

import java.util.ArrayList;
import java.util.Random;

/**
 * <p>
 * Generate alignment strategies, the matcher list should not be more than five.
 * </p>
 * 
 * @author Rajaram
 * @version 1.0
 */
public class GenerateAlignmentStrategies {
    
    /**
     * List of generated strategies.
     */
    private ArrayList<String> strategies = new ArrayList<String> ();
    /**
     * List of matchers.
     */
    private String[] matchers = {"NGram","TermBasic"};//,"TermWN","UMLSKSearch","BayesLearning"};
    /**
     * List of thresholds.
     */
    private String[] thersholds = {"0.3","0.4","0.5"};//,"0.6","0.7","0.8"};    
    /**
     * Default constructor.
     */
    public GenerateAlignmentStrategies() {
    }
    /**
     * Constructor with the user matchers and thresholds set.
     * 
     * @param uMatchers      List of matchers.
     * @param uThersholds    List of thresholds.
     */
    public GenerateAlignmentStrategies(String[] uMatchers,
            String[] uThersholds) {
        matchers = uMatchers;        
        thersholds = uThersholds;        
    }
    
    /**
     * This method returns list of generated alignment strategies.
     * 
     * @return Alignment strategies.
     */
    public ArrayList<String> getStrategies() {
        
        return loadFromDB();
        
//        int totalStrategies = 0;
//        
//        //  single matcher in a strategies.
//        if (matchers.length > 0) {
//            generate1MatcherSet();
//        }    
//        //Generating two matchers in a strategies.    
//        if (matchers.length > 1) {
//            totalStrategies = possibleStrategies(2); 
//            generate2MatchersSet(totalStrategies);
//        }    
//        // Generating three matchers in a strategies.    
//        if (matchers.length > 2) {
//            totalStrategies = possibleStrategies(3);
//            generateMatcherSet3(totalStrategies);
//        }     
//        //Generating four matchers in a strategies.
//        if (matchers.length > 3) {
//            generate4MatchersSet();
//        }
//        //Generating five matchers in a strategies.     
//        if (matchers.length > 4) {
//            generate5MatchersSet();
//        }    
//     
//    return strategies;    
    }
    
    /**
     * To load alignment strategies from the database.
     * 
     * @return List of alignment strategies. 
     */
    public ArrayList<String> loadFromDB() {
        StrategiesDB db = new StrategiesDB();        
        ArrayList<String> result = db.getStrategies();        
        db.closeConnection();        
        return result;        
    }    
    
    /**
     * Print generated strategies.
     */
    private void printStrategies() {
        for(String s:strategies) {
            System.out.println(s);
        }
    }
    
    /**
     * Generates single matcher strategies.
     */
    private void generate1MatcherSet() {
        
        for (String matcher:matchers) {
            
            for (String threshold:thersholds) {
                strategies.add(matcher+",1.0,-NA-," + threshold);
            }
            // Adding double threshold to this strategies.
            addDoubleThreshold(matcher + ",1.0,-NA-,");            
        }
    }
    
    /**
     * Generates two matchers strategies.
     */
    private void generate2MatchersSet(int totalSize) {
                
        ArrayList<String> twoMatchers = new ArrayList();        
        Random randomNum = new Random();          
        
        while (twoMatchers.size() < totalSize) {
            
            // To pick matchers randomly from the list.
            int ranNum1 = randomNum.nextInt((matchers.length));        
            int ranNum2 = randomNum.nextInt((matchers.length));        
            
            while (ranNum2 == ranNum1) {
                ranNum2= randomNum.nextInt((matchers.length));
            }
            // Forming matchers combination.
            String matcherSet = matchers[ranNum1] + ";" + matchers[ranNum2] 
                    + ",";
            // Reverse of the above combination.
            String matcherSetReverse = matchers[ranNum2] + ";" + 
                    matchers[ranNum1] + ",";
            
            if (twoMatchers.isEmpty()) {
                System.out.println(matcherSet);
                twoMatchers.add(matcherSet);
            } 
            else if (!twoMatchers.contains(matcherSet) && 
                    !twoMatchers.contains(matcherSetReverse)) {
                
                System.out.println(matcherSet);    
                twoMatchers.add(matcherSet);
            }        
        }        
         
        ArrayList<String> twoMatchersWegSet1 = new ArrayList<String>();
        ArrayList<String> twoMatchersWegSet2 = new ArrayList<String>();
        ArrayList<String> twoMatchersWegSet3 = new ArrayList<String>();
        
        /**
         * Adding weights to the strategies, in the current implementation 
         * this process is done manually.
         */
        for (String s:twoMatchers) {
            twoMatchersWegSet1.add(s + "1.0;1.0,");
            twoMatchersWegSet2.add(s + "1.0;2.0,");
            twoMatchersWegSet3.add(s + "2.0;1.0,");
        }
        
        
        for (int i = 0; i < twoMatchers.size(); i++) {
            
            int first = 0;            
            for (String s: thersholds) {
                
                /**
                 * In the current implementation the strategies that has 
                 * even weights for the matcher will gets maximum combination.
                 */ 
                strategies.add(twoMatchersWegSet1.get(i) + "maximum," + s);               
                strategies.add(twoMatchersWegSet1.get(i) + "weighted," + s);
                
                strategies.add(twoMatchersWegSet2.get(i) + "weighted," + s);
                
                strategies.add(twoMatchersWegSet3.get(i) + "weighted," + s);
                
                /**
                 * The method 'addDoubleThreshold' adds both upper and lower
                 * thresholds, so to this method give a strategies without any
                 * threshold.
                 */ 
                if(first == 0) {
                addDoubleThreshold(twoMatchersWegSet1.get(i) + "maximum,");
                addDoubleThreshold(twoMatchersWegSet1.get(i) + "weighted,");
                addDoubleThreshold(twoMatchersWegSet2.get(i) + "weighted,");
                addDoubleThreshold(twoMatchersWegSet3.get(i) + "weighted,");
                }                
                first++;                
            }
        }        
    }
    
    /**
     * Generates three matchers strategies.
     */
    private void generateMatcherSet3(int totalsize) {
        
        ArrayList<String> threeMatchers = new ArrayList<String> ();        
        Random randomNum = new Random();
             
        while (threeMatchers.size() < totalsize) {
             // To pick matchers randomly from the list.
            int ranNum1 = randomNum.nextInt((matchers.length));        
            int ranNum2 = randomNum.nextInt((matchers.length));        
            int ranNum3 = randomNum.nextInt((matchers.length));
            
            while (ranNum2 == ranNum1 || ranNum2 == ranNum3) {
                ranNum2 = randomNum.nextInt((matchers.length));
            }        
            while (ranNum3 == ranNum1 || ranNum3 == ranNum2 ) {
                ranNum3 = randomNum.nextInt((matchers.length));
            }        
            // Forming matchers combination.
            String matcherSet = matchers[ranNum1] + ";" + matchers[ranNum2]
                    + ";" + matchers[ranNum3] + ",";
            // Reverse of the above combination.
            String matcherSetRev1 = matchers[ranNum1] + ";" + matchers[ranNum3]
                    + ";" + matchers[ranNum2] + ",";        
            String matcherSetRev2 = matchers[ranNum2] + ";"+matchers[ranNum1] 
                    + ";" + matchers[ranNum3] + ",";        
            String matcherSetRev3 = matchers[ranNum2] + ";" + matchers[ranNum3]
                    + ";" + matchers[ranNum1] + ",";        
            String matcherSetRev4 = matchers[ranNum3] + ";" + matchers[ranNum1]
                    + ";" + matchers[ranNum2] + ",";        
            String matcherSetRev5 = matchers[ranNum3] + ";" + matchers[ranNum2]
                    + ";" + matchers[ranNum1] + ",";
            
            if (threeMatchers.isEmpty()) {
                System.out.println(matcherSet);
                threeMatchers.add(matcherSet);
            }
            else if (!threeMatchers.contains(matcherSet)) {
                
                if (!threeMatchers.contains(matcherSetRev1) && 
                        !threeMatchers.contains(matcherSetRev2) && 
                        !threeMatchers.contains(matcherSetRev3) && 
                        !threeMatchers.contains(matcherSetRev4) && 
                        !threeMatchers.contains(matcherSetRev5)) {
                    System.out.println(matcherSet);
                    threeMatchers.add(matcherSet);
                }
            }
        }       
        
        ArrayList<String> threeMatchersWegSet1= new ArrayList<String>();
        ArrayList<String> threeMatchersWegSet2= new ArrayList<String>();
        ArrayList<String> threeMatchersWegSet3= new ArrayList<String>();
        ArrayList<String> threeMatchersWegSet4= new ArrayList<String>();
        ArrayList<String> threeMatchersWegSet5= new ArrayList<String>();
        ArrayList<String> threeMatchersWegSet6= new ArrayList<String>();
        ArrayList<String> threeMatchersWegSet7= new ArrayList<String>();
        
        /**
         * Adding weights to the strategies, in the current implementation this
         * process is done manually.
         */
        for(String s:threeMatchers) {
            threeMatchersWegSet1.add(s + "1.0;1.0;1.0,");
            threeMatchersWegSet2.add(s + "2.0;1.0;1.0,");
            threeMatchersWegSet3.add(s + "1.0;2.0;1.0,");            
            threeMatchersWegSet4.add(s + "1.0;1.0;2.0,");            
            threeMatchersWegSet5.add(s + "2.0;2.0;1.0,");            
            threeMatchersWegSet6.add(s + "1.0;2.0;2.0,");            
            threeMatchersWegSet7.add(s + "2.0;1.0;2.0,");
        }
        
        for (int i = 0; i < threeMatchers.size(); i++) {
            
            int first = 0;
            for (String s: thersholds) {
                /**
                 * In the current implementation the strategies that has even
                 * weights for the matcher gets maximum combination.
                 */
                strategies.add(threeMatchersWegSet1.get(i) + "maximum," + s);                
                strategies.add(threeMatchersWegSet1.get(i) + "weighted," + s);
                
                strategies.add(threeMatchersWegSet2.get(i) + "weighted," + s);
                
                strategies.add(threeMatchersWegSet3.get(i) + "weighted," + s);
                
                strategies.add(threeMatchersWegSet4.get(i) + "weighted," + s);
                
                strategies.add(threeMatchersWegSet5.get(i) + "weighted," + s);
                
                strategies.add(threeMatchersWegSet6.get(i) + "weighted," + s);
                
                strategies.add(threeMatchersWegSet7.get(i) + "weighted," + s);                
                /**
                 * The method 'addDoubleThreshold' adds both upper and lower
                 * thresholds, so to this method give a strategies without any
                 * threshold.
                 */ 
                if (first == 0) {
                    addDoubleThreshold(threeMatchersWegSet1.get(i) 
                            + "maximum,");
                    addDoubleThreshold(threeMatchersWegSet1.get(i) 
                            + "weighted,");
                    addDoubleThreshold(threeMatchersWegSet2.get(i) 
                            + "weighted,");
                    addDoubleThreshold(threeMatchersWegSet3.get(i) 
                            + "weighted,");
                    addDoubleThreshold(threeMatchersWegSet4.get(i) 
                            + "weighted,");
                    addDoubleThreshold(threeMatchersWegSet5.get(i) 
                            + "weighted,");
                    addDoubleThreshold(threeMatchersWegSet6.get(i) 
                            + "weighted,");
                    addDoubleThreshold(threeMatchersWegSet7.get(i) 
                            + "weighted,");
                }
                first++;
            }
        }
    }
    
    /**
     * Generates four matchers strategies.
     */
    private void generate4MatchersSet() {
         
         ArrayList<String> fourMatchers = new ArrayList(); 
         
         for (int i = 0; i < matchers.length; i++) {
             
             String matcherSet = "";
             
             if (( i + 3) < matchers.length) {
                 matcherSet = matchers[i] + ";"+ matchers[i + 1] + 
                         ";" + matchers[i + 2] + ";"+matchers[i + 3] + ",";
                 System.out.println(matcherSet);
                 fourMatchers.add(matcherSet);                  
             } else {
                 int j = i;
                 int count = 0;
                 matcherSet = "";
                 
                 while (count < 2) {
                     
                     int n = j + 1;
                     
                     if (count > 0) {
                         matcherSet = matcherSet+";";
                     }
                     if (((matchers.length)-n)>0) {
                         matcherSet = matcherSet + matchers[j] + ";"
                                 + matchers[j + 1];  
                     }  
                     else if (((matchers.length) - j) > 0) {
                         matcherSet = matcherSet + matchers[j] + ";"
                                 + matchers[(j + 1) - matchers.length];
                     }
                     else {
                         matcherSet = matcherSet + matchers[j-matchers.length]
                                 + ";" + matchers[(j + 1)-matchers.length];
                     }
                     
                     count++;
                     j = j + 2; 
                 }
                 
                 matcherSet = matcherSet + ",";
                 System.out.println(matcherSet);
                 fourMatchers.add(matcherSet);
             }
         }                  
                  
        ArrayList<String> fourMatchersWegSet1 = new ArrayList<String>();
        ArrayList<String> fourMatchersWegSet2 = new ArrayList<String>();
        ArrayList<String> fourMatchersWegSet3 = new ArrayList<String>();
        ArrayList<String> fourMatchersWegSet4 = new ArrayList<String>();
        ArrayList<String> fourMatchersWegSet5 = new ArrayList<String>();
        ArrayList<String> fourMatchersWegSet6 = new ArrayList<String>();
        ArrayList<String> fourMatchersWegSet7 = new ArrayList<String>();        
        ArrayList<String> fourMatchersWegSet8 = new ArrayList<String>();
        ArrayList<String> fourMatchersWegSet9 = new ArrayList<String>();
        ArrayList<String> fourMatchersWegSet10 = new ArrayList<String>();
        ArrayList<String> fourMatchersWegSet11 = new ArrayList<String>();
        ArrayList<String> fourMatchersWegSet12 = new ArrayList<String>();
        ArrayList<String> fourMatchersWegSet13 = new ArrayList<String>();
        ArrayList<String> fourMatchersWegSet14 = new ArrayList<String>();        
        ArrayList<String> fourMatchersWegSet15 = new ArrayList<String>();
        /**
         * Adding weights to the strategies, in the current implementation 
         * this process is done manually.
         */
        for (String s:fourMatchers) {
            
            fourMatchersWegSet1.add(s + "1.0;1.0;1.0;1.0,");
            fourMatchersWegSet2.add(s + "2.0;1.0;1.0;1.0,");
            fourMatchersWegSet3.add(s + "1.0;2.0;1.0;1.0,");
            fourMatchersWegSet4.add(s + "1.0;1.0;2.0;1.0,");
            fourMatchersWegSet5.add(s + "1.0;1.0;1.0;2.0,");
                      
            fourMatchersWegSet6.add(s + "2.0;2.0;1.0;1.0,");
            fourMatchersWegSet7.add(s + "2.0;1.0;2.0;1.0,");
            fourMatchersWegSet8.add(s + "2.0;1.0;1.0;2.0,");
                   
            fourMatchersWegSet9.add(s + "1.0;2.0;2.0;1.0,");                      
            fourMatchersWegSet10.add(s + "1.0;2.0;1.0;2.0,");
                      
            fourMatchersWegSet11.add(s + "1.0;1.0;2.0;2.0,");
                      
            fourMatchersWegSet12.add(s + "1.0;2.0;2.0;2.0,");
            fourMatchersWegSet13.add(s + "2.0;1.0;2.0;2.0,");
            fourMatchersWegSet14.add(s + "2.0;2.0;1.0;2.0,");                      
            fourMatchersWegSet15.add(s + "2.0;2.0;2.0;1.0,");
        }
        
        for (int i = 0; i < fourMatchers.size(); i++) {
            
            int first = 0;
            for (String s: thersholds) {
                /**
                 * In the current implementation the strategies that has even
                 * weights for the matcher gets maximum combination.
                 */ 
                strategies.add(fourMatchersWegSet1.get(i) + "maximum," + s);                
                strategies.add(fourMatchersWegSet1.get(i) + "weighted," + s);                
                
                strategies.add(fourMatchersWegSet2.get(i) + "weighted," + s);               
                
                strategies.add(fourMatchersWegSet3.get(i) + "weighted," + s);                 
                
                strategies.add(fourMatchersWegSet4.get(i) + "weighted," + s);               
                
                strategies.add(fourMatchersWegSet5.get(i) + "weighted," + s);                
                
                strategies.add(fourMatchersWegSet6.get(i) + "weighted," + s);                
                
                strategies.add(fourMatchersWegSet7.get(i) + "weighted," + s);                
                
                strategies.add(fourMatchersWegSet8.get(i) + "weighted," + s);                
                
                strategies.add(fourMatchersWegSet9.get(i) + "weighted," + s);                
                
                strategies.add(fourMatchersWegSet10.get(i) + "weighted," + s);                
                
                strategies.add(fourMatchersWegSet11.get(i) + "weighted," + s);                
                
                strategies.add(fourMatchersWegSet12.get(i) + "weighted," + s);                
                
                strategies.add(fourMatchersWegSet13.get(i) + "weighted," + s);                 
                
                strategies.add(fourMatchersWegSet14.get(i) + "weighted," + s);                
                
                strategies.add(fourMatchersWegSet15.get(i) + "weighted," + s);                
                /**
                 * The method 'addDoubleThreshold' adds both upper and lower
                 * thresholds, so to this method give a strategies without any
                 * threshold.
                 */
                if (first == 0) {
                    addDoubleThreshold(fourMatchersWegSet1.get(i) 
                            + "maximum,");
                    addDoubleThreshold(fourMatchersWegSet1.get(i) 
                            + "weighted,");
                    addDoubleThreshold(fourMatchersWegSet2.get(i) 
                            + "weighted,");
                    addDoubleThreshold(fourMatchersWegSet3.get(i) 
                            + "weighted,");
                    addDoubleThreshold(fourMatchersWegSet4.get(i) 
                            + "weighted,");
                    addDoubleThreshold(fourMatchersWegSet5.get(i) 
                            + "weighted,");
                    addDoubleThreshold(fourMatchersWegSet6.get(i) 
                            + "weighted,");
                    addDoubleThreshold(fourMatchersWegSet7.get(i) 
                            + "weighted,");
                    addDoubleThreshold(fourMatchersWegSet8.get(i) 
                            + "weighted,");
                    addDoubleThreshold(fourMatchersWegSet9.get(i) 
                            + "weighted,");
                    addDoubleThreshold(fourMatchersWegSet10.get(i) 
                            + "weighted,");
                    addDoubleThreshold(fourMatchersWegSet11.get(i) 
                            + "weighted,");
                    addDoubleThreshold(fourMatchersWegSet12.get(i) 
                            + "weighted,");
                    addDoubleThreshold(fourMatchersWegSet13.get(i) 
                            + "weighted,");
                    addDoubleThreshold(fourMatchersWegSet14.get(i) 
                            + "weighted,");
                    addDoubleThreshold(fourMatchersWegSet15.get(i) 
                            + "weighted,");
                }
                first++;
            }
        }
     }
        
     /**
     * Generates five matchers strategies.
     */   
     private void generate5MatchersSet() {
         
         String matcherSet = "";
         ArrayList<String> fiveMatchersWegSet = new ArrayList();
         
         for (String matcher:matchers) {
             matcherSet = matcherSet.concat(matcher).concat(";");
         }
         // To remove last ';'
         matcherSet = matcherSet.substring(0, matcherSet.length()-1);
         // Adding ','
         matcherSet = matcherSet.concat(",");
         
         fiveMatchersWegSet.add(matcherSet + "1.0;1.0;1.0;1.0;1.0,");
      
         fiveMatchersWegSet.add(matcherSet + "2.0;1.0;1.0;1.0;1.0,");
         fiveMatchersWegSet.add(matcherSet + "1.0;2.0;1.0;1.0;1.0,");
         fiveMatchersWegSet.add(matcherSet + "1.0;1.0;2.0;1.0;1.0,");
         fiveMatchersWegSet.add(matcherSet + "1.0;1.0;1.0;2.0;1.0,");
         fiveMatchersWegSet.add(matcherSet + "1.0;1.0;1.0;1.0;2.0,");
      
         fiveMatchersWegSet.add(matcherSet + "2.0;2.0;1.0;1.0;1.0,");
         fiveMatchersWegSet.add(matcherSet + "2.0;1.0;2.0;1.0;1.0,");
         fiveMatchersWegSet.add(matcherSet + "2.0;1.0;1.0;2.0;1.0,");
         fiveMatchersWegSet.add(matcherSet + "2.0;1.0;1.0;1.0;2.0,");

         fiveMatchersWegSet.add(matcherSet + "1.0;2.0;2.0;1.0;1.0,");
         fiveMatchersWegSet.add(matcherSet + "1.0;2.0;1.0;2.0;1.0,");
         fiveMatchersWegSet.add(matcherSet + "1.0;2.0;1.0;1.0;2.0,");
         
         fiveMatchersWegSet.add(matcherSet + "1.0;1.0;2.0;2.0;1.0,");
         fiveMatchersWegSet.add(matcherSet + "1.0;1.0;2.0;1.0;2.0,");

         fiveMatchersWegSet.add(matcherSet + "1.0;1.0;1.0;2.0;2.0,");
         
         fiveMatchersWegSet.add(matcherSet + "2.0;2.0;2.0;1.0;1.0,");
         fiveMatchersWegSet.add(matcherSet + "2.0;1.0;2.0;2.0;1.0,");
         fiveMatchersWegSet.add(matcherSet + "2.0;1.0;1.0;2.0;2.0,");

         fiveMatchersWegSet.add(matcherSet + "1.0;2.0;2.0;2.0;1.0,");
         fiveMatchersWegSet.add(matcherSet + "1.0;1.0;2.0;2.0;2.0,");
         
         fiveMatchersWegSet.add(matcherSet + "1.0;2.0;2.0;2.0;2.0,");
         fiveMatchersWegSet.add(matcherSet + "2.0;1.0;2.0;2.0;2.0,");
         fiveMatchersWegSet.add(matcherSet + "2.0;2.0;1.0;2.0;2.0,");
         fiveMatchersWegSet.add(matcherSet + "2.0;2.0;2.0;1.0;2.0,");
         fiveMatchersWegSet.add(matcherSet + "2.0;2.0;2.0;2.0;1.0,");
         
         int i = 0;
         
         for (String w: fiveMatchersWegSet) {
             for (String threshold: thersholds) {
                 if (i == 0) {
                     strategies.add(w + "maximum," + threshold);
                 }                
                 strategies.add(w + "weighted," + threshold);
             }
             
             if (i == 0) {
                 addDoubleThreshold(w + "maximum,");
             }
             addDoubleThreshold(w + "weighted,");
             i++;
         }
     }     
    
    /**
     * Adding double threshold to the strategies.
     * 
     * @param   Strategy
     */    
    private void addDoubleThreshold(String inputStrategy) {
        
        for(int i = 0; i < thersholds.length; i++) {
            if ((i + 1) < (thersholds.length)) {
                for (int j = i + 1; j < thersholds.length; j++) {
                    strategies.add(inputStrategy + thersholds[i] + 
                            ";" + thersholds[j]);
                }                
            }            
        }
    }
     
     /**
      * To find the factorial of a number.
      * 
      * @param n    Any number greater than 0.
      * 
      * @return     Factorial of an input number. 
      */
     private int factorial(int n) {
         
         int fact = 1;
         if(n == 1) {
             return 1;
         } else {
            for (int i = n; i >= 1; i--) {
                 fact=fact*i;
             }
            return fact;        
         }
     }     
    
    /**
     * To find number of strategies can be generated for given 'r'.
     * 
     * @param r     Number matchers in a strategy.
     * 
     * @return Number of possible strategies.
     */
    private int possibleStrategies (int r) {
        
        /**
         * Combination formula = n! / (n-r)! r!
         * 
         * In our case 
         * n = number of matcher in the list
         * r = number of matcher we want in a strategy. 
         */
        int totalStrategies = factorial(matchers.length) / 
                factorial(matchers.length - r);
        totalStrategies = totalStrategies / factorial(r);
        
        return totalStrategies;
    }
    
    
    public static void main(String args[]) {
        
        GenerateAlignmentStrategies test = new GenerateAlignmentStrategies();   
        ArrayList<String> result = test.getStrategies();   
        test.printStrategies();        
        System.out.println("\n\n\n\nTotal size = "+result.size());
    }
    
}
