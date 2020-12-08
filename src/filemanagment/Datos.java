package filemanagment;
import regex.*;
import java.util.*;
import errores.*;
import compiladorasc.*;
import java.util.regex.*;

//La clase "Datos" nos permite manejar todos los datos de nuestro archivo como son los mnemonicos, opcode, localidad y direccionamiento 
public class Datos {
    //Atributos de la clase
    public String mnemonico, opcode, localidad, direccionamiento, parts[], opers[];
    public String etiqueta, localidadetiqueta, valor,nuevo;
    public static int contador; //Este atributo nos ayuda a determinar la localidad de memoria en la cual insertamos la instrucción
    public ArrayList <String> operandos = new ArrayList<String>(); //Se creó una lista de operandos para poder guardarlos
    public ArrayList <String> relativos= new ArrayList<String>();  //Se creó una lista "relativos" para guardar instrucciones que sean relativas
    FileMan file = new FileMan(); 
    public int saltos, i,numLinea;
    public CompiladorASC com = new CompiladorASC();

    //Métodos de la clase

    public Datos(String instruccion, Queue <String> etiqueta,ArrayList<String> etiquetas, int numlinea){// Constructor de la clase, generará un mnemónico con su direccionamiento, operandos  y opcode correspondiente
        
        this.numLinea=numlinea; //Asociamos a la instrucción el numero de linea que tiene en el archivo
        SetSplits(instruccion,etiqueta,etiquetas,numlinea);//El método SetSplits se encarga de construir el dato correctamente.
    }
    
