package se.liu.ida.sambo.text.classifier;

import java.util.*;

/**
 * Creates a list of instances from data files
 * Specializations handle various ways of storing
 * instances.
 *
 * @author Ray Mooney
 */

public abstract class InstancesConstructor {

    /** Return the list of instances for this dataset */
    public abstract List getInstances();
}














