/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import annotation.AnnotationCache;
import annotation.AnnotationClass;
import annotation.AnnotationField;
import constante.ConnectionConf;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import outil.Attribut;
import static outil.ObjectReflect.getAttributsNotNull;
import static outil.Reflect.majuscule;

/**
 *
 * @author Prisca
 */
public class BaseDAO{
    
    private static BaseDAO instance=null;
    
    /**
     * 
     * @return  retourne l' instance de BaseDAO
     */
    public static BaseDAO dao(){
        if(instance==null)
            instance=new BaseDAO();
        return instance; 
    }
   
    /**
     * 
     * @param sequence nom de la sequence
     * @param connection connection à la base de donné
     * @return la valeur du sequence
     * @throws Exception   sequence qui n'existe pas dans la base
     */
    public int nextVal(String sequence,Connection connection)throws Exception{
        int value=0;
        boolean verifierConnection=false;
        String requette=getRequetteSequence();
        requette=String.format(requette,sequence);
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        try{
            if(connection==null)
            {
                connection=this.connected();
                verifierConnection=true;
            }
            System.out.println("requette="+requette);
            preparedStatement=connection.prepareStatement(requette);
            resultSet=preparedStatement.executeQuery();
            resultSet.next();
            value=resultSet.getInt(1);
        }
        catch(Exception exception){
            throw exception;
        }
        finally{
            if(resultSet!=null)
                resultSet.close();
            if(preparedStatement!=null)
                preparedStatement.close();
            if(verifierConnection)
                connection.close();
        } 
        
        return value;
    }
           
                   
    private String getRequetteSequence() throws Exception{
        String requette="";
        ConnectionConf configuration;
        try{
            configuration=new ConnectionConf();
            requette=configuration.getRequettesequence();
        }
        catch(Exception exception){
            throw  exception;
        }
        return requette;
    }
    
    
    private  BaseDAO(){}
    
    
    /**
     * 
     * @param objectCondition les attributs non null sont les conditions
     * @param connection connection à la base de donné
     * @throws Exception nom de table nom specifier ou qui n'existe pas, objectCondition null, objectCondition null, 
     * colonne  pas definie dans la table,
     * nom de table qui n'est pas definie dans la classe corespondant
     */   
    public void delete(Object objectCondition,Connection connection) throws Exception{
        delete(null,objectCondition,connection) ;
    }
    /**
     * 
     * @param table nom de la table pour faire l'action d'effacer
     * @param objectCondition les attributs non null sont les conditions 
     * @param connection connection à la base de donné
     * @throws Exception nom de table non specifier ou qui n'existe pas dans la base, objectCondition null, objectCondition null, colonne 
     * pas definie dans la table
     */
    public void delete(String table,Object objectCondition,Connection connection) throws Exception{
         table=verifierTable(objectCondition,table);
        String requette="delete from  "+table;
        List<Attribut>condition;
        PreparedStatement preparedStatement =null;
        boolean verifierConnection = false;
        int insert;
        try{
//preparation  du requette
            condition=getAttributsNotNull(objectCondition);
            int tailleCondition=condition.size();
            requette+=this.getWhere(condition, null,true);
// connection
            if(connection==null){
                connection=this.connected();
                verifierConnection=true;
            }
            System.out.println(requette);
            preparedStatement=connection.prepareStatement(requette);
            for(int i=0;i<tailleCondition;i++){
                preparedStatement.setObject(i+1 ,condition.get(i).getValue());
            }
            insert=preparedStatement.executeUpdate();
            
            if(insert>0){
                Cache.getInstance().delete(objectCondition);
            }
            
        }
        catch(Exception exception){
            throw exception;
        }
        finally{
            if(preparedStatement !=null)
                preparedStatement.close();
            if(verifierConnection)
                connection.close();
        }
        
    }
    
        
    private String verifierTable(Object object,String table) throws Exception{
        if(table==null){
            Class c=object.getClass();
            AnnotationClass annotationClass = (AnnotationClass) c.getAnnotation(AnnotationClass.class);
            
            if(annotationClass!=null){
                table=annotationClass.table();
            }
            else{
                 throw new Exception("Il faut definir la table");
            }
        }
        
        return table;
    }
    /**
     * 
     * @param objectCondition les attributs non null sont les conditions
     * @param objectModifier  les attributs non null sont les valeur à modifier 
     * @param connection connection à la base de donné
     * @throws Exception nom de table non specifier ou qui n'existe pas dans la base, objectCondition null, objectCondition null,
     * colonne  pas definie dans la table,
     * nom de table qui n'est pas definie dans la classe corespondant
     */
    public void update(Object objectCondition,Object objectModifier,Connection connection) throws Exception {
        try{
            String table=verifierTable(objectCondition, null);
            this.update(table,objectCondition, objectModifier, connection);
        }
        catch(Exception exception){
            throw exception;
        }
        
    }
   
    
    /**
     * @param table nom de la table pour faire l'action de modifier
     * @param objectCondition les attributs non null sont les conditions 
     * @param objectModifier  les attributs non null sont les valeur à modifier 
     * @param connection connection à la base de donné
     * @throws Exception nom de table non specifier ou qui n'existe pas dans la base, objectCondition null, objectCondition null, colonne 
     * pas definie dans la table
     */
    public void update(String table,Object objectCondition,Object objectModifier,Connection connection) throws Exception{
        table=verifierTable(objectCondition,table);
        String requette="update "+table+" set ";
        List<Attribut>modifier;
        List<Attribut>condition;
        PreparedStatement preparedStatement =null;
        boolean verifierConnection = false;
        int insert;
        if(objectModifier==null)
            throw new Exception("Il faut precicer l'objet à modifier");
        try{
//preparation  du requette
            modifier=getAttributsNotNull(objectModifier);
            int tailleModifier = modifier.size();
            for(int i=0;i<tailleModifier;i++){
                if(i!=0)
                    requette+=" , ";
                requette+=String.format(" %s = ? ", modifier.get(i).getName());
                
            }
            
            condition=getAttributsNotNull(objectCondition); 
            int tailleCondition = 0;
            if(condition!=null ){
                tailleCondition=condition.size();
                for(int i=0;i<tailleCondition;i++){
                    if(i!=0)
                        requette+=" and ";
                    else
                        requette += " where ";
                     requette+=String.format(" %s like ? ", condition.get(i).getName());
                }
                
            }
// connection
            if(connection==null){
                connection=this.connected();
                verifierConnection=true;
            }
            System.out.println(requette);
            preparedStatement=connection.prepareStatement(requette);
            int j = 1;
            for(int i=0;i<tailleModifier;i++,j++){
                preparedStatement.setObject(j ,modifier.get(i).getValue());
            }
            for(int i=0;i<tailleCondition;i++,j++){
                preparedStatement.setObject(j ,condition.get(i).getValue());
            }
            insert=preparedStatement.executeUpdate();
            
            if(insert>0){
                Cache.getInstance().delete(objectCondition);
            }
            
        }
        catch(Exception exception){
            throw exception;
        }
        finally{
            if(preparedStatement !=null)
                preparedStatement.close();
            if(verifierConnection)
                connection.close();
        }
        
    }
    
