/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filemanagment;

/**
 *
 * @author 1ZW05LA_RS3
 */
public class Datos {
    String opcode;
    String operando;
    public Datos(String op, String oper){
        this.opcode = op;
        this.operando = oper;
    }
    String convertidorhex(String operando){ //CONVERTIR LOS OPERANDOS A HEXADECIMAL
         if (operando.charAt(0)=='$'){ //metodo para insertar en la lista (compilador) cuando ya estan en hexadecimal
         }
         else{//insertaremos el caso de cuando es decimal para convertirlo a hexadecimal y agregarlo al array compilador 
             int operandoConvertido= Integer.parseInt("40",16); 
             System.out.println(operandoConvertido);
             //agregarlos
        
         } 
    
}
