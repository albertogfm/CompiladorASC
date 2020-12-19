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
        String k = "", G="";
        Pattern TagorCons = Pattern.compile("^(([A-Za-z_]+[0-9]*))$"); //Esta expresión regular nos ayudará a revisar si los operandos son identificadores de constantes,variables,etiquetas o si se trata de algun valor numerico en hexadecimal o decimal.            
        Matcher checker;
        String ind;
        int ascii;
        char character;
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
                if(this.opcode.equals("--")){
                    file.errores.add(new ErrorASC(5,linea));
                    return;
                }    
                if(this.opcode.length()==4)//Si el opcode tiene 16 bits, le sumamos 2 localidades de memoria en caso contrario solo le agragamos 1 localidad
                    this.contador+=2;
                else
                    this.contador+=1;
            }
            else{//Si el mnemonico no es valido se generará el error tipo 4
                file.errores.add(new ErrorASC(4,linea-1));
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
                    FileMan.firstOrg.add(linea);
                    String nuevaLocalidad = parts[1].substring(1);
                    contador=hexadecimalADecimal(nuevaLocalidad);
                    return;
                }
                if(parts[0].equals("FCB")|| parts[0].equals("fcb")){ //Si se detecta que la linea es la directiva FCB, creamos el dato y compilamos los operandos que tenga.
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
                if(parts[0].toLowerCase().equals("reset")){ //Si se detecta que la linea es un RESET, agarramos los operandos que tenga y reiniciamos el programa.
                    opers=parts[2].split(",");
                    this.mnemonico="RESET";
                    this.direccionamiento="--";
                    this.operandos.add(opers[0]);
                    this.operandos.add(opers[1]);
                    this.opcode="--";
                    this.localidad=SetLocalidad(contador);
                    return;
                }
                if(parts.length == 2 && (parts[0].toLowerCase().equals("bclr") || parts[0].toLowerCase().equals("bset"))){//Si el mnemonico es una instrucción excepcional bclr, bset las tratamos debido a que se deben de tratar de manera extraordinaria.                    
                    if(etiqueta.peek()!=null)
                        this.etiqueta=etiqueta.poll();
                    this.mnemonico=parts[0].toLowerCase();
                    this.localidad=SetLocalidad(contador);
                    opers = parts[1].split(",");//Separamos los operandos para identificar el valor de los mismos.

                    if(opers.length==2){ 
                        this.operandos.add(opers[0]);
                        this.operandos.add(opers[1]);
                        this.contador+=2;
                    }

                    else{
                        this.operandos.add(opers[0]);
                        this.operandos.add(opers[2]);
                        this.contador+=2;
                        if(opers[1].charAt(0) == 'x' || opers[1].charAt(0) == 'X' ) //Si la instrucción tiene modo de direccionamiento indexado a X o Y, lo identificamos y le damos su opcode correspondiente.
                            this.direccionamiento="indx";
                        else
                            this.direccionamiento="indy";
                    }   
                }

                else{   
                    if(parts.length==2){ // En otro caso que no sean las instrucciones bcrl, bset crearemos el dato dependiendo de los parametros con los que cuente.
                        if(etiqueta.peek()!=null)
                            this.etiqueta=etiqueta.poll();
                        if(mnemonicosREL(parts[0].toLowerCase())){//Si la instrucción es relativa marcamos con el método mnemonicosREL que esta instrucción tendra un salto.
                            this.operandos.add(parts[1]);
                            this.localidad = SetLocalidad(contador);   
                            this.contador+=1;
                        }
                        checker = TagorCons.matcher(parts[1]);//la variable checker nos ayudara a verificar si el operando de las instrucción se trata de una constante, variable o una etiqueta. 
                        if(parts[1].charAt(0)=='#'){//Si la constante o variable tienen un #, significa que su modo de direccionamiento es imm, si despues del # se encuentra un $ entonces los operandos de la instrucción no pueden ser una etiqueta, variable o constante.
                            if(parts[1].charAt(1)=='$')
                                checker = TagorCons.matcher(parts[1].substring(0));
                            else
                                checker = TagorCons.matcher(parts[1].substring(1));
                        }
                        Boolean bandera=checker.find();
                        if(bandera){//Si la bandera se pone en true, entonces los operandos de la instrucción son etiquetas, variables o constantes.
                            if(file.constantesYvariables.containsKey(parts[1].substring(1)) || file.constantesYvariables.containsKey(parts[1])){//Si el operando es una variable o constante, buscaremos el valor al que esta asociado.
                                this.localidad = SetLocalidad(contador);
                                this.mnemonico=parts[0].toLowerCase();
                                if(parts[1].charAt(0)=='#'){ //Si la variable, constante empieza con # entonces su modo de direccionamiento sera imm.
                                    valor = file.constantesYvariables.get(parts[1].substring(1)); //La variable valor guardara el valor de la variable o constante que hayamos guardado. 
                                    if(valor.substring(1, 3).equals("00")){ //Si el valor tiene doble 00, y sabemos que la instruccion tiene ademas de modo de direccionamiento extendido el modo de direccionamiento directo, entonces lo removemos para optimizarlo.
                                        if(parts[0].toLowerCase().equals("ldx")){}
                                        else    
                                            valor="$"+valor.substring(3);
                                    }
                                    if((valor.length()==3||valor.length()==5))
                                        this.direccionamiento="imm";
                                    else{
                                        file.errores.add(new ErrorASC(7,linea));
                                        return;
                                    }
                                }
                                else{//Si la variable o constante no tiene #, entonces buscaremos el modo de direccionamiento.
                                    valor = file.constantesYvariables.get(parts[1]);
                                    if(valor.substring(1, 3).equals("00")){//Nuevamente, si el valor tiene doble 00, y sabemos que la instruccion tiene ademas de modo de direccionamiento extendido el modo de direccionamiento directo, entonces lo removemos para optimizarlo.
                                        if(checkDIR(parts[0])){
                                            valor="$"+valor.substring(3);
                                        }
                                    }
                                    if((valor.length()==3))//Si al final le quitamos los 00, su modo de direccionamiento es dir.
                                        this.direccionamiento="dir";
                                    else//Si no le quitamos los 00, entonces su modo de direccionamiento es ext.
                                        if(valor.length()==5)
                                            this.direccionamiento="ext";
                                        else{
                                            file.errores.add(new ErrorASC(7,linea));//Si la magnitud es mayor a 4, entonces hay un error de magnitud en el operando.
                                            return;
                                    }
                                }
                                if(valor.substring(1).length() == 2) //Si el operando es de 8 bits entonces sumamos una celda de memoria, si es de 16, 2 celdas de memoria.
                                    this.contador += 1;
                                else
                                    this.contador += 2;
                                this.operandos.add(valor);// Guardamos el valor de la constante o variable.
                                this.opcode=file.readOpcodes(this.mnemonico,this.direccionamiento);//Obtenemos el opcode de la instrucción de acuerdo al direccionamiento que hayamos definido.
                                if(this.opcode.length()==4)
                                    this.contador+=2;
                                else
                                    this.contador+=1;  
                                return;
                            }                            
                            if(etiquetas.contains(parts[1])){//Si el operando se trata de una etiqueta, buscaremos el valor de la localidad en la cual fue declarada.
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
                                file.errores.add(new ErrorASC(1,linea));// En caso de que el operando sea una variable, constante o etiqueta inexistente se genera dicho error.
                                this.operandos.add(null);
                            }
                            if(operandos.get(0)==null)
                               return;
                        }                    
                        else{//Si el operando no es una etiqueta, constante o variable entonces buscaremos el direccionamiento con el valor numerico que tenga como operando.
                            this.localidad = SetLocalidad(contador);
                            if(parts[1].contains("’")||parts[1].contains("'")){//Si el operando empieza con una comilla, entonces esto nos indica que buscaremos el codigo ascii del caracter que tenga despues de la comilla.
                                if(parts[1].charAt(0)=='#'){
                                    character = parts[1].charAt(2);
                                    ascii = (int)character;
                                    this.direccionamiento="imm";
                                    this.mnemonico=parts[0].toLowerCase();
                                    this.opcode=file.readOpcodes(parts[0].toLowerCase(),"imm");
                                    this.contador+=2;
                                    this.operandos.add(String.valueOf(ascii));
                                    return;
                                }    
                                else{
                                    character = parts[1].charAt(1);
                                    ascii=(int)character;
                                    this.direccionamiento="dir";
                                    this.mnemonico=parts[0].toLowerCase();
                                    this.opcode=file.readOpcodes(parts[0].toLowerCase(),"dir");
                                    this.contador+=1;
                                    this.operandos.add(String.valueOf(ascii));
                                    return;
                                }
                            }
                            if(parts[1].contains(",")){//Si el operando tiene una x o y porque su modo de direccionamiento es indexado, entonces solo nos quedamos con el valor que esta antes de la ,
                                ind=parts[1].substring(0, 3);
                                this.contador+=1;
                                this.operandos.add(parts[1]);
                            }
                            else{
                                if(parts[0].toLowerCase().equals("ldx")){//Tratamiento especial con ldx
                                    if(parts[1].charAt(0)=='#')
                                        if(parts[1].charAt(1)=='$'){
                                            k = parts[1].substring(2);
                                            while(k.length()<4){
                                                k='0'+k;
                                            }
                                            parts[1]="#$"+k;
                                        }
                                    
                                }
                                if(parts[1].substring(1).length() == 2 || parts[1].substring(2).length()== 2 )//Si el operando tiene 8 o 16 bits, agregamos 1 o 2 celdas respectivamente.
                                    this.contador+=1;
                                else{
                                    this.contador+=2;
                                    
                                }    
                                this.operandos.add(parts[1]);  
                            }
                        }
                    }    
                    this.mnemonico=parts[0].toLowerCase();//Asignamos el nombre del mnemonico.     
                }
                if(parts.length==3){//Cuando se splitean 3 espacios (operandos) en la linea de instrucción entonces se trata de las instrucciones de excepcion brclr o brset las cuales inicializamos de manera especial.
                    if(etiqueta.peek()!=null)
                        this.etiqueta=etiqueta.poll();
                        this.mnemonico=parts[0].toLowerCase();
                        this.localidad = SetLocalidad(contador);
                        opers=parts[1].split(",");// Separamos sus operandos por la , y guardamos.
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
                            if(opers[1].charAt(0) == 'x' || opers[1].charAt(0) == 'X') //Si la instrucción contiene una x o una y, incializamos su modo de direccionamiento.
                                this.direccionamiento="indx";
                            else
                                this.direccionamiento="indy";
                        } 
                }      
                this.direccionamiento = SetDireccionamiento(this.operandos.get(0));//Al final con los operandos que tengamos entonces determinamos el modo de direccionamiento a aquellas instrucciones que no la tengan inicializado todavia.
                this.opcode=file.readOpcodes(this.mnemonico,this.direccionamiento);
                if(this.opcode==null){
                    file.errores.add(new ErrorASC(7,linea));//Si no encontramos ningun opcode en nuestro excel, entonces la magnitud del operando es incorrecta
                    return;
                }    
                if(this.opcode.length()==4)//Si el opcode que encontramos es de 16 bits, sumamos 2 celdas, si es de 8 entonces solo 1 celda.
                    this.contador+=2;
                else
                    this.contador+=1;
                if(operandos.get(0).toLowerCase().contains(",y")||operandos.get(0).toLowerCase().contains(",x")){//Tratamiento especial para las instrucciones con modo de direccionamiento indexado.
                    this.nuevo =this.operandos.get(0).substring(0,3);
                    this.operandos.remove(0);
                    this.operandos.add(this.nuevo);
                }    
            }
        }
   }
    
    String SetDireccionamiento(String operandos){//Con este método obtenemos el direccionamiento de acuerdo a la magnitud de los operandos y del mnamonico.
        String direccion;
        ArchivoRegex regex = new ArchivoRegex();
        direccion=regex.matcher(this.mnemonico,operandos,this.direccionamiento);
        return direccion;
    }
    
    public void ImprimirDatos(){//Con este método, se imprimen en pantalla los atributos del dato, como lo son su mnemonico, opcode, operandos, localidad etc.
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
            System.out.println("Etiqueta:"+this.etiqueta); 
    }
    public String SetLocalidad(int localidad){//Con este método, convertimos la localidad que esta en decimal a hexadecimal.
        String localHex;
        localHex=Integer.toHexString(localidad);
        return localHex;
    }
    public static int hexadecimalADecimal(String hexadecimal) {//El método nos permite convertir de hexadecimal a decimal
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
    public static int caracterHexadecimalADecimal(char caracter) {//Este método es complemento del método hexadecimalADecimal
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
    public boolean mnemonicosREL(String instruccion){//Este método identifica si la instrucción se trata de una que tiene modo de direccionamiento relativo.
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
    public boolean checkDIR(String mnemon){//Con este método optimizamos el modo de direccionamiento de la instrucción.
        if(!file.readOpcodes(mnemon.toLowerCase(),"ext").equals("-- "))
            if(!file.readOpcodes(mnemon.toLowerCase(),"dir").equals("-- "))
                return true;
        return false;
    }
    
}
