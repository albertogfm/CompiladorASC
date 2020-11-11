package filemanagment;
import regex.*;
import java.util.*;


public class Datos {
    public String mnemonico, opcode, localidad, direccionamiento, parts[],opers[];
    public String etiqueta;
    public ArrayList <String> operandos = new ArrayList<String>(); 
    FileMan file = new FileMan(); 
    int saltos, i;
    public Datos(String instruccion){
        SetSplits(instruccion);
    }
    void SetSplits(String instruccion){
        if(instruccion.length()<4)
            this.mnemonico=instruccion.toLowerCase();
        else{
            parts = instruccion.split(" ");
        if(parts.length == 2 && (parts[0].toLowerCase() == "bcrl" || parts[0].toLowerCase() == "bset")){
            this.mnemonico=parts[0].toLowerCase();
                opers = parts[1].split(",");
                System.out.println(opers.length);
                if(opers.length==2){
                    this.operandos.add(opers[0]);
                    this.operandos.add(opers[1]);        
                }
                else{
                    this.operandos.add(opers[0]);
                    this.operandos.add(opers[2]);
                    if(opers[1].charAt(0) == 'x' || opers[1].charAt(0) == 'X' )
                        this.direccionamiento="indx";
                    else
                        this.direccionamiento="indy";
                    }    
        }
        if(parts.length==2){
            this.mnemonico=parts[0].toLowerCase();
            this.operandos.add(parts[1]);
        }
        if(parts.length==3){

           this.etiqueta=parts[2];
           this.mnemonico=parts[0].toLowerCase();
           opers=parts[1].split(",");
           System.out.println(opers[1]);
           if(opers.length==2){
                    this.operandos.add(opers[0]);
                    this.operandos.add(opers[1]);        
                }
                else{
                    this.operandos.add(opers[0]);
                    this.operandos.add(opers[2]);
                        if(opers[1].charAt(0) == 'x' || opers[1].charAt(0) == 'X')
                            this.direccionamiento="indx";
                        else
                            this.direccionamiento="indy";
                    }    
        }
    }
        this.direccionamiento = SetDireccionamiento();
        this.opcode=file.readOpcodes(this.mnemonico, this.direccionamiento);
    }
    String SetDireccionamiento(){
        String direccion;
        ArchivoRegex regex = new ArchivoRegex();
        direccion=regex.matcher(this.mnemonico,this.operandos,this.direccionamiento);
        return direccion;
    }
    void convertidorhex(String operando){ //CONVERTIR LOS OPERANDOS A HEXADECIMAL
         if (operando.charAt(0)=='$'){ //metodo para insertar en la lista (compilador) cuando ya estan en hexadecimal
         }
         else{//insertaremos el caso de cuando es decimal para convertirlo a hexadecimal y agregarlo al array compilador 
             int operandoConvertido= Integer.parseInt("40",16); 
             System.out.println(operandoConvertido);
             //agregarlos
        
         } 
    }
    public void ImprimirDatos(){
        System.out.println("Mnemonico: "+this.mnemonico);
        System.out.println("Modo de direccionamiento: "+this.direccionamiento);
        System.out.println("Operando(s): "); 
        for (int i=0;i<this.operandos.size();i++){
            System.out.println(this.operandos.get(i));
        }
        System.out.println("OpCode: "+this.opcode);
        
    
    }
}
