package compiladorasc;

import regex.*;
import filemanagment.*;
import java.util.*;

/**
 *
 * @author alber
 */
public class CompiladorASC {
    static FileMan file = new FileMan();
    public static void main(String[] args) {
        int des;
        Scanner sc = new Scanner(System.in); 
        boolean rep = true;
        while(rep){
            sc.nextLine();
            System.out.println("Ingresa una instruccion");
            String instruccion = sc.nextLine();
            Datos prueba = new Datos (instruccion);
            if(prueba.mnemonico==null){
                System.out.println("Ya termine");
            }
            else{
                file.instrucciones.add(prueba);
                prueba.ImprimirDatos();
                Compilador();     
            }
            System.out.println("Agregamos otra?\n1.- Si\n2.- No");
            des=sc.nextInt();
            if(des==2)
                rep=false;
        }
    }
    
    static public void Compilador(){//Recibe un FileMan
        Datos ingreso = file.instrucciones.poll();
        String nuevo, convertido;
        int limite=ingreso.operandos.size()-1,convertidor,large;
        for(int i=0; i<limite; i++){
            if(ingreso.operandos.get(i).charAt(0)=='$' || ingreso.operandos.get(i).substring(0, 2).equals ("#$")){
                System.out.println("Es hexa");
                large = ingreso.operandos.get(i).length();
                if(ingreso.operandos.get(i).substring(0, 2).equals ("#$")){
                   nuevo = ingreso.operandos.get(i).substring(2, large);
                    System.out.println(nuevo);
                }
                else{
     
                }
            }
            else{
                System.out.println("No es hexa");
                large = ingreso.operandos.get(i).length();
                convertidor = Integer.parseInt(ingreso.operandos.get(i));
                convertido = Integer.toHexString(convertidor);
                System.out.println(convertido);
            }    
        }
        //ingreso.ImprimirDatos();
    }
    
}
      
    
    

