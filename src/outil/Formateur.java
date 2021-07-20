/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package outil;

import java.text.DecimalFormat;
import java.text.FieldPosition;

/**
 *
 * @author nyamp
 */
public class Formateur {
    public static String formaterLong(long nbr) {
        String result = new String();
        DecimalFormat formatter = new DecimalFormat();
        result = "" + formatter.format(nbr, new StringBuffer()
            , new FieldPosition(DecimalFormat.FRACTION_FIELD));
        return result;
    }
}
