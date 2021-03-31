/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import annotation.AnnotationCache;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import outil.Reflect;

/**
 *
 * @author Sanda
 */
public class Cache extends HashMap<String,List<Object>>{
    private Cache(){}
    
    /** Instance unique pré-initialisée */
    private static Cache INSTANCE = null;

    /** Point d'accès pour l'instance unique du singleton
     * @param object object conrrespondant à un table ou vue  */
    
    
    public void delete(Object object){
        boolean verifier=false;
        Class classe=object.getClass();
        AnnotationCache annotation=(AnnotationCache) classe.getAnnotation(AnnotationCache.class);
        if(annotation!=null){
            String[] tableau=annotation.cacher();
            int taille=tableau.length;
            for(int i=0;i<taille;i++){
                this.remove(tableau[i].toLowerCase());
            }
        }
    }
    
    public static Cache getInstance()
    {		
        if (INSTANCE == null)
        {
            INSTANCE = new Cache();	
        }
        return INSTANCE;
    }
    public boolean exist(String table){
        return this.containsKey(table.toLowerCase());
    }
    public void add(String table,List liste) {
        this.put(table.toLowerCase(),liste);
    }
//  reset
    public void reset(String table){
        this.remove(table.toLowerCase());
    }
    public List get(String table,Object objet,List liste) throws Exception{
        List ret=null;
        try{
            ret=Reflect.select(objet, this.get(table));
        }
        catch(Exception exception){
            throw exception;
        }
        return ret;
    }
}
