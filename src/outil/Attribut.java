/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package outil;

import java.util.Date;

/**
 *
 * @author Prisca
 */
public  class Attribut {
    Object value;
    String name;
    Class type;
    String operateur="like";

    public Attribut(Object value, String name) {
        this.setValue(value);
        this.setName(name);
    }

    public Attribut(Object value) {
        this.value=value;
        this.type=value.getClass();
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
        type=value.getClass();
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public Class getType() {
        return type;
    }
    
    public String getRequette(){
        
        if(value instanceof Date || value instanceof Number){
            operateur="=";
        }
        return String.format(" %s %s ? ",name,operateur);
    }
}