    /**
     * 
     * @param objet  les attributs non null sont les valeurs à inserer
     * @param connection connection à la base de donné
     * @throws  Exception nom de table non specifier ou qui n'existe pas dans la base, objectCondition null,
     * colonne  pas definie dans la table,
     * nom de table qui n'est pas definie dans la classe corespondant
     */
    public void insert(Object objet,Connection connection) throws  Exception{
        insert(null,objet, connection);
    }
    
    /**
     * 
     * @param table nom de la table pour faire l'action de modifier
     * @param objet  les attributs non null sont les valeurs à inserer  ,
     * nom de table definie dans la classe corespondant
     * @param connection connection à la base de donné
     * @throws java.lang.ClassNotFoundException le champs n'a pas la méthode get ou set 
     * @throws  Exception nom de table non specifier ou qui n'existe pas dans la base, objectCondition null, colonne 
     * pas definie dans la table
     */
    public void insert(String table,Object objet,Connection connection) throws ClassNotFoundException, Exception{
        table=verifierTable(objet,table);
        List<Attribut>insertion;
        String requette = "insert into "+ table;
        String colomn = "(";
        String value = "(";
        PreparedStatement preparedStatement =null;
        boolean verifierConnection = false;
        int insert;
        try{
            insertion=getAttributsNotNull(objet);
            if(insertion==null)
                throw new Exception("Il faut des valeurs à inserer");
            int taille=insertion.size();
            for(int i=0;i<taille;i++){
                if(i>0){
                  colomn+=" , ";
                  value+=",";
                }
                colomn+=insertion.get(i).getName();
                value+="?";
            }
            colomn+=")";
            value+=")";
       
        requette+=colomn+" values "+value;
            if(connection==null){
                connection=this.connected();
                verifierConnection=true;
            }
            System.out.println(requette);
            preparedStatement=connection.prepareStatement(requette);
            for(int i=0;i<taille;i++){
                preparedStatement.setObject(i+1 ,insertion.get(i).getValue());
            }
            insert=preparedStatement.executeUpdate();
           
            if(insert>0){
                Cache.getInstance().delete(objet);
            }
        }
        catch(ClassNotFoundException | SQLException exception){
            throw exception;
        }
        finally{
            if(preparedStatement !=null)
                preparedStatement.close();
            if(verifierConnection)
                connection.close();
        }
    }
    
