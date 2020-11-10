package regex;

import java.util.*;
import java.util.regex.*;
import filemanagment.*;

public class ArchivoRegex{ //CLASE COMPILADOR
    FileMan opcode;
    int numeroceldas;
    Queue <Datos> instrucciones = new LinkedList();
    public ArchivoRegex(){} //Constructor de la clase
    
    void Compilador(String opcode,String operando){
       Datos instrucción = new Datos(opcode,operando);
       instrucciones.add(instrucción);
        
    } 
 
    
    

    public void matcher(String cadena){ //Método que se encarga de identificar el modo de direccionamiento a través de expresiones regulares y de validarlos
        //NOTAAAAAAAAAAA: AGREGAR EL FUNCIONAMIENTO EN ASSCCI PREGUNTA OBLIGATORIA EL LUNES 
        opcode= new FileMan();
        Pattern expresionINH = Pattern.compile("^([a-zA-Z]{3,6})$"); // Identifica el modo de direccionamiento INHERENTE
        Pattern expresionIMM = Pattern.compile("^([a-zA-Z]{3,6}( )(#)(\\$)?([0-9a-fA-F]{2,4}))$");// Identifica el modo de direccionamiento INMEDIATO
        Pattern expresionEXT = Pattern.compile("^([a-zA-Z]{3,6}( )(\\$)?([0-9a-fA-F]{4}))$");// Identifica el modo de direcciconamiento EXTENDIDO
        Pattern expresionDIR = Pattern.compile("^([a-zA-Z]{3,6}( )(\\$)?([0-9a-fA-F]{2}))$"); // Identifica el modo de direccionamiento  DIRECTO
        Pattern expresionREL = Pattern.compile("^([a-zA-Z]{3,6}( )[a-zA-Z]*)$");// Identifica el modo de direccionamiento el RELATIVO
        Pattern expresionINDX = Pattern.compile("^([a-zA-Z]{3,6}( )(\\$)?([0-9a-fA-F]{2})(,[xX]))$");// Identifica el modo de direccionamiento INDEXADO RESPECTO A "Y"
        Pattern expresionINDY = Pattern.compile("^([a-zA-Z]{3,6}( )(\\$)?([0-9a-fA-F]{2})(,[yY]))$");// Identifica el modo de direccionamiento INDEXADO RESPECTO A "X"
        Pattern comentarios = Pattern.compile("((\\*)[a-zA-Z0-9\\*( )]*)");//Identifica los comentarios 


        Matcher inh = expresionINH.matcher(cadena);//Valida la expresión INHERENTE
        Matcher imm = expresionIMM.matcher(cadena);//Valida la expresión INMEDIATO
        Matcher ext = expresionEXT.matcher(cadena);//Valida la expresión EXTENDIDO
        Matcher dir = expresionDIR.matcher(cadena);//Valida la expresión DIRCETO
        Matcher rel = expresionREL.matcher(cadena);//Valida la expresión RELATIVO       
        Matcher indx = expresionINDX.matcher(cadena);//Valida la expresión INDEXADO RESPECTO A "X"
        Matcher indy = expresionINDY.matcher(cadena);//Valida la expresión INDEXADO RESPECTO A "Y"
        Matcher coment = comentarios.matcher(cadena);//Valida comentarios

      //En esta sección identificamos el modo de direccionamiento, y con el método readOpcodes obtenemos el opcode del mnemónico de acuerdo a su modo de direccionamiento  
        if(inh.find()){
            System.out.println("inh");
            String[] parts = cadena.split(" ");
            String mnemonico = parts[0];
            String operando = parts[1];       
             System.out.println(opcode.readOpcodes(mnemonico.toLowerCase(),"inh"));
        }
        if(imm.find()){
            System.out.println("imm");
            String[] parts = cadena.split(" ");
            String mnemonico = parts[0];
            String operando = parts[1];   
            System.out.println(opcode.readOpcodes(mnemonico.toLowerCase(),"imm"));
        }
        if(ext.find()) { 
            System.out.println("Extendido");  
            String[] parts = cadena.split(" ");
            String mnemonico = parts[0];
            String operando = parts[1];   
             System.out.println(opcode.readOpcodes(mnemonico.toLowerCase(),"ext"));
        }  
        if(dir.find()){
            System.out.println("Directa");
            String[] parts = cadena.split(" ");
            String mnemonico = parts[0];
            String operando = parts[1];   
             System.out.println(opcode.readOpcodes(mnemonico.toLowerCase(),"dir"));
        }
        if(rel.find()){
            System.out.println("Relativo");
            String[] parts = cadena.split(" ");
            String mnemonico = parts[0];
            String operando = parts[1];  
             System.out.println(opcode.readOpcodes(mnemonico.toLowerCase(),"rel"));
        }
        if(indx.find()){
            System.out.println("Indexado en X");
            String[] parts = cadena.split(" ");
            String mnemonico = parts[0];
            String operando = parts[1]; 
             System.out.println(opcode.readOpcodes(mnemonico.toLowerCase(),"indx"));
        }
        if(indy.find()){
            System.out.println("Indexado en Y");
            String[] parts = cadena.split(" ");
            String mnemonico = parts[0];
            String operando = parts[1]; 
             System.out.println(opcode.readOpcodes(mnemonico.toLowerCase(),"indy"));
        }
        
}

}


