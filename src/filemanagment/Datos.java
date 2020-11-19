package filemanagment;
import regex.*;
import java.util.*;
import errores.*;

public class Datos {
    public String mnemonico, opcode, localidad, direccionamiento, parts[], opers[];
    public String etiqueta;
    public static int contador;
    public ArrayList <String> operandos = new ArrayList<String>();
    //public ArrayList <String> localidades = new ArrayList<String>(); 
    FileMan file = new FileMan(); 
    int saltos, i;
    
    public Datos(String instruccion){// LDAA $45 Constructor de la clase, generará un menemónico con su direccionamiento y opcode correspondiente
        SetSplits(instruccion);
    }
    
    void SetSplits(String instruccion){                             
        
        if(instruccion.length()<4){ //Si la instrucción tiene menos de 5 caracteres y no tiene espacio, significa que la instrucción tiene direccionamiento inherente.
            //Si intruccion = org
            //metododeError()
            //Si no
            this.mnemonico=instruccion.toLowerCase();
            this.direccionamiento= "inh";
            this.localidad = SetLocalidad(contador);
            //exception no operandos
            this.operandos.add(" ");
        }
        
        else{ //En otro caso, evaluaremos caso por caso para generar la instrucción correctamente.
            parts = instruccion.split(" ");
            
            if(parts[0].equals("ORG")||parts[0].equals("org")){
                String nuevaLocalidad = parts[1].substring(1);
                contador=hexadecimalADecimal(nuevaLocalidad);    
                return;
            }
            
            if(parts.length == 2 && (parts[0].toLowerCase().equals("bclr") || parts[0].toLowerCase().equals("bset"))){
                System.out.println("aqui si");
                this.mnemonico=parts[0].toLowerCase();
                this.localidad=SetLocalidad(contador);
                opers = parts[1].split(",");
                
                if(opers.length==2){ 
                    this.operandos.add(opers[0]);
                    this.operandos.add(opers[1]);
                    this.contador+=2;
                }
                
                else{
                    this.operandos.add(opers[0]);
                    this.operandos.add(opers[2]);
                    this.contador+=2;
                    if(opers[1].charAt(0) == 'x' || opers[1].charAt(0) == 'X' )
                        this.direccionamiento="indx";
                    else
                        this.direccionamiento="indy";
                }   
            }
            
            
            else{   
            if(parts.length==2){
                System.out.println("Aqui no");
                this.mnemonico=parts[0].toLowerCase();
                this.localidad = SetLocalidad(contador);
                this.operandos.add(parts[1]);         
                if(parts[1].substring(1).length() == 2 || parts[1].substring(2).length()==2 || parts[1].substring(0).length()==2 || parts[1].substring(1, 3).length()==2 || parts[1].substring(0, 3).length() == 2)
                    this.contador+=1;
                else
                    this.contador+=2;
            }
            
            if(parts.length==3){
                System.out.println("Entre");
                this.etiqueta=parts[2];
                this.mnemonico=parts[0].toLowerCase();
                this.localidad = SetLocalidad(contador);
                opers=parts[1].split(",");
                if(opers.length==2){
                    this.operandos.add(opers[0]);
                    this.operandos.add(opers[1]);
                    this.contador+=3;
                }
                else{
                    this.operandos.add(opers[0]);
                    this.operandos.add(opers[2]);
                    this.contador+=3;
                    if(opers[1].charAt(0) == 'x' || opers[1].charAt(0) == 'X')
                        this.direccionamiento="indx";
                    else
                        this.direccionamiento="indy";
                    }    
                }
            }
        }
        this.direccionamiento = SetDireccionamiento();
        try{
            this.opcode=file.readOpcodes(this.mnemonico,this.direccionamiento);
            if(this.opcode.length()==4)
                contador+=2;
            else
                contador+=1; 
        }
        catch(NullPointerException e){
            Error err = new Error();
            err.MnemonicNotFound();
            System.out.println(err.getMessage());

        }
   }
    String SetDireccionamiento(){
        String direccion;
        ArchivoRegex regex = new ArchivoRegex();
        direccion=regex.matcher(this.mnemonico,this.operandos.get(0),this.direccionamiento);
        return direccion;
    }
    
    public void ImprimirDatos(){
        System.out.println("Mnemonico: "+this.mnemonico);
        System.out.println("Modo de direccionamiento: "+this.direccionamiento);
        System.out.println("Operando(s): ");
        for (int i=0;i<this.operandos.size();i++){
            System.out.println(this.operandos.get(i));
        }
        System.out.println("OpCode: "+this.opcode);
        System.out.println("Localidad: "+this.localidad);
        if(this.etiqueta!=null)
            System.out.println(this.etiqueta);
    
    }
    public String SetLocalidad(int localidad){
        String localHex;
        localHex=Integer.toHexString(localidad);
        return localHex;
    }
    public static int hexadecimalADecimal(String hexadecimal) {
    int decimal = 0;
    // Saber en cuál posición de la cadena (de izquierda a derecha) vamos
    int potencia = 0;
    // Recorrer la cadena de derecha a izquierda
    for (int x = hexadecimal.length() - 1; x >= 0; x--) {
        int valor = caracterHexadecimalADecimal(hexadecimal.charAt(x));
        long elevado = (long) Math.pow(16, potencia) * valor;
        decimal += elevado;
        // Avanzar en la potencia
        potencia++;
    }
    return decimal;
}
    public static int caracterHexadecimalADecimal(char caracter) {
    switch (caracter) {
        case 'A':
            return 10;
        case 'B':
            return 11;
        case 'C':
            return 12;
        case 'D':
            return 13;
        case 'E':
            return 14;
        case 'F':
            return 15;
        default:
            return Integer.parseInt(String.valueOf(caracter));
    }
}
}
