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
public class ErrorASC{//Con esta clase tratamos todos los errores que puedan estar en el archivo 
    public String typeError;
    public int lineaError;


    public ErrorASC(int type,int linea){//Se utilizó un switch-case para identificar cada error 
        this.lineaError = linea+1;
        switch (type) {
            case 1:
                this.typeError = ("001 VARIABLE O CONSTANTE O ETIQUETA INEXISTENTE");//Se englobaron los tres errores de constante, variable y etiqueta inexistente para manejarlos de manera más fácil
                break;
            //case 2: PRUEBA
                //this.typeError = ("002 VARIABLE O CONTANTE INEXISTENTE");
                //break;   
            //case 3: PRUEBA
                //this.typeError = ("003 ETIQUETA INEXISTENTE");
                //break; 
            case 4:
                this.typeError = ("004 MNEMÓNICO INEXISTENTE"); //Error: mnemónico inexistente 
                break;            
            case 5:
                this.typeError = ("005 INSTRUCCIÓN CARECE DE OPERANDO(S)"); //Error: instrucción sin operandos
                break;  
            
            case 6:
                this.typeError = ("006 INSTRUCCION NO LLEVA OPERANDO(S)"); //Error: instrucción no lleva operandos 
                break;
            case 7:
                this.typeError =("007 MAGNITUD DE OPERANDO ERRONEA"); //Error: La magnitud del operando no es la correcta
                 break;   
            case 8:
                this.typeError = ("008 SALTO RELATIVO MUY LEJANO"); //Error: Salto lejano
                break; 
            case 9:
                this.typeError = ("009 INSTRUCCIÓN CARECE DE ALMENOS UN ESPACIO RELATIVO AL MARGEN"); //Error: La instrucción no tiene espacios al margen 
                break;            
            case 10:
                this.typeError = ("010 NO SE ENCUENTRA END"); //Error: El programa no tiene final  
                break;  
        }
    }
    public String toString(){ //Regresa el tipo de error en la línea que se encuentra 
        return this.typeError +" en la línea "+ this.lineaError+"\n";
    }
}
