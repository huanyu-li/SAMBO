/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.MModel;

import java.util.HashMap;

/**
 * Map from id and URI
 * @author huali50
 */
public class testURITable {
    //Map of Integer id and URI
    private HashMap<Integer, String> indexURI;
    //Map of URI and Integer id
    private HashMap<String, Integer> URIindex;
    private int size;
    public testURITable()
    {
        indexURI = new HashMap<Integer,String>();
        URIindex = new HashMap<String,Integer>();
        size = 0;
    }
    /**
     * Add uri 
     * @author huali50
     * @param uri 
     */
    public void addURI(String uri){
        if(!URIindex.containsKey(uri)){
            size = size+1;
            indexURI.put(new Integer(size), uri);
            URIindex.put(uri, new Integer(size));
        }
    }
    /**
     * Get index based on URI
     * @author huali50
     * @param uri
     * @return id or 0
     */
    public int getIndex(String uri){
        if(URIindex.containsKey(uri))
            return URIindex.get(uri);
        else
            return 0;
    }
    /**
     * Get URI baed on index
     * @author huali50
     * @param index
     * @return URI or null
     */
    public String getURI(int index){
        if(indexURI.containsKey(index))
            return indexURI.get(index);
        else
            return null;
    }
}
