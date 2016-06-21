/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.MModel;

import java.util.HashMap;

/**
 *
 * @author huali50
 */
public class testURITable {
    private HashMap<Integer, String> indexURI;
    private HashMap<String, Integer> URIindex;
    private int size;
    public testURITable()
    {
        indexURI = new HashMap<Integer,String>();
        URIindex = new HashMap<String,Integer>();
        size = 0;
    }
    public void addURI(String uri){
        if(!URIindex.containsKey(uri)){
            size = size+1;
            indexURI.put(new Integer(size), uri);
            URIindex.put(uri, new Integer(size));
        }
        else
        {}
    }
    public int getIndex(String uri){
        if(URIindex.containsKey(uri))
            return URIindex.get(uri);
        else
            return 0;
    }
    public String getURI(int index){
        if(indexURI.containsKey(index))
            return indexURI.get(index);
        else
            return null;
    }
}
