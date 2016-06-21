/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.algos.matching.algos.newRajaram;

import java.util.List;

/**
 * The interface for UMLS query.
 *
 * @author Rajaram
 * @version 1.0
 */
public abstract class UMLSQuery {

    /**
     * <p>
     * This methods gets the CUIDs for the given term.
     * </p>
     *
     * @param term              The term to be searched.
     * @param searchLevel       Search levels between 1 and 3.
     *
     * @return  List that contains CUIDs, empty list is returned
     *          if no CUIDs are found.
     */
    public abstract List<String> getCUIDs(final String term,
            final int searchLevel);
}
