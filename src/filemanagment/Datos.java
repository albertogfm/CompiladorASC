package filemanagment;
import regex.*;
import java.util.*;
import errores.*;
import compiladorasc.*;
import java.util.regex.*;

public class Datos {
    public String mnemonico, opcode, localidad, direccionamiento, parts[], opers[];
    public String etiqueta, localidadetiqueta, valor;
    public static int contador;
    public ArrayList <String> operandos = new ArrayList<String>();
    public ArrayList <String> relativos= new ArrayList<String>();
    FileMan file = new FileMan(); 
    int saltos, i,numLinea;
    public CompiladorASC com = new CompiladorASC();
    
    public Datos(String instruccion, Queue <String> etiqueta,ArrayList<String> etiquetas){// LDAA $45 Constructor de la clase, generará un menemónico con su direccionamiento y opcode correspondiente
        SetSplits(instruccion,etiqueta,etiquetas);
    }
    public Datos(String instruccion, Queue <String> etiqueta,ArrayList<String> etiquetas, int numlinea){// LDAA $45 Constructor de la clase, generará un menemónico con su direccionamiento y opcode correspondiente
        this.numLinea=numlinea;
        SetSplits(instruccion,etiqueta,etiquetas);
    }
    void SetSplits(String instruccion, Queue <String> etiqueta,ArrayList<String> etiquetas){
        Pattern TagorCons = Pattern.compile("^([A-Za-z]+)([0-9]*)");               
        Matcher checker;
        if(instruccion.length()<5){ //Si la instrucción tiene menos de 5 caracteres y no tiene espacio, significa que la instrucción tiene direccionamiento inherente.
            if(etiqueta.peek()!=null)
                this.etiqueta=etiqueta.poll();
            this.mnemonico=instruccion.toLowerCase();
            this.direccionamiento= "inh";
            this.localidad = SetLocalidad(contador);
            this.operandos.add(" ");
        }
        
        else{ //En otro caso, evaluaremos caso por caso para generar la instrucción correctamente.
            parts = instruccion.split(" ");
            if(mnemonicosREL(parts[0])){
                this.mnemonico=parts[0].toLowerCase();
                this.operandos.add(parts[1]);
                this.direccionamiento="rel";
                this.localidad=SetLocalidad(contador);
                this.opcode=file.readOpcodes(this.mnemonico,this.direccionamiento);
                if(this.opcode == null){                 
                    file.errores.add(new ErrorASC(this.numLinea,4));
                    return;
                }
                this.contador+=1;
                if(this.opcode.length()==4)
                    this.contador+=2;
                else
                    this.contador+=1;
                return;
            }
            if(parts[0].equals("ORG")||parts[0].equals("org")){
                String nuevaLocalidad = parts[1].substring(1);
                contador=hexadecimalADecimal(nuevaLocalidad);
                return;
            }
            
            if(parts.length == 2 && (parts[0].toLowerCase().equals("bclr") || parts[0].toLowerCase().equals("bset"))){
                if(etiqueta.peek()!=null)
                    this.etiqueta=etiqueta.poll();
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
                    if(etiqueta.peek()!=null)
                        this.etiqueta=etiqueta.poll();
                    if(mnemonicosREL(parts[0].toLowerCase())){
                        this.operandos.add(parts[1]);
                        this.localidad = SetLocalidad(contador);   
                        this.contador+=1;
                    }
                    checker = TagorCons.matcher(parts[1]);
                    if(parts[1].charAt(0)=='#')
                        if(parts[1].charAt(1)=='$')
                            checker = TagorCons.matcher(parts[1].substring(0));
                        else
                            checker = TagorCons.matcher(parts[1].substring(1));
                    if(checker.find()){
                        if(file.constantesYvariables.containsKey(parts[1].substring(1))){
                            this.localidad = SetLocalidad(contador);
                            this.mnemonico=parts[0].toLowerCase();
                            if(parts[1].charAt(0)=='#')
                                valor = file.constantesYvariables.get(parts[1].substring(1));
                            else
                                valor = file.constantesYvariables.get(parts[1]);
                            if(valor.substring(1).length() == 2)
                                this.contador += 1;
                            else
                                this.contador += 2;
                            this.operandos.add(valor);
                            this.direccionamiento = SetDireccionamiento(valor);
                            this.opcode=file.readOpcodes(this.mnemonico,this.direccionamiento);
                            if(this.opcode == null){
                                file.errores.add(new ErrorASC(this.numLinea,4));
                                return;
                            }
                            if(this.opcode.length()==4)
                                this.contador+=2;
                            else
                                this.contador+=1;  
                            return;
                        }    
                        if(etiquetas.contains(parts[1])){
                            if(mnemonicosREL(parts[0])){}
                            else{
                                this.localidad = SetLocalidad(contador);   
                                this.operandos.add(parts[1]);
                                this.contador+=2;
                                this.direccionamiento= "ext";
                                this.mnemonico=parts[0].toLowerCase();
                                this.opcode=file.readOpcodes(this.mnemonico,this.direccionamiento);
                                if(this.opcode == null){
                                    file.errores.add(new ErrorASC(this.numLinea,4));
                                    return;
                                }
                                if(this.opcode.length()==4)
                                    this.contador+=2;
                                else
                                    this.contador+=1;
                                return;
                            }
                            
                        }
                    }                    
                    else{
                        this.localidad = SetLocalidad(contador);   
                        if(parts[1].substring(1).length() == 2 || parts[1].substring(2).length()== 2 )
                            this.contador+=1;
                        else
                            this.contador+=2;
                        this.operandos.add(parts[1]);   
                    }
                }    
                this.mnemonico=parts[0].toLowerCase();      
            }
            if(parts.length==3){
                if(etiqueta.peek()!=null)
                    this.etiqueta=etiqueta.poll();
                this.mnemonico=parts[0].toLowerCase();
                this.localidad = SetLocalidad(contador);
                opers=parts[1].split(",");
                if(opers.length==2){
                    this.operandos.add(opers[0]);
                    this.operandos.add(opers[1]);
                    this.operandos.add(parts[2]);
                    this.contador+=3;
                }
                else{
                    this.operandos.add(opers[0]);
                    this.operandos.add(opers[2]);
                    this.operandos.add(parts[2]);
                    this.contador+=3;
                    if(opers[1].charAt(0) == 'x' || opers[1].charAt(0) == 'X')
                        this.direccionamiento="indx";
                    else
                        this.direccionamiento="indy";
                    } 
                }
        }
       
