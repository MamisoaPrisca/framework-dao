/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package outil;

import annotation.AnnotationField;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Prisca
 */
public class Reflect {
    public static File getFile(String path) throws ClassNotFoundException {
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

    public static void setObject(Object object,String fieldname,Object value) throws Exception{
        Class classobject=object.getClass();
        Method method;
        String methodname="set"+majuscule(fieldname);
        Method[] methods;
        try{
            method=classobject.getDeclaredMethod(methodname, value.getClass());
        }
        catch(NoSuchMethodException | SecurityException exception1){
            try{
                methodname="set"+fieldname;
                method=classobject.getDeclaredMethod(methodname, value.getClass());
            }
            catch(NoSuchMethodException | SecurityException exception2){
               
                    throw exception2;
            }
        }
    }
    
    public static List select(Object objet,List liste) throws IllegalAccessException, Exception{
        List newliste=new ArrayList();
        try{
            List<Method>methods=getAttributsNotNull(objet);
            int taille=methods.size();
            int taille2=liste.size();
            boolean verifier=true;
            Object[] getValue=new Object[taille];
            for(int i=0;i<taille;i++){
                getValue[i]=methods.get(i).invoke(objet, (Object) null);
            }
            for(int j=0;j<taille2;j++){
                for(int i=0;i<taille;i++){
                    if(!getValue[i].equals(methods.get(i).invoke(liste.get(j), (Object) null))){
                        verifier=false;
                        break;
                    }
                }
                if(verifier)
                    newliste.add(liste.get(j));
                verifier=true;
            }
        }
        catch(Exception exception){
            throw exception;
        }
        return newliste;
    }
    
    
    private static List<Method> getAttributsNotNull(Object object) throws NoSuchMethodException, IllegalAccessException, Exception{
       ArrayList<Attribut> attributs=new ArrayList();
        Class classe=object.getClass();
        Field[] fields=null;
        Method method=null;
        String nomchamp=null;
        Object attribut=null;
        List<Method> methods=new ArrayList();
        AnnotationField annotationField=null;
        while(!classe.getSimpleName().equals("Object")){
            fields=classe.getDeclaredFields();
            for(Field field : fields){
                annotationField = field.getAnnotation(AnnotationField.class);
                if(annotationField!=null){
                    try{
                        nomchamp=field.getName();
                        method= ObjectReflect.getMethode(classe, nomchamp);
                        attribut=method.invoke(object);
                            if(attribut!=null){
                                if((attribut.getClass().isPrimitive() ||  attribut instanceof Number)  ){
                                    if(((Number)attribut).intValue()!=0){
                                        methods.add(method);
                                    }
                                }
                                else{
                                    methods.add(method);
                                }
                            }
                    }
                    catch(IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException exception){
                        throw exception;
                    }
                }
                
            }
            classe=classe.getSuperclass();
        }
        return methods;
    }

    
    public static String majuscule(String s){
        String ret=s;
        Character c=ret.charAt(0);
        c=c.toUpperCase(c);
        char[]ch=s.toCharArray();
        ch[0]=c;
        String mot=new String(ch);
        return mot;
    }
    public static Field[] getField(Object o){
        Class c=o.getClass();
        return getField(c);
    }
    
    public static Field[] getField(Class c){
            
            Field[]ret=c.getDeclaredFields();
            while(1<2){
                    c=c.getSuperclass();
                    if(c.getName().compareTo("java.lang.Object")==0)break;
                    Field[]champ=c.getDeclaredFields();
                    Field[]tab=new Field[ret.length+champ.length];
                    int j=0;
                    for(int i=0;i<tab.length;i++){
                            if(i<ret.length)tab[i]=ret[i];
                            else{
                                    tab[i]=champ[j];
                                    j++;
                            }
                    }
                    ret=tab;
            }
            return ret;
    }
    
    
	public static String formater(double nbr) {
		String result = new String();
		DecimalFormat formatter = new DecimalFormat();
		result = "" + formatter.format(nbr, new StringBuffer()
			, new FieldPosition(DecimalFormat.FRACTION_FIELD));
		return result;
	}
	
        public static String chiffreEnLettre(double nbr){
		Double i=new Double(nbr);
		return chiffreEnLettre(i.intValue());
	}
    
        public static String chiffreEnLettre(int nbr){
        String[] valeur = {"","un","deux","trois","quatre","cinq","six","sept","huit","neuf","dix","onze","douze","treze","quatorze","quinze","seize","dix-sept","dix-huit","dix-neuf","vingt",
					"vingt-un","vingt-deux","vingt-trois","vingt-quatre","vingt-cinq","vingt-six","vingt-sept","vingt-huit","vingt-neuf","trente",
					"trente-un","trente-deux","trente-trois","trente-quatre","trente-cinq","trente-six","trente-sept","trente-huit","trente-neuf","quarente",
					"quarente-un","quarente-deux","quarente-trois","quarente-quatre","quarente-cinq","quarente-six","quarente-sept","quarente-huit","quarente-neuf","cinquante",
					"cinquante-un","cinquante-deux","cinquante-trois","cinquante-quatre","cinquante-cinq","cinquante-six","cinquante-sept","cinquante-huit","cinquante-neuf","soixante",
					"soixante-un","soixante-deux","soixante-trois","soixante-quatre","soixante-cinq","soixante-six","soixante-sept","soixante-huit","soixante-neuf","soixante-dix",
					"soixante-onze","soixante-douze","soixante-treze","soixante-quatorze","soixante-quinze","soixante-seize","soixante-dix-sept","soixante-dix-huit","soixante-dix-neuf","quatre-vingt",
					"quatre-vingt-un","quatre-vingt-deux","quatre-vingt-trois","quatre-vingt-quatre","quatre-vingt-cinq","quatre-vingt-six","quatre-vingt-sept","quatre-vingt-huit","quatre-vingt-neuf","quatre-vingt-dix",
					"quatre-vingt-onze","quatre-vingt-douze","quatre-vingt-treze","quatre-vingt-quatorze","quatre-vingt-quinze","quatre-vingt-seize","quatre-vingt-dix-sept","quatre-vingt-dix-huit","quatre-vingt-dix-neuf"};
        String lettre = "";

        String[] valeur1 = {"","milles","millions","milliards"};
        int n =0;
        while(nbr>0){
            int cet = nbr%100;
            int ml = nbr%1000;
            if(ml > 0){
            lettre = valeur1[n] + " " + lettre;
            }

             if(ml > 1 || n!= 1){
            lettre = valeur[cet] + " " + lettre;
            }
            nbr = nbr / 100;
            
            int dix = nbr%10;
            if(dix > 0){
                lettre = "cent " + lettre;
            }

            if(dix > 1){
            lettre = valeur[dix] + " " + lettre;
            }
            nbr = nbr / 10;

            if(n==3){
                n=0;
            }

            n++;
        }
        return lettre;
    }
	
        public static Class getClass(Object[]liste){
		Class c=null;
			if(liste.length!=0){
				c=liste[0].getClass();
				if(c.getName().equals("java.lang.Object")==true){
					c=c.getSuperclass();
				}
			}
		return c;
	}
	
        public static double somme(List vec,String champ,String champcond,String valueCond)throws Exception{
		Class c=vec.get(0).getClass();Method m;Method m2;
		while(1<2){
			try{
				m=c.getMethod("get"+majuscule(champ));
				break;
			}catch(Exception e){
				try{
					m=c.getMethod("get"+champ.toLowerCase());
					break;					
				}catch(Exception f){
					try{
						m=c.getMethod("get"+majuscule(champ.toLowerCase()));
						break;
					}catch(Exception g){
						try{
							m=c.getMethod("get"+champ);
							break;
						}catch(Exception h){
							if(c.getName().compareTo("java.lang.Object")==0)
                                                            throw new Exception("Champ non valide");
							c=c.getSuperclass(); 
						}
					}
				}
			}
		}while(1<2){
			try{
				m2=c.getMethod("get"+majuscule(champcond));
				break;
			}catch(Exception e){
				try{
					m2=c.getMethod("get"+champcond.toLowerCase());
					break;					
				}catch(Exception f){
					try{
						m2=c.getMethod("get"+majuscule(champcond.toLowerCase()));
						break;
					}catch(Exception g){
						try{
							m2=c.getMethod("get"+champcond);
							break;
						}catch(Exception h){
							if(c.getName().compareTo("java.lang.Object")==0)
                                                            throw new Exception("Champ non valide");
							c=c.getSuperclass(); 
						}
					}
				}
			}
		}
		double ret=0;
                int taille=vec.size();
		for(int i=0;i<taille;i++){
			if(m2.invoke(vec.get(i)).toString().compareToIgnoreCase(valueCond)==0){
				Double d=(Double)m.invoke(vec.get(i));
				ret=ret+d.doubleValue();
			}
		}
		return ret;
		
	}
	
        public static double somme(List vec,String champ)throws Exception{
		Class c=vec.get(0).getClass();Method m;
		while(1<2){
			try{
				m=c.getMethod("get"+majuscule(champ));
				break;
			}catch(Exception e){
				try{
					m=c.getMethod("get"+champ.toLowerCase());
					break;					
				}catch(Exception f){
					try{
						m=c.getMethod("get"+majuscule(champ.toLowerCase()));
						break;
					}catch(Exception g){
						try{
							m=c.getMethod("get"+champ);
							break;
						}catch(Exception h){
                                                        try{
                                                            m=c.getMethod(champ);
                                                            break;
                                                        }
                                                        catch(Exception i){
                                                            if(c.getName().compareTo("java.lang.Object")==0)
                                                                throw new Exception("Champ non valide");
                                                            c=c.getSuperclass(); 
                                                        }
						}
					}
				}
			}
		}
		double ret=0;
		for(int i=0;i<vec.size();i++){
			Double d=(Double)m.invoke(vec.get(i));
			ret=ret+d.doubleValue();
		}
		return ret;
		
	}
	
	public static double somme(Object[]vec,String champ)throws Exception{
		Class c=vec[0].getClass();Method m;
		while(1<2){
			try{
				m=c.getMethod("get"+majuscule(champ));
				break;
			}catch(Exception e){
				try{
					m=c.getMethod("get"+champ.toLowerCase());
					break;					
				}catch(Exception f){
					try{
						m=c.getMethod("get"+majuscule(champ.toLowerCase()));
						break;
					}catch(Exception g){
						try{
							m=c.getMethod("get"+champ);
							break;
						}catch(Exception h){
							if(c.getName().compareTo("java.lang.Object")==0)throw new Exception("Champ non valide");
							c=c.getSuperclass(); 
						}
					}
				}
			}
		}
		double ret=0;
		for(int i=0;i<vec.length;i++){
			Double d=(Double)m.invoke(vec[0]);
			ret=ret+d;
		}
		return ret;
		
	}
	

	public static Method[]getMethods(Class c, String[]att)throws Exception{
		Method[]met=new Method[att.length];
		for( int i=0;i<att.length;i++){
			Method m=null;
			try{
				m=c.getMethod("get"+att[i]);
				met[i]=m;
			}
			catch(Exception e){
				try{
					m=c.getMethod("get"+majuscule(att[i]));
					met[i]=m;

				}catch(Exception e2){
					throw e2;
				}
			}
		}
		return met;
	}
    
}
