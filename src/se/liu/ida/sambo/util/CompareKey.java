/*
 * CompareKey.java
 *
 */

package se.liu.ida.sambo.util;

/**
 *
 * @author  He Tan
 * @version 
 */

import com.objectspace.jgl.BinaryPredicate;

 
public final class CompareKey implements BinaryPredicate{

  /**
   * Return true if the first operand is less than the second operand.
   * @param first The first operand, which is converted into a String if necessary.
   * @param second The second operand, which is converted into a String if necessary.
   * @return first.toString() < second.toString()
   */
  public boolean execute( Object first, Object second )
    {
        return first.toString().compareTo(second.toString()) < 0;
    }
 
}