        this.direccionamiento = SetDireccionamiento(this.operandos.get(0));
        this.opcode=file.readOpcodes(this.mnemonico,this.direccionamiento);
        if(this.opcode == null){
            file.errores.add(new ErrorASC(this.numLinea,4));
            return;
        }
        if(this.opcode.length()==4)
            this.contador+=2;
        else
            this.contador+=1;
         
   }
    
    String SetDireccionamiento(String operandos){
        String direccion;
        ArchivoRegex regex = new ArchivoRegex();
        direccion=regex.matcher(this.mnemonico,operandos,this.direccionamiento);
        return direccion;
    }
    
    public void ImprimirDatos(){
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("Mnemonico: "+this.mnemonico);
        System.out.println("Modo de direccionamiento: "+this.direccionamiento);
        if(this.direccionamiento.equals("inh"))
            System.out.println("Direcionamiento inherente no tiene operandos");
        else{
            System.out.println("Operando(s): ");
            for (int i=0;i<this.operandos.size();i++){
                System.out.println(this.operandos.get(i));
            }
        }
        System.out.println("OpCode: "+this.opcode);
        System.out.println("Localidad: "+this.localidad);
        if(this.etiqueta!=null)
            System.out.println("Etiqueta: "+this.etiqueta); 
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
    public boolean mnemonicosREL(String instruccion){
        this.relativos.add("bcc");
        this.relativos.add("bcs");
        this.relativos.add("beq");
        this.relativos.add("bge");
        this.relativos.add("bgt");
        this.relativos.add("bhi");
        this.relativos.add("bhs");
        this.relativos.add("ble");
        this.relativos.add("blo");
        this.relativos.add("bls");
        this.relativos.add("blt");
        this.relativos.add("bmi");
        this.relativos.add("bne");
        this.relativos.add("bpl");
        this.relativos.add("bra");
        this.relativos.add("brn");
        this.relativos.add("bsr");
        this.relativos.add("bvc");
        this.relativos.add("bvs");
        if(this.relativos.contains(instruccion.toLowerCase()))
            return true;
        return false;
    }
    
}
