/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package errores;

import java.util.*;

/**
 *
 * @author alber
 */
public class ErrorASC{
    public String typeError;
    public int lineaError;


    public ErrorASC(int type,int linea){
        this.lineaError = linea+1;
        switch (type) {
            case 1:
                this.typeError = ("001 VARIABLE O CONSTANTE O ETIQUETA INEXISTENTE");
                break;
            //case 2:
                //this.typeError = ("002 VARIABLE O CONTANTE INEXISTENTE");
                //break;   
            case 3:
                this.typeError = ("003 ETIQUETA INEXISTENTE");
                break; 
            case 4:
                this.typeError = ("004 MNEMÓNICO INEXISTENTE");
                break;            
            case 5:
                this.typeError = ("005 INSTRUCCIÓN CARECE DE OPERANDO(S)");
                break;  
            
            case 6:
                this.typeError = ("006 INSTRUCCION NO LLEVA OPERANDO(S)");
                break;
            case 7:
                this.typeError =("007 MAGNITUD DE OPERANDO ERRONEA");
                 break;   
            case 8:
                this.typeError = ("008 SALTO RELATIVO MUY LEJANO");
                break; 
            case 9:
                this.typeError = ("009 INSTRUCCIÓN CARECE DE ALMENOS UN ESPACIO RELATIVO AL MARGEN");
                break;            
            case 10:
                this.typeError = ("010 NO SE ENCUENTRA END");
                break;  
        }
    }
    public String toString(){
        return this.typeError +" en la línea "+ this.lineaError+"\n";
    }
}
