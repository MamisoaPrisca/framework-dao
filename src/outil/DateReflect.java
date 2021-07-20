/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package outil;

import java.sql.Date;

/**
 *
 * @author nyamp
 */
public class DateReflect {
    public static Date getPaque(int annee)throws Exception{
        Date paque=null;
        if(annee<=1583)throw new Exception("Inpossible de calculer la date de paque en cette annee");
        int n=annee%19;//cycle de Meton
        int u=annee%100;// rang de l'année 
        int c=annee/100;//centaine et de l'annee
//siècle bissextile
        int s=c/4;
        int t=c%4;
        int p=(c+8)/25;//cycle de proemptose
        int q=(c-p+1)/3;//proemptose
        int e=((19*n)+c-s-q+15)%30;//épacte 
//année bissextile 	
        int b=u/4;
        int d=u%4;
        int l=((2*t)+(2*b)-e-d+32)%7;//lettre dominicale 
        int h=(n+(2*b)+(22*l))/451;//correction 
        int m=(e+l+(7*h)+114)/31;//mois du samedi saint
        int j=(e+l+(7*h)+114)%31;//quantieme du samedi saint
        if(m==3)//mois de mars
                paque=DateReflect.stringToDate(annee+"-"+m+"-"+(j+1));
        else if(m==4)//mois d'avril
                paque=DateReflect.stringToDate(annee+"-"+m+"-"+(j+1));

        return paque;
    }
    
    public static boolean dateEntre2date(Date daterecent , Date dateancient, Date dateverifier){
        boolean verifier=false;
        Date temp=null;
        if(daterecent.before(dateancient)){
            temp=daterecent;
            daterecent=dateancient;
            dateancient=temp;
        }
        if(dateancient.equals(dateverifier)==true || daterecent.equals(dateverifier)==true)
                 verifier=true;
        if(dateverifier.before(daterecent)==true && dateverifier.after(dateancient)==true){
                verifier=true;
        }
        return verifier;
    }
    
    public static Date[] getFerier(int annee)throws Exception{
        Date[] ferier=new Date[4];
        ferier[0]=Date.valueOf(annee+"-01-01");
        ferier[1]=DateReflect.ajouterJour(1,DateReflect.getPaque(annee));
        ferier[2]=Date.valueOf(annee+"-11-01");
        ferier[3]=Date.valueOf(annee+"-12-25");

        return ferier;
    }
    
    public static Date ajouterJourOuvrable(int jour,Date date) throws Exception{
        int nbr=0;
        int nonouvrable=0;
        Date ret=null;
        if(jour==0)
            return date; 
        int signe=jour/Math.abs(jour);
        
        try{
            System.out.print(nbr);
            if(jour>0){
            while(nbr-nonouvrable<jour){
                nbr=jour+(nonouvrable);
                ret=DateReflect.ajouterJour(nbr, date);
                if(date.after(ret)){
                    nonouvrable=DateReflect.nbrJourNonOuvrable(date, ret);
                }
                else{
                    nonouvrable=DateReflect.nbrJourNonOuvrable(ret, date);
                }
                nbr++;
           }
            }
            else{
            while(nbr+(nonouvrable)>jour){
                nbr=jour+(nonouvrable);
                ret=DateReflect.ajouterJour(nbr, date);
                if(date.after(ret)){
                    nonouvrable=DateReflect.nbrJourNonOuvrable(date, ret);
                }
                else{
                    nonouvrable=DateReflect.nbrJourNonOuvrable(ret, date);
                }
                nbr--;
           }
            }
        }
        catch(Exception exception){
            throw exception;
        }
        
        return ret;
    }

    public static java.sql.Date ajouterJour(int jour,java.sql.Date date){
        long jourenmilliseconde=jour*86400000;
        long dateenmilisseconde=date.getTime();
        return new java.sql.Date(dateenmilisseconde+jourenmilliseconde); 
    }
    
    public static int nbrJourNonOuvrable(Date daterecent , Date dateancient) throws Exception{
        int nbr=0;
        try{
            nbr+=DateReflect.nbrFerier(daterecent, dateancient)+DateReflect.nbrWeenkend(daterecent, dateancient);
        }
        catch(Exception exception){
            throw exception;
        }
        return nbr;
    }
    
    public static int nbrFerier(Date daterecent , Date dateancient)throws Exception{
        int nbr=0;
        Date temprecente=null;
        Date tempacient=null;
        Date[]ferier=null;
        int annee=1900;
        int differenceannee=daterecent.getYear()-dateancient.getYear();
        if(differenceannee==0){
            ferier=getFerier(daterecent.getYear()+1900);
            for(int i=0;i<ferier.length;i++){
                if(DateReflect.dateEntre2date(daterecent,dateancient,ferier[i])==true){
                        if(ferier[i].getDay()!=0 && ferier[i].getDay()!=6){
                                nbr++;
                        }
                }
            }
        }
        else{
            temprecente=daterecent;
            annee+=temprecente.getYear();
            tempacient=Date.valueOf(annee+"-01-01");
            ferier=getFerier(temprecente.getYear()+1900);
            for(int j=0;j<2;j++){
                for(int i=0;i<ferier.length;i++){
                    if(DateReflect.dateEntre2date(temprecente,tempacient,ferier[i])==true){
                        if(ferier[i].getDay()!=0 && ferier[i].getDay()!=6){
                            nbr++;
                        }
                    }
                }

                tempacient=dateancient;
                annee=1900+tempacient.getYear();
                temprecente=Date.valueOf(annee+"-12-31");
                ferier=getFerier(temprecente.getYear()+1900);
            }

            nbr+=(differenceannee-1)*4;
        }

        return nbr;
    }

    public static int nbrFerier(String daterecent , String dateancient)throws Exception{
        return nbrFerier(DateReflect.stringToDate(daterecent),DateReflect.stringToDate(dateancient));
    }

    public static  int differenceJour(Date d1,Date d2){
        long l=d1.getTime()-d2.getTime();
        Long ret=l/86400000;
        int difference=ret.intValue();
        return difference;
    }
        
    public static int nbrWeenkend(Date daterecent , Date dateancient)throws Exception{

        //plus 1 pour que le premier jour de la semaine=1;
        int jourrecent=dateancient.getDay()+1;
        int difference=differenceJour(daterecent,dateancient);
        int jourdelasemaine=7;
        int weekend=(difference/jourdelasemaine)*2;
        int reste=difference%jourdelasemaine;
        int somme=reste+jourrecent;
        if(somme==7) weekend++;
        else if(somme>7)weekend+=2;

        try{
            weekend+=DateReflect.nbrFerier(daterecent,dateancient);
        }catch(Exception e){
            weekend=0;
        }
        return weekend;
    }
    
    public static Date stringToDate(String date)throws Exception{
        int annee=1111,mois=12,jour=31;
        Integer i;
        String[]d=date.split("/");
        if(d.length==1){
            d=date.split("-");
            if(d.length==1)throw new Exception("La date n'est pas correct , separez la date par'-' ou '/'"); 
        }
        if(d.length>3)throw new Exception("La date invalide");
        if(d[0].length()==4){
            i=new Integer(d[0]);
            annee=i;
            i=new Integer(d[1]);
            mois=i;
            i=new Integer(d[2]);
            jour=i;
        }
        else if(d[2].length()==4){
            i=new Integer(d[2]);
            annee=i;
            i=new Integer(d[1]);
            mois=i;
            i=new Integer(d[0]);
            jour=i;
        }
        else throw new Exception("La date invalide, le format du date est jour-mois-année ou année-mois-jour");
        return Date.valueOf(annee+"-"+mois+"-"+jour);
    }
}
