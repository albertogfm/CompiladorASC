/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiladorasc;



import regex.*;
import filemanagment.*;
import java.util.ArrayList;

/**
 *
 * @author alber
 */
public class CompiladorASC {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        FileMan trataFiles = new FileMan();
        //System.out.println(trataFiles.readOpcodes("adca","INDX"));
        trataFiles.leerArchivo("ERROR");
        for (int i = 0; i < 500; i++) {
            System.out.println(trataFiles.lineasArchivoASC.get(i)); 
        }
      
    }
    
}