    void SetSplits(String instruccion, Queue <String> etiqueta,ArrayList<String> etiquetas,int linea){ //Generar el dato correctamente
        Pattern TagorCons = Pattern.compile("^(([A-Za-z_0-9]+))$"); //Esta expresión regular nos ayudará a revisar si los operandos son identificadores de constantes,variables,etiquetas o si se trata de algun valor numerico en hexadecimal o decimal.            
        Matcher checker;
        String ind;
        if(instruccion.length()<=5){ //Si la instrucción tiene menos de 5 caracteres y no tiene espacio, significa que la instrucción tiene direccionamiento inherente.
            if(etiqueta.peek()!=null)//Si se leyo una etiqueta previamente, le asociamos a la misma la localidad del dato a crear.
                this.etiqueta=etiqueta.poll();

            if(file.readNemon(instruccion.toLowerCase())){//El método readNemon nos ayuda a identificar si la linea contiene un mnemonico valido, en caso contrario se crea el error "MNEMONICO INEXISTENTE"
                this.mnemonico=instruccion.toLowerCase();
                this.direccionamiento= "inh";//Identifica si la instrucción es "inherente"
                this.localidad = SetLocalidad(contador);//Se identifica su localidad
                this.operandos.add(" ");//Al no tener operandos se rellena con un espacio vacio 
                //Trataremos los opcode de las instrucciones 
                this.opcode=file.readOpcodes(this.mnemonico,this.direccionamiento); //Con este método obtenemos el opcode de la instrucción.
                if(this.opcode.length()==4)//Si el opcode tiene 16 bits, le sumamos 2 localidades de memoria en caso contrario solo le agragamos 1 localidad
                    this.contador+=2;
                else
                    this.contador+=1;
            }
            else{//Si el mnemonico no es valido se generará el error tipo 4
                file.errores.add(new ErrorASC(4,linea));
            }
        }
        
        else{ //Si la linea tiene más carácteres evaluaremos caso por caso para generar la instrucción correctamente
            parts = instruccion.split(" ");//Separamos la instrucción por espacio paraa identificar mnemónico-operandos          
            if(!file.readNemon(parts[0].toLowerCase())) //Si al separar las línea de instrucción se identifica que el mnemónico no existe, no se genera ningún dato
                file.errores.add(new ErrorASC(4,linea));
            else{
                if(mnemonicosREL(parts[0])){ //Este método revisa si el mnemónico corresponde a uno relativo, si lo es generamos el dato en este momento
                    this.mnemonico=parts[0].toLowerCase();
                    this.operandos.add(parts[1]);
                    this.direccionamiento="rel";
                    this.localidad=SetLocalidad(contador);//Se identifica su localidad
                    this.opcode=file.readOpcodes(this.mnemonico,this.direccionamiento);
                    this.contador+=2;//Se agregan sus respectivas casillas
                    return;
                }
                if(parts[0].equals("ORG")|| parts[0].equals("org")){ //Si lo primero que leemos es un org, inicializamos nuestra localidad de memoria de acuerdo al argumento que tenga el org
                    String nuevaLocalidad = parts[1].substring(1);
                    contador=hexadecimalADecimal(nuevaLocalidad);
                    return;
                }
                if(parts[0].equals("FCB")|| parts[0].equals("fcb")){ //Si lo primero que leemos es un org, inicializamos nuestra localidad de memoria de acuerdo al argumento que tenga el org
                    opers=parts[1].split(",");
                    this.operandos.add(opers[0]);
                    this.operandos.add(opers[1]);
                    this.mnemonico="Directiva FCB";
                    this.direccionamiento="Directiva ensamblador";
                    this.opcode="0";
                    this.localidad=SetLocalidad(contador);
                    this.contador+=2;
                    return;
                }
                if(parts[0].toLowerCase().equals("reset")){
                    opers=parts[2].split(",");
                    this.mnemonico="RESET";
                    this.direccionamiento="--";
                    this.operandos.add(opers[0]);
                    this.operandos.add(opers[1]);
                    this.opcode="--";
                    this.localidad=SetLocalidad(contador);
                    return;
                }
                if(parts.length == 2 && (parts[0].toLowerCase().equals("bclr") || parts[0].toLowerCase().equals("bset"))){//Si el mnemonico
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
                        if(parts[1].charAt(0)=='#'){
                            if(parts[1].charAt(1)=='$')
                                checker = TagorCons.matcher(parts[1].substring(0));
                            else
                                checker = TagorCons.matcher(parts[1].substring(1));
                        }
                        Boolean bandera=checker.find();
                        if(bandera){
                            if(file.constantesYvariables.containsKey(parts[1].substring(1)) || file.constantesYvariables.containsKey(parts[1])){
                                this.localidad = SetLocalidad(contador);
                                this.mnemonico=parts[0].toLowerCase();
                                if(parts[1].charAt(0)=='#'){
                                    valor = file.constantesYvariables.get(parts[1].substring(1));
                                    if(valor.substring(1, 3).equals("00"))
                                        valor="$"+valor.substring(3);
                                    if((valor.length()==3||valor.length()==5))
                                        this.direccionamiento="imm";
                                    else{
                                        file.errores.add(new ErrorASC(7,linea));
                                        return;
                                    }
                                }
                                else{
                                    valor = file.constantesYvariables.get(parts[1]);
                                    if(valor.substring(1, 3).equals("00")){
                                        valor="$"+valor.substring(3);
                                    }
                                    if((valor.length()==3))
                                        this.direccionamiento="dir";
                                    else
                                        if(valor.length()==5)
                                            this.direccionamiento="ext";
                                        else{
                                            file.errores.add(new ErrorASC(7,linea));
                                            return;
                                    }
                                }
                                if(valor.substring(1).length() == 2)
                                    this.contador += 1;
                                else
                                    this.contador += 2;
                                this.operandos.add(valor);
                                this.opcode=file.readOpcodes(this.mnemonico,this.direccionamiento);
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
                                    if(this.opcode.length()==4)
                                        this.contador+=2;
                                    else
                                        this.contador+=1;
                                    return;
                                }

                            }
                            else{
                                file.errores.add(new ErrorASC(1,linea));
                                this.operandos.add(null);
                            }
                            if(operandos.get(0)==null)
                               return;
                        }                    
                        else{
                            this.localidad = SetLocalidad(contador);
                            if(parts[1].contains(",")){
                                ind=parts[1].substring(0, 3);
                                this.contador+=1;
                                this.operandos.add(parts[1]);
                            }
                            else{
                                if(parts[1].substring(1).length() == 2 || parts[1].substring(2).length()== 2 )
                                    this.contador+=1;
                                else
                                    this.contador+=2;
                                this.operandos.add(parts[1]);  
                            }
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
                this.direccionamiento = SetDireccionamiento(this.operandos.get(0));
                this.opcode=file.readOpcodes(this.mnemonico,this.direccionamiento);
                if(this.opcode==null){
                    file.errores.add(new ErrorASC(7,linea));
                    return;
                }    
                if(this.opcode.length()==4)
                    this.contador+=2;
                else
                    this.contador+=1;
                if(operandos.get(0).toLowerCase().contains(",y")||operandos.get(0).toLowerCase().contains(",x")){
                    this.nuevo =this.operandos.get(0).substring(0,3);
                    this.operandos.remove(0);
                    this.operandos.add(this.nuevo);
                }    
            }
        }
   }
    
    String SetDireccionamiento(String operandos){
        String direccion;
        ArchivoRegex regex = new ArchivoRegex();
        direccion=regex.matcher(this.mnemonico,operandos,this.direccionamiento);
        return direccion;
    }
    
    public void ImprimirDatos(){
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("Numero de linea: "+this.numLinea);
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
