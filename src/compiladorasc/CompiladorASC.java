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
        FileMan file = new FileMan();
        Datos prueba = new Datos ("BRCLR $07");
        //prueba.ImprimirDatos();
        file.instrucciones.add(prueba);
        file.imprimir();
    }
    
    public void Compilador(){
        
    }
    
}
      
    
    

