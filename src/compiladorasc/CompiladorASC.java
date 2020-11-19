package compiladorasc;

import regex.*;
import filemanagment.*;
import java.util.*;
import java.util.regex.*;
/**
 *
 * @author alber
 */
public class CompiladorASC {
    static FileMan file = new FileMan();
    public Queue <String> etiqueta = new LinkedList<>();
    public ArrayList <String> etiquetas = new ArrayList<>();
/*public static void main(String[] args) {
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
    }*/

    
    public void Compilador(FileMan file){//Recibe un FileMan
        ArrayList <String> lineasArc = file.lineasArchivoASC;
        boolean repetir = true;
        int i = 0;
        while(repetir){
            String linea = lineasArc.get(i);
            repetir = select(linea);
        }
        /*boolean lectura = true;
        int =0, caso;
        while(lectura){
            String linea=file.lineasArchivoASC.get(i);
            
        }
        
        
        
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
        //ingreso.ImprimirDatos();*/
    }
    public boolean select (String linea){
        Datos dato;
        int caso;
        Validador checker = new Validador();
        Pattern comentariosUnicamente = Pattern.compile("^((\\*)[a-zA-Z0-9_\\*( )]*)$");
        Matcher onlycomentario = comentariosUnicamente.matcher(linea);
        Pattern comentarios = Pattern.compile("((\\*)[a-zA-Z0-9_\\*( )]*)");      //Matcher y Patern de Comentarios y espacios en blanco
        Matcher comentario = comentarios.matcher(linea);
        Pattern espaciosBlanco = Pattern.compile("^( )*$");
        Matcher espacios = espaciosBlanco.matcher(linea);

        if(espacios.find() || onlycomentario.find()) //Ignora los espacios en blanco
            return true;

        if(comentario.find()){//Ignora el comentario y se queda con la linea de instrucciones
            String[] parts = linea.split("*");
            linea=parts[0];
        }                     
        caso = checker.Reconoce(linea);//Solo linea 
        
        switch(caso){
            case 1://Constante y variable
                String[] parts = linea.split(" ");
                if(parts[1].equals("EQU"))
                file.constantesYvariables.put(parts[0],parts[2]);
                break;
            case 2://Intrucción 
                dato = new Datos(linea);
                file.instrucciones.add(dato);
                break;
            case 3://Etiquetas
                etiqueta.add(linea);
                etiquetas.add(linea);
                break;
            case 4://Fin 
                return false;
        }
        return false;
    }
    
}
      
    
    

