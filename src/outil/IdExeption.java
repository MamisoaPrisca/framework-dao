/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package outil;

/**
 *
 * @author Prisca
 */
public class IdExeption extends Exception{
    String nomchamp;

    public IdExeption(String nomchamp) {
        this.nomchamp = nomchamp;
    }
    
    public String  getMessage(){
       String message=nomchamp+" commance par '"+ nomchamp.charAt(2) +"'";
       return message;
    }
}
