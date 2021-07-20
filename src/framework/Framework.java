/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import java.sql.Date;
import java.sql.Timestamp;
import outil.Attribut;



/**
 *
 * @author nyamp
 */
public class Framework {

    /**
     * @param args the command line arguments
     * @throws java.lang.ClassNotFoundException
     */
    @SuppressWarnings("empty-statement")
    public static void main(String[] args) throws Exception {
    //Connection connect=null;
            try{
               Attribut a=new Attribut(new Timestamp(System.currentTimeMillis()),"date");
               System.out.println(a.getRequette());
            }catch(Exception e){
                    throw e;
            }finally{
//                    if(connect!=null)
//                        connect.close();
            }
       
    }
}