/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.MModel;

import java.util.Set;

/**
 *
 * @author huali50
 */
public interface testMElement {
    public String getURI();
    public String getLocalName();
    public String getAlignComment();
    public String getAlignName();
    public testMElement getAlignElement();
    //public boolean isMProperty();
        /**Check whether the element is a concpet
     *
     *@return true, if it is a concept
     */
    public boolean isMClass();

    /**Check whether the element is a relation
     *
     *@return true, if it is a relation
     */
    public boolean isMProperty();
    
}
