

package se.liu.ida.sambo.algos.matching.algos;


import java.util.StringTokenizer;
import java.util.Vector;
import se.liu.ida.sambo.MModel.MElement;
import se.liu.ida.sambo.algos.matching.Matcher;

/** The EditDistance Clss
 * @by Michael Gilleland, Merriam Park Software
 *
 * @changed by He Tan
 *
 * http://www.merriampark.com/ld.htm
 */
public class EditDistance extends Matcher{
    
    /** Constructs <B>EditDistance</B>*/
    public EditDistance()  { 
    }   
    
   
    
    @Override
    public double getSimValue(String s1, String s2){
        
        return 1.0- ((double)LD(s1, s2)/(double) Handler.max(s1.length(), s2.length()));
    }
 
 
    
       
  //****************************
  // Get minimum of three values
  //****************************

  private int Minimum (int a, int b, int c) {
  int mi;

    mi = a;
    if (b < mi) {
      mi = b;
    }
    if (c < mi) {
      mi = c;
    }
    return mi;

  }

  //*****************************
  // Compute Levenshtein distance
  //*****************************

  private double LD (String s, String t) {
  int d[][]; // matrix
  int n; // length of s
  int m; // length of t
  int i; // iterates through s
  int j; // iterates through t
  char s_i; // ith character of s
  char t_j; // jth character of t
  int cost; // cost

    // Step 1

    n = s.length ();
    m = t.length ();
    if (n == 0) {
      return m;
    }
    if (m == 0) {
      return n;
    }
    d = new int[n+1][m+1];

    // Step 2

    for (i = 0; i <= n; i++) {
      d[i][0] = i;
    }

    for (j = 0; j <= m; j++) {
      d[0][j] = j;
    }

    // Step 3

    for (i = 1; i <= n; i++) {

      s_i = s.charAt (i - 1);

      // Step 4

      for (j = 1; j <= m; j++) {

        t_j = t.charAt (j - 1);

        // Step 5

        if (s_i == t_j) {
          cost = 0;
        }
        else {
          cost = 1;
        }

        // Step 6

        d[i][j] =  Minimum (d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1] + cost);

      }

    }

    // Step 7

    return d[n][m];

  }
  
   public static void main(String[] args) {
      //  for (int i = 0; i < 18; i++) {
            EditDistance test = new EditDistance();
           // System.out.println(test.getSimValue("heart ventricle", "heart ventricle"));
           // System.out.println(test.getSimValue("heart left ventricle", "heart ventricle"));
           // System.out.println(test.getSimValue("heart right ventricle", "heart ventricle"));
            System.out.println(test.getSimValue("palpebra", "sclera"));
             //System.out.println(test.getSimValue("nasal cavity", "paranasal sinus")); 
             System.out.println("\n");
       // }
    }

}
