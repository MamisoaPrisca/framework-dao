/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package constante;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author nyamp
 */
public class ConnectionConf {
    private String connection;
    private String user;
    private String password;
    private String forname;
    private String date;
    private String requettepagination;
    private String requettesequence;

    public String getRequettesequence() {
        return requettesequence;
    }

    public void setRequettesequence(String requettesequence) {
        this.requettesequence = requettesequence;
    }

    public String getRequettepagination() {
        return requettepagination;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getForname() {
        return forname;
    }
    
    
    
    public ConnectionConf() throws Exception{
        try{
            this.sets();
        }
        catch(Exception exception){
            throw exception;
        }
    }

    public String getConnection() {
        return connection;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
    
     private static File getFile(String path) throws ClassNotFoundException {
        // Get a File object for the package
        File directory = null;
        try {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            URL resource = cld.getResource(path);
            if (resource == null) {
                throw new ClassNotFoundException("No resource for " + path);
            }
            directory = new File(resource.getFile());
        }
        catch (NullPointerException x) {
            throw new ClassNotFoundException(" (" + directory + ") does not exit ");
        }
        if (!directory.exists()){
            throw new ClassNotFoundException(" (" + directory + ") does not exit ");
        }
        return directory;
    }
    
    public String getChemin(String fichier) throws ClassNotFoundException{
        try{
            
            String path = getFile("conf/"+fichier).getAbsolutePath();
            return  path;
        }
        catch(ClassNotFoundException exception){
            throw exception;
        }
    }
    private String getStringConnection(String typebase,String host,String database) throws IOException, ParseException, ClassNotFoundException{
        String stringConnection=null;
        JSONParser parser = new JSONParser();
        FileReader file=null;
        try {
            String fichier= this.getChemin("connection.json");
            file=new FileReader(fichier);
            Object obj = parser.parse(file);
            JSONArray array =  (JSONArray) obj;
            int taille=array.size();
            for(int i=0;i<taille;i++){
                JSONObject objct=(JSONObject) array.get(i);
                if(objct.get("type").equals(typebase)){
                    this.forname=(String) objct.get("forname");
                    this.setDate((String) objct.get("date"));
                    stringConnection=String.format("%s%s%s%s",objct.get("enteteurl"),host,objct.get("finurl"),database);
                    this.requettepagination=(String) objct.get("pagination");
                    this.requettesequence=(String) objct.get("sequence");

                }
            }
            
        } catch (IOException | ParseException e) {
            throw e;
        }
        finally{
            if(file!=null){
                file.close();
            }
        }
        return stringConnection;
    }
    private String getStringConnection(String typebase,String host) throws IOException, ParseException, ClassNotFoundException{
        return this.getStringConnection(typebase,host,"");
    }
    private void sets() throws Exception{
        JSONParser parser = new JSONParser();
        FileReader file=null;
        try {
            String fichier= this.getChemin("configuration.json");
            file=new FileReader(fichier);
            Object obj = parser.parse(file);
            JSONObject object=(JSONObject) obj;
            this.connection = this.getStringConnection((String)object.get("typebase"),(String)object.get("host"),(String)object.get("base"));
            this.user = (String)object.get("user");
            this.password = (String)object.get("password");
        } catch (IOException | ParseException e) {
            throw e;
        }
        finally{
            if(file!=null){
                file.close();
            }
        }
    }
    
}