    /**
     * 
     * @param object les attributs non null sont les conditions
     * @param connection connection à la base de donné
     * @throws  Exception nom de table non specifier ou qui n'existe pas dans la base, objectCondition null,
     * colonne pas definie dans la table
     * @return list d'objet , dont les objets sont de meme instance que l' object condition
     */
    public List select(Object object,Connection connection) throws Exception {
        return select(null,object,null,connection);
    }
    
    /**
     * 
     * @param object les attributs non null sont les conditions 
     * @param apresWhere se sont condition tel que : order by ...  
     * @param connection connection à la base de donné
     * @throws  Exception nom de table non specifier ou qui n'existe pas dans la base, objectCondition null, colonne 
     * pas definie dans la table
     * @return list d'objet , dont les objets sont de meme instance que l' object condition
     */
    public List select(Object object,String apresWhere,Connection connection) throws Exception {
        return select(null,object,apresWhere,connection);
    }
    
    private boolean isCache(Object object,String table){
        boolean verifier=false;
        Class classe=object.getClass();
        AnnotationCache annotation=(AnnotationCache) classe.getAnnotation(AnnotationCache.class);
        if(annotation!=null){
            String[] tableau=annotation.cacher();
            int taille=tableau.length;
            for(int i=0;i<taille;i++){
                if(tableau[i].equalsIgnoreCase(table)){
                   verifier =true;
                }
            }
        }
        return verifier; 
    }

    /**
     * 
     * @param table nom de la table pour faire l'action d'effacer
     * @param object les attributs non null sont les conditions 
     * @param apresWhere se sont condition tel que : order by ...  
     * @param connection connection à la base de donné
     * @throws  Exception nom de table non specifier ou qui n'existe pas dans la base, objectCondition null, colonne 
     * pas definie dans la table
     * @return list d'objet , dont les objets sont de meme instance que l' object condition
     */
    public List select(String table,Object object,String apresWhere,Connection connection) throws  Exception{

        List resultat=null;
        try{
            List<Attribut>condition=getAttributsNotNull(object);
            resultat=select(table,object,condition,apresWhere,true,connection);
        }
        catch(Exception exception){
            throw exception;
        }
        return resultat;
    }
    
