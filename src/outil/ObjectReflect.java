/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package outil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import annotation.AnnotationField;
/**
 *
 * @author Prisca
 */



public class ObjectReflect {
    static String majuscule(String s){
        String ret=s;
        Character c=ret.charAt(0);
        c=Character.toUpperCase(c);
        char[]ch=s.toCharArray();
        ch[0]=c;
        String mot=new String(ch);
        return mot;
    }
    
    static Method getMethode(Class classe,String nomchamp) throws NoSuchMethodException{
        Method method=null;
         try{
                    method= classe.getMethod("get"+majuscule(nomchamp));
                }
                catch(NoSuchMethodException | SecurityException exception){
                    try{
                        method= classe.getMethod("get"+nomchamp);               
                    }
                    catch(NoSuchMethodException | SecurityException exception2){
                        try{
                            nomchamp=nomchamp.toLowerCase();
                            method= classe.getMethod("get"+nomchamp);
                        }
                        catch(SecurityException exception3){
                            throw exception3;
                        }

                    }
                }
        return method;
    }
    
    public static ArrayList<Attribut> getAttributsNotNull(Object object) throws NoSuchMethodException, IllegalAccessException, Exception{
       ArrayList<Attribut> attributs=new ArrayList();
        Class classe=object.getClass();
        Field[] fields=null;
        Method method=null;
        String nomchamp=null;
        Object attribut=null;
        String column=null;
        AnnotationField annotationField=null;
        while(!classe.getSimpleName().equals("Object")){
            fields=classe.getDeclaredFields();
            for(Field field : fields){
                annotationField = field.getAnnotation(AnnotationField.class);
                if(annotationField!=null){
                    column=annotationField.attribut();
                    try{
                        nomchamp=field.getName();
                        method= getMethode(classe, nomchamp);
                        attribut=method.invoke(object);
                            if(attribut!=null){
                                if((attribut.getClass().isPrimitive() ||  attribut instanceof Number)  ){
                                    if(((Number)attribut).intValue()!=0){
                                        attributs.add(new Attribut(attribut,column));
                                    }
                                }
                                else{
                                    attributs.add(new Attribut(attribut,column));
                                }
                            }
                    }
                    catch(Exception exception){
                        throw exception;
                    }
                }
                
            }
            classe=classe.getSuperclass();
        }
        return attributs;
    }
}
