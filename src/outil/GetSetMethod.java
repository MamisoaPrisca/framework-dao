/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package outil;

import java.lang.reflect.Method;

/**
 *
 * @author Prisca
 */
public class GetSetMethod {
    Method get;
    Method set;
    
    public GetSetMethod(){
        
    }
    public GetSetMethod(Method get,Method set){
        this.setGet(get);
        this.setSet(set);
    }

    public Method getGet() {
        return get;
    }

    public void setGet(Method get) {
        this.get = get;
    }

    public Method getSet() {
        return set;
    }

    public void setSet(Method set) {
        this.set = set;
    }
    
    
    
}