    private  List getResultat(PreparedStatement preparedStatement,Class classObject) throws ClassNotFoundException, SQLException, IllegalAccessException, IllegalArgumentException, InstantiationException, NoSuchMethodException, InvocationTargetException{
        List resultat=new ArrayList();
        ResultSet resultSet=null;
        boolean next;
        Method[] methods;
        Object temp;
        Object result;
        Class[] c;
        int taille;
        try{
            resultSet=preparedStatement.executeQuery();
                methods=findMethod(classObject,resultSet);
                taille=methods.length;
                next=resultSet.next();

                while(next)
                {
                    temp=classObject.newInstance();
                    for(int i=0;i<taille;i++){

                        if(methods[i]!=null){
                            c=methods[i].getParameterTypes();
                            if(c[0].getSimpleName().equals("int") || c[0].getSimpleName().equals("Integer"))
                                result=resultSet.getInt(i+1);
                            else if(c[0].getSimpleName().equals("double") || c[0].getSimpleName().equals("Double"))
                                result=resultSet.getDouble(i+1);
                            else if(c[0].getSimpleName().equals("long") || c[0].getSimpleName().equals("long"))
                                result=resultSet.getLong(i+1);
                            else if(c[0].getSimpleName().equals("String"))
                                result=resultSet.getString(i+1);
                            else if(c[0].getSimpleName().equals("Timestamp"))
                                result=resultSet.getTimestamp(i+1);
                            else if(c[0].getSimpleName().equals("Date"))
                                result=resultSet.getDate(i+1);
                            else 
                                result=resultSet.getObject(i+1);
                            try{
                                methods[i].invoke(temp,result);
                            }
                            catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException exception){
                                 throw exception;
                            }
                        }
                    }
                    resultat.add(temp);
                    next=resultSet.next();

                }
        }
        catch(ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | InvocationTargetException | SQLException exception){
            throw  exception;
        }
        finally{
            if(resultSet!=null)
                resultSet.close();
        }
        return resultat;
    }
    
    
    private List select(String table,Object object, List<Attribut>condition,String apresWhere,boolean separateurIsAnd,Connection connection) throws ClassNotFoundException, Exception{
//donnee
        table=verifierTable(object,table);
        List resultat=new ArrayList(0);
        boolean verifierConnection=false;
        String requette="select * from "+table;
        int taille;
        String where;
        PreparedStatement preparedStatement=null;
        if(object==null){
            throw new Exception("objet ne doit pas etre null");
        }
        try{
            
            where=this.getWhere(condition, apresWhere,separateurIsAnd);
//verification du connection
            if(connection==null)
            {
                connection=this.connected();
                verifierConnection=true;
            }
//verifier cache
            boolean verifiercache=false;
            Cache cache=Cache.getInstance();
            boolean existeCache=this.isCache(object,table);
            if(existeCache && where.equals("")){
               // verifier anaty cache
                if(cache.exist(table)){
                    verifiercache = true;
                    resultat=cache.get(table);
                    
                }
            }
            if(!verifiercache){
                
//construction du requette;
                requette+=where;
                System.out.println("requette="+requette);
                preparedStatement=connection.prepareStatement(requette);
                taille=condition.size();
                for(int i=0;i<taille;i++){
                    preparedStatement.setObject(i+1,condition.get(i).getValue());
                }
                resultat=this.getResultat(preparedStatement, object.getClass());
                if(existeCache){
                    Cache.getInstance().add(table,resultat);
                }
            }
            
        }
        catch(ClassNotFoundException | SQLException exception){
            throw exception;
        }
        finally{
            if(preparedStatement!=null)
                preparedStatement.close();
            if(verifierConnection)
                connection.close();
        }
        return resultat;
    }
     
    
    public List selectTableau(String table,Object object,List condition,String apresWhere,Connection connection) throws ClassNotFoundException, Exception{
//donnee
        table=verifierTable(object,table);
        List resultat=new ArrayList(0);
        boolean verifierConnection=false;
        String requette="select * from "+table;
        int taille;
        String where;
        PreparedStatement preparedStatement=null;
        if(object==null){
            throw new Exception("objet ne doit pas etre null");
        }
        try{
            HashMap map=this.getWhereArray(condition, apresWhere);
            where=(String) map.get("where");
//verification du connection
            if(connection==null)
            {
                connection=this.connected();
                verifierConnection=true;
            }
//verifier cache
            boolean verifiercache=false;
            Cache cache=Cache.getInstance();
            boolean existeCache=this.isCache(object,table);
            if(existeCache && where.equals("")){
               // verifier anaty cache
                if(cache.exist(table)){
                    verifiercache = true;
                    resultat=cache.get(table);
                    
                }
            }
            if(!verifiercache){
                
//construction du requette;
                requette+=where;
                System.out.println("requette="+requette);
                preparedStatement=connection.prepareStatement(requette);
                taille=condition.size();
                List<Attribut> attribut=(List<Attribut>) map.get("attribut");
                for(int i=0;i<taille;i++){
                    preparedStatement.setObject(i+1,attribut.get(i).getValue());
                }
                resultat=this.getResultat(preparedStatement, object.getClass());
                if(existeCache){
                    Cache.getInstance().add(table,resultat);
                }
            }
            
        }
        catch(ClassNotFoundException | SQLException exception){
            throw exception;
        }
        finally{
            if(preparedStatement!=null)
                preparedStatement.close();
            if(verifierConnection)
                connection.close();
        }
        return resultat;
    }
     
    
    private String getWhere(List<Attribut>condition,String apresWhere,boolean separateurIsAnd) throws Exception{
        String separateur="and";
        if(!separateurIsAnd){
            separateur="or";
        }
        String where = "";
        int taille;
        try{
            //construction du requette;
            if(condition!=null){
                taille=condition.size();
                for(int i=0;i<taille;i++){
                    if(i==0)
                     where+=" where ";
                    else 
                        where+=" "+separateur+" ";
                    where+=condition.get(i).getRequette();
                }
            }
            if(apresWhere!=null)
                where+=" "+apresWhere;  
        }
        catch(Exception exception){
            throw exception;
        }
        return where;
    }
    
    private HashMap getWhereArray(List condition,String afterwhere) throws Exception{
        String where="";
        int taille;
        String temporaire;
        HashMap map=new HashMap();
        List<Attribut> atribut;
        atribut = new ArrayList();
        try{
            //construction du requette;
            if(condition!=null){
                taille=condition.size();
                int size;
                List<Attribut> attributs;
                for(int i=0;i<taille;i++){
                    attributs=getAttributsNotNull(condition.get(i));
                    atribut.addAll(attributs);
                    size=attributs.size();
                    temporaire="";
                    for(int j=0;j<size;j++){
                        if(j>0)
                            temporaire+=" and ";
                        temporaire+=attributs.get(j).getRequette();
                    }
                    if(i>0)
                        where+=" or ";
                    where+=String.format("(%s) ",temporaire);
                }
                if(!where.equals("")){
                    where=String.format(" where (%s)",where);
                }
            }
            if(afterwhere!=null)
                where+=" "+afterwhere;  
        }
        catch(Exception exception){
            throw exception;
        }
        map.put("where",where);
        map.put("attribut",atribut);
        return map;
    }
    
    
    
    private Method[] findMethod(Class laClassDeLobjet, ResultSet resultSet)throws NoSuchMethodException, SQLException, ClassNotFoundException{
        
        ResultSetMetaData resultSetMetaData=resultSet.getMetaData();
        int taille=resultSetMetaData.getColumnCount();
        Method[]tableau=new Method[taille];
        Class cs=laClassDeLobjet;
        Class classChamp;
        String column;
        AnnotationField annotationField;
        Method methodTrouver;
        int j=-1;
        String nomchamp;
        while(1==1)
        {
            if(cs.getName().compareTo("java.lang.Object")==0)break;
            Field[]champ=cs.getDeclaredFields();
            for (Field champ1 : champ) {
                nomchamp=champ1.getName();
                classChamp=champ1.getType();
                annotationField = champ1.getAnnotation(AnnotationField.class);
                
                if(annotationField!=null){
                    column=annotationField.attribut();
                    methodTrouver=null;
                    try{
                        int i=resultSet.findColumn(column);
                        try {
                            methodTrouver = laClassDeLobjet.getMethod("set" + majuscule(nomchamp), classChamp);
                        }
                        catch (NoSuchMethodException | SecurityException e) {
                            try {
                                methodTrouver = laClassDeLobjet.getMethod("set" + majuscule(nomchamp.toLowerCase()),classChamp);
                            } catch (NoSuchMethodException | SecurityException g) {
                                try{
                                    methodTrouver = laClassDeLobjet.getMethod("set" + nomchamp.toLowerCase(), classChamp);
                                }
                                catch(NoSuchMethodException | SecurityException exception){
                                    throw exception;
                                }
                            }
                        }
                        tableau[i-1]=methodTrouver;
                    }
                    catch(SQLException exception){
                    }
                    catch(NoSuchMethodException | SecurityException  exception){
                        throw exception;
                    }
                }
            }
            cs=cs.getSuperclass();
        }
        return tableau;
    }
    
    
    public List select(String table,Object object,Connection connection) throws Exception{
        return select(table,object,null, connection);
    }
    
    /**
     * 
     * @return connction à la base qui corresponse à ce qui est defini   dans le fichier de configuration
     * @throws Exception driver introuvable,
     * ou la base n'existe pas ,mot de passe incorrect ,utisateur invalide
     * 
     */
    public Connection connected() throws Exception{
            Connection connect=null;
            Statement s=null;
            try{
                ConnectionConf configuration = new ConnectionConf();
                Class.forName(configuration.getForname());
                
                connect=DriverManager.getConnection(configuration.getConnection(),configuration.getUser(),configuration.getPassword());
            }catch(Exception e){
                try{
                    URI dbUri = new URI(System.getenv("DATABASE_URL"));
                    String username = dbUri.getUserInfo().split(":")[0];
                    String password = dbUri.getUserInfo().split(":")[1];
                    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

                    connect= DriverManager.getConnection(dbUrl, username, password);
                }
                catch(URISyntaxException | SQLException exception2){
                    throw exception2;
                }
            }finally{
                    if(s!=null)
                        s.close();
            }
            return connect;
    }
        
    private String getpaginationrequette() throws Exception{
        String requette="";
        ConnectionConf configuration;
        try{
            configuration=new ConnectionConf();
            requette=configuration.getRequettepagination();
        }
        catch(Exception exception){
            throw  exception;
        }
        return requette;
    }
    
    public List select(String table,int page,int nombre,Object object,String apresWhere,Connection connection) throws Exception{
        return select(table, page, nombre, object, apresWhere,true, connection);
    }
    /**
     * 
     * @param table nom de la table pour faire l'action d'effacer
     * @param page numero de page à effectuer 
     * @param nombre nombre de resultat
     * @param object les attributs non null sont les conditions 
     * @param apresWhere se sont condition tel que : order by ...  
     * @param separateurIsAnd  
     * @param connection connection à la base de donné
     * @throws  Exception nom de table non specifier ou qui n'existe pas dans la base, objectCondition null, colonne 
     * pas definie dans la table
     * @return list d'objet  de taille nombre au maximun dont les objets sont de meme instance que l' object condition
     */
    public List select(String table,int page,int nombre,Object object,String apresWhere,boolean separateurIsAnd,Connection connection) throws Exception{
        table=verifierTable(object,table);
        List resultat=new ArrayList(nombre);
        List<Attribut>condition;
        boolean verifierConnection=false;
        int debut= (page-1)*nombre+1;
        int fin=debut+nombre-1;
        String requette;
        int taille;
        PreparedStatement preparedStatement=null;        
        if(object==null){
            throw new Exception("objet ne doit pas etre null");
        }
        try{
            
//verification du connection
            if(connection==null)
            {
                connection=this.connected();
                verifierConnection=true;
            }
//verifier cache
            boolean verifiercache=false;
            Cache cache=Cache.getInstance();
            boolean existeCache=this.isCache(object,table);
            if(existeCache){
               // verifier anaty cache
                if(cache.exist(table)){
                    verifiercache = true;
                    resultat=cache.get(table);
                    
                }
            }
            if(!verifiercache){ 
//construction du requette;
                requette=getpaginationrequette();
                condition=getAttributsNotNull(object);
                requette=String.format(requette,table,this.getWhere(condition, apresWhere,separateurIsAnd));
                preparedStatement=connection.prepareStatement(requette);
                int i;
                taille=condition.size();
                for(i=0;i<taille;i++){
                    preparedStatement.setObject(i+1,condition.get(i).getValue());
                    System.out.println("i="+i);
                }
                preparedStatement.setInt(++i, debut);
                System.out.println("i="+i);
                preparedStatement.setInt(++i, nombre);
                System.out.println("i="+i);
                System.out.println("requette="+requette );
                resultat=this.getResultat(preparedStatement, object.getClass());
                if(existeCache){
                    Cache.getInstance().add(table,resultat);
                }
            }
            
        }
        catch(ClassNotFoundException | SQLException exception){
            throw exception;
        }
        finally{
            if(preparedStatement!=null)
                preparedStatement.close();
            if(verifierConnection)
                connection.close();
        }
        return resultat;
    }
    
    
    /**
     * 
     * @param page numero de page à effectuer
     * @param nombre nombre de resultat
     * @param object les attributs non null sont les conditions 
     * @param apresWhere se sont condition tel que : order by ...  
     * @param connection connection à la base de donné
     * @throws  Exception nom de table non specifier ou qui n'existe pas dans la base, objectCondition null, colonne 
     * pas definie dans la table
     * @return list d'objet  de taille nombre au maximun dont les objets sont de meme instance que l' object condition
     */
    public List select(Object object,int page,int nombre,String apresWhere,Connection connection) throws Exception {
        return select(verifierTable(object, null),page,nombre,object,apresWhere,connection);
    }
    
    /**
     * 
     * @param page numero de page à effectuer
     * @param nombre nombre de resultat
     * @param object les attributs non null sont les conditions 
     * @param apresWhere se sont condition tel que : order by ...  
     * @param separateurIsAnd  
     * @param connection connection à la base de donné
     * @throws  Exception nom de table non specifier ou qui n'existe pas dans la base, objectCondition null, colonne 
     * pas definie dans la table
     * @return list d'objet  de taille nombre au maximun dont les objets sont de meme instance que l' object condition
     */
    public List select(Object object,int page,int nombre,String apresWhere,boolean separateurIsAnd,Connection connection) throws Exception {
        return select(verifierTable(object, null),page,nombre,object,apresWhere,separateurIsAnd,connection);
    }
    /**
     * 
     * @param page numero de page à effectuer
     * @param nombre nombre de resultat
     * @param object les attributs non null sont les conditions 
     * @param separateurIsAnd  
     * @param connection connection à la base de donné
     * @throws  Exception nom de table non specifier ou qui n'existe pas dans la base, objectCondition null, colonne 
     * pas definie dans la table
     * @return list d'objet  de taille nombre au maximun dont les objets sont de meme instance que l' object condition
     */
    public List select(Object object,int page,int nombre,boolean separateurIsAnd,Connection connection) throws Exception {
        return select(verifierTable(object, null),page,nombre,object,null,separateurIsAnd,connection);
    }
    
    /**
     * 
     * @param page numero de page à effectuer 
     * @param nombre nombre de resultat
     * @param object les attributs non null sont les conditions  
     * @param connection connection à la base de donné
     * @throws  Exception nom de table non specifier ou qui n'existe pas dans la base, objectCondition null, colonne 
     * pas definie dans la table
     * @return list d'objet  de taille nombre au maximun dont les objets sont de meme instance que l' object condition
     */
    public List select(Object object,int page,int nombre,Connection connection) throws Exception {
        return select(verifierTable(object, null),page,nombre,object,null,connection);
    }
    
    
}
