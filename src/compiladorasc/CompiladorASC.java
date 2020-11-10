/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiladorasc;

import regex.*;
import filemanagment.*;
import java.util.*;

/**
 *
 * @author alber
 */
public class CompiladorASC {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //ArchivoRegex regex = new ArchivoRegex();
        //regex.matcher("LDY 6500");
        Datos prueba = new Datos ("BRCLR $07,X,#$80 SALTO2");
        System.out.println(prueba.mnemonico);
        System.out.println(prueba.etiqueta);
        System.out.println(prueba.direccionamiento);
        prueba.ImprimirOps();
        
    }
    
}
      
    
    

