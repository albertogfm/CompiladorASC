package compiladorasc;

import regex.*;
import filemanagment.*;
import java.util.*;
import java.util.regex.*;
import errores.*;
/**
 *
 * @author alber
 */
public class CompiladorASC {//Con esta clase hacemos la compilación del programa
    //Se declaran todos los datos que utilizarémos 
    static FileMan fileASC = new FileMan();
    public Queue <String> etiqueta = new LinkedList<>();
    public static ArrayList <String> etiquetas = new ArrayList<>();
    public static ArrayList <String> compilacion = new ArrayList<>();
    public static ArrayList <String> compilacionLST = new ArrayList<>();
    public static ArrayList <Datos> datos2 = new ArrayList<>();
    public ArrayList <String> relativos = new ArrayList<>();
    public static Queue <Datos> saltos = new LinkedList<>();
    public static ArrayList <Datos> lst = new ArrayList <>();
     
    
    public void Compilador(FileMan file){//Recibe el archivo para leerlo y tratar errores 
        ArrayList <String> lineasArc = file.lineasArchivoASC;
        boolean endNotExist = true, repetir2=true;
        int i;
        checkIfMarginCorrect(file);
        for(i=0;i<lineasArc.size();i++){//Con este ciclo, leeremos todas las lineas del archivo.
            String linea = lineasArc.get(i);
            endNotExist = select(linea,i);//Cuando se detecte un end en el archivo, dejaremos de leer.
            if(endNotExist==false)
                break;
        }
        if(endNotExist)
            file.errores.add(new ErrorASC(10,lineasArc.size()));//Si nunca se leyo un end en el archivo, entonces se genera dicho error.
        //prePass(file);  PRUEBAS
        if(file.errores.isEmpty()){//Si a lo largo de la lectura del archivo nunca se detectaron errores, entonces se procede a generar el archivo LST y S19.
            firstCheck(fileASC.instrucciones,file);
            SecondCheck(file);
            //imprimirArray(); PRUEBAS
            if(etiquetas.size()!=0)
                initEtiq();
            //imprimirArrayLST(); PRUEBAS
            //imprimirEtiquetas(); PRUEBAS 
            file.opCodesFile = this.compilacion;
        }
        else{//Si se detecto por lo menos un error, no se genera el archivo LST ni el S19.
            System.out.println("Se detectaron "+file.errores.size()+" errores :C");
            for(int j=0 ; j< file.errores.size(); j++){
                System.out.println(file.errores.get(j).toString());
            }
            
        }
    }
    //Se tratan los comentarios, espacios en blancos y datos que puedan estar en la línea de la instrucción 
    public boolean select (String linea, int numLinea){
    //Expresiones regulares para identificar comentarios y espacios en blanco    
        Datos dato;
        int caso;
        Validador checker = new Validador();
        Pattern comentariosUnicamente = Pattern.compile("^(( )*(\\*)[a-zA-Z0-9\\*_,( )]*)$");
        Matcher onlycomentario = comentariosUnicamente.matcher(linea);
        Pattern comentarios = Pattern.compile("(()(\\*)+[A-Za-z0-9_]*)");
        Matcher comentario = comentarios.matcher(linea);
        Pattern espaciosBlanco = Pattern.compile("^( )*$");
        Matcher espacios = espaciosBlanco.matcher(linea);

        if(espacios.find() || onlycomentario.find()){ //Ignora los espacios en blanco
            return true;}

        if(comentario.find()){//Ignora el comentario y se queda con la linea de instrucciones
            String[] parts = linea.split("\\*");
            linea=parts[0];
        }
        caso = checker.Reconoce(linea,numLinea);//Solo linea 
        switch(caso){
            case 1://Constante y variable
                linea=deleteSpacesIntermedium(linea);//Elimina espacios intermedios
                String[] parts = linea.split(" ");
                if(parts[1].equals("EQU")){
                    fileASC.constantesYvariables.put(parts[0],parts[2]);
                    FileMan.poolOfConstAndVar.add(parts[0]);
                }
                break;
            case 2://Instrucción 
                while(linea.startsWith(" ")){ //Verifica si al principio de la línea de instrucción hay espacios en blanco 
                    linea=linea.substring(1);
                }
                while(linea.endsWith(" ")){//Verifica si al final de la línea de instrucción hay espacios en blanco 
                    linea=linea.substring(0,linea.length()-1);
                }
                //Imprimirá los datos encontrados
                linea=deleteSpacesIntermedium(linea);
                dato = new Datos(linea,etiqueta,etiquetas,numLinea);//Se crea el dato
                    if(dato.opcode!=null){
                        dato.ImprimirDatos();
                        fileASC.instrucciones.add(dato);
                        datos2.add(dato);
                    }
                break;
            case 3://Etiquetas
                while(linea.endsWith(" ")){
                    linea=linea.substring(0,linea.length()-1);
                }
                etiqueta.add(linea);
                //etiquetas.add(linea); PRUEBAS
                break;
            case 4://Fin 
                FileMan.endInLine = numLinea;
                return false;
            case 6://Reset
                while(linea.startsWith(" ")){
                    linea=linea.substring(1);
                }
                while(linea.endsWith(" ")){
                    linea=linea.substring(0,linea.length()-1);
                }
                //Imprimirá los datos encontrados
                linea=deleteSpacesIntermedium(linea);
                dato = new Datos(linea,etiqueta,etiquetas,numLinea);
                    if(dato.opcode!=null){
                        dato.ImprimirDatos();
                        fileASC.instrucciones.add(dato);
                        datos2.add(dato);
                    }
        }
        return true;
    }
    //Se realiza la primera pasada en la compilación 
    public void firstCheck(Queue <Datos> datos, FileMan file){ //Agregar elementos a la pila
        int i,j,u=0;
        boolean rep= true;
        while(rep){
            u+=1;           
            Datos element = datos.poll(); //Desencolamos
            if(datos.peek()==null)
                rep=false;
            String buscador;
            Pattern TagorCons = Pattern.compile("^([A-Za-z]+)([0-9]*)");//Verificar si el operando es un valor numérico o una etiqueta
            if(element.mnemonico.equals("Directiva FCB")||element.mnemonico.equals("RESET")){}//Identifica si es una directiva FCB o un RESET
            else{
            if(element.opcode.length() == 2){ // Se tratan los tamaños de los opcode que pueda tener el mnemónico y se agregan al array
                compilacion.add(element.opcode.toUpperCase());
                compilacionLST.add("-"+element.opcode.toUpperCase()+"-");
            }
                if(element.opcode.length() == 4){
                compilacion.add(element.opcode.substring(0,2).toUpperCase());
                compilacion.add(element.opcode.substring(2).toUpperCase());
                compilacionLST.add("-"+element.opcode.substring(0,2).toUpperCase());
                compilacionLST.add(element.opcode.substring(2).toUpperCase()+"-");
                }
            }
            int limite=element.operandos.size();
            for(i=0;i<limite;i++){ //Se tratan los operandos y se agregan al array
                if(element.mnemonico.equals("Directiva FCB")){//Tratamiento de compilación para la directiva FCB
                    compilacion.add(element.operandos.get(0).substring(1).toUpperCase());
                    compilacion.add(element.operandos.get(1).substring(1).toUpperCase());
                    compilacionLST.add(element.operandos.get(0).substring(1).toUpperCase());
                    compilacionLST.add(element.operandos.get(1).substring(1).toUpperCase());
                    break;
                }    
                Matcher checker = TagorCons.matcher(element.operandos.get(i));//Consultar si es una etiqueta, constante o variable
                if(element.operandos.get(i).equals(" "))
                    break;
                if(checker.find()){//Identifica que el operando es una etiqueta
                    if(mnemonicosREL2(element.mnemonico)){
                        compilacion.add("--");//Se deja el espacio en blanco
                        compilacionLST.add("--");
                        saltos.add(element);//Agregar dicha instrucción para realizar el salto en la segunda pasada.
                        break;
                    }
                    if(etiquetas.contains(element.operandos.get(i))){ //Si es una etiqueta buscar su localidad
                        for(j=0;j<datos2.size();j++){
                            buscador=element.operandos.get(i);
                            if(datos2.get(j).etiqueta!=null){
                                if(datos2.get(j).etiqueta.equals(buscador)){//Cuando encuentra la localidad de la etiqueta, se guarda y compila.
                                    compilacion.add(datos2.get(j).localidad.substring(0,2).toUpperCase());//Se agrega la localidad en la tabla de compilacion
                                    compilacion.add(datos2.get(j).localidad.substring(2).toUpperCase());
                                    compilacionLST.add(datos2.get(j).localidad.substring(0,2).toUpperCase());
                                    compilacionLST.add(datos2.get(j).localidad.substring(2).toUpperCase());
                                    break;
                                }
                            }     
                        }
                        
                    }
                    if(fileASC.constantesYvariables.containsKey(element.operandos.get(i))){//Si es una constante o varible, buscamos el valor para compilarlo. 
                        buscador=fileASC.constantesYvariables.get(element.operandos.get(i));
                            //System.out.println(fileASC.constantesYvariables.get(element.operandos.get(i))); PRUEBA
                            if(buscador.length() <= 2){
                                compilacion.add(buscador.toUpperCase());
                                compilacionLST.add(buscador.toUpperCase());
                            }
                            else{
                                compilacion.add(buscador.substring(0,2).toUpperCase());
                                compilacion.add(buscador.substring(2,4).toUpperCase());
                                compilacionLST.add(buscador.substring(0,2).toUpperCase());
                                compilacionLST.add(buscador.substring(2,4).toUpperCase());
                            }
                    }
                }
                else{
                    Convertidor(element.operandos.get(i));//Si no es ni etiqueta, ni varible ni constante entonces es un valor numerico que convertiremos a hexadecimal en caso de que sea décimal.
                }
            }
        }
    }

    public void Convertidor(String numero){ //Checa si el operando es hexadecimal, si no la convierte y los agrega al array
        String nuevo, convertido;
        int convertidor;      
        //¿Qué caso es?                                      
        if(numero.charAt(0)=='$' || numero.substring(0, 2).equals("#$")){//Si contiene el simbolo $ entonces solo se compila debido a que es hexadecimal.
            if(numero.charAt(0)=='$'){
                nuevo = numero.substring(1);
                if(nuevo.length()==1 || nuevo.length()==3)
                    nuevo='0'+nuevo;
                if(nuevo.length() == 2){
                    compilacion.add(nuevo.toUpperCase());//Opcode de 8 bits
                    compilacionLST.add(nuevo.toUpperCase());
                    return;
                }
                else{
                    compilacion.add(nuevo.substring(0,2).toUpperCase());//Opcode 16 bits
                    compilacion.add(nuevo.substring(2).toUpperCase());
                    compilacionLST.add(nuevo.substring(0,2).toUpperCase());
                    compilacionLST.add(nuevo.substring(2).toUpperCase());                 
                    return;
                }    
            }
        }
        if(numero.substring(0, 2).equals ("#$")){
            nuevo = numero.substring(2);
            if(nuevo.length()==1 || nuevo.length()==3)
                    nuevo='0'+nuevo;
            if(nuevo.length() == 2){//Opcode de 8 bits
                compilacion.add(nuevo.toUpperCase());
                compilacionLST.add(nuevo.toUpperCase());
                return;
            }
            else{//Opcode de 16 bits
                compilacion.add(nuevo.substring(0,2).toUpperCase());
                compilacion.add(nuevo.substring(2).toUpperCase());
                compilacionLST.add(nuevo.substring(0,2).toUpperCase());
                compilacionLST.add(nuevo.substring(2).toUpperCase());
                return;
            }    
        }
        //Si no contiene el simbolo $ convertimos el número decimal a hexadecimal.   
        else{
            if(numero.charAt(0)=='#')
                numero=numero.substring(1);
            convertidor = Integer.parseInt(numero);
            convertido = Integer.toHexString(convertidor);
            if(convertido.length()==1 || convertido.length()==3)
                convertido= '0'+convertido;
            if(convertido.length() <=2 ){//Opcode de 8 bits
                compilacion.add(convertido.toUpperCase());
                compilacionLST.add(convertido.toUpperCase());
            }    
            else{//Opcode de 16 bits
                compilacion.add(convertido.substring(0,2).toUpperCase());
                compilacion.add(convertido.substring(2).toUpperCase()); 
                compilacionLST.add(convertido.substring(0,2).toUpperCase());
                compilacionLST.add(convertido.substring(2).toUpperCase());
            }    
        }
    }


    public boolean mnemonicosREL2(String instruccion){//Este método identifica los mnemonicos que pueden tener salto a otra localidad, es decir todos los mnemonicos relativos
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
        this.relativos.add("brclr");
        this.relativos.add("brset");
        if(this.relativos.contains(instruccion.toLowerCase()))
            return true;
        return false;
    }
    //PRUEBAS
    public void imprimirArray(){//Imprime en el S19
        for(int i=0;i<compilacion.size();i++)
            System.out.println(compilacion.get(i));
    }
    
    public void imprimirArrayLST(){//Imprime en el LST
        for(int i=0;i<compilacionLST.size();i++)
            System.out.println(compilacionLST.get(i));
    }

    public void imprimirEtiquetas(){//Imprime etiquetas
        for(int i=0;i<etiquetas.size();i++)
            System.out.println(etiquetas.get(i));
    }
    

    public void SecondCheck(FileMan file){//Segunda pasada, en donde haremos el calculo del salto
        int i, dif1,dif2,newBin,decimal=0,n=0,j,x, index=0,nuevaLoc; 
        String localidad=" ",hex;
        boolean rep = true;
        int complementoa2;
        while(rep){
            if(saltos.peek()==null){
                break;
            }
            Datos element = saltos.poll(); //Desencolamos las instrucciones que tengan salto
            for(i=0; i<element.operandos.size(); i++){
                String etiqueta = element.operandos.get(i);
                if(etiquetas.contains(etiqueta)){ //Identifica si la etiqueta existe en nuestra lista
                    for(j=0;j<datos2.size();j++){
                        if(datos2.get(j).etiqueta!=null)
                            if(datos2.get(j).etiqueta.equals(etiqueta)){
                                localidad=datos2.get(j).localidad;//Obtenemos la localidad donde haremos el salto
                            }   
                    }
                } 
            }
            if((Integer.parseInt(element.localidad,16)>Integer.parseInt(localidad,16))||(Integer.parseInt(element.localidad,16)==Integer.parseInt(localidad,16))){//Salto negativo
                dif1=Integer.parseInt(element.localidad,16);//Convertimos a décimal la localidad de la instruccion para realizar la operación de resta
                if(element.operandos.size()==1)
                    dif1+=1;
                else
                    dif1+=3;
                dif2=Integer.parseInt(localidad,16);//Convertimos a décimal la localidad donde se encuentra la etiqueta
                int diferenciaNegativa=(dif2-dif1)-1;//Diferencia de localidades
                if(diferenciaNegativa==-127){
                    file.errores.add(new ErrorASC(8,element.numLinea));//Si la diferencia es menor a -127, generamos el error.  
                }
                else{//Calculo del complemento a2
                    diferenciaNegativa=-1*diferenciaNegativa;
                    String binario = Long.toBinaryString(diferenciaNegativa);
                    while(binario.length()<8){
                        binario = '0'+binario;   
                    }
                    newBin = Integer.parseInt(twosCompliment(binario));
                    decimal=binarioDec(newBin);
                    hex=Integer.toHexString(decimal);
                    for(x=0;x<compilacion.size();x++){
                        if(compilacion.get(x).equals("--")){//En nuestra lista de compilación buscamos las celdas que esten vacias para insertar el valor.
                            compilacion.set(x,hex.toUpperCase());
                            compilacionLST.set(x,hex.toUpperCase());
                            break;
                        }
                    }
                }
            }     

            else{//Salto positivo
                dif1=Integer.parseInt(element.localidad,16);//Convertimos a décimal la localidad para realizar la operación aritmética
                dif2=Integer.parseInt(localidad,16);
                if(element.operandos.size()==1)
                    if(element.opcode.length()==2)
                        dif1+=1;
                    else
                        dif1+=2;
                else
                    if(element.opcode.length()==2)
                        dif1+=3;
                    else
                        dif1+=4;
                int diferenciaPositiva=(dif2-dif1)-1;
                if(diferenciaPositiva==128){//Si la diferencia es mayor a 128, generamos el error.  
                    file.errores.add(new ErrorASC(8,element.numLinea));
                }
                else{
                    String result2=Integer.toHexString(diferenciaPositiva);
                    nuevaLoc = dif1+diferenciaPositiva;
                    result2=Integer.toHexString(diferenciaPositiva);
                    if(result2.length()==1)
                        result2='0'+result2;
                    for(x=0;x<compilacion.size();x++){
                        if(compilacion.get(x).equals("--")){
                            compilacion.set(x,result2.toUpperCase());
                            compilacionLST.set(x,result2.toUpperCase());
                            break;
                        }
                    }
                }
                
            }
        }
    }

    public String twosCompliment(String bin) {//Método que realiza el complemento a2 de nuestro numero binario.
        int suma1, suma2=1,total;
        StringBuilder bina = new StringBuilder(bin);
        for(int i=0;i<bin.length();i++){
            if(bin.charAt(i)=='1')
                bina.setCharAt(i,'0');
            else
                bina.setCharAt(i,'1');
        }
        bin = bina.toString();
        suma1=Integer.parseInt(bin,2);
        total=suma1+suma2;
        return Long.toBinaryString(total);
    }

    public int binarioDec(int binario){//Método que realiza la conversión de binario a decimal
       int resto, decimal=0, i=0;
       while (binario != 0){
           resto = binario % 10;
           decimal = decimal + (resto * (int) Math.pow(2, i));
           i++;
           binario = binario / 10;
       }
       return decimal;
    }

    public void checkIfMarginCorrect(FileMan file){//Con este método le damos formato a nuestras lineas del archivo para procesarlas corectamente
        Pattern Textoespaciado = Pattern.compile("^(( )+[a-zA-Z0-9(\\$#)?(’')( ),_]*)$");
        Pattern espaciosBlanco = Pattern.compile("^\\s*$");
        Pattern comentarios = Pattern.compile("(()(\\*)+[A-Za-z0-9_]*)");
        Pattern comentariosUnicamente = Pattern.compile("^(( )*(\\*)[a-zA-Z0-9\\*_,( )]*)$");
        for(int i = 0 ; i< file.lineasArchivoASC.size();i++){
            String linea=file.lineasArchivoASC.get(i);
            Matcher comentario = comentarios.matcher(linea);
            Matcher onlycomment = comentariosUnicamente.matcher(linea);
            Boolean check3=comentario.find();
            Boolean check4=onlycomment.find();
            if(check4)
                continue;
            if(check3){//Ignora el comentario y se queda con la linea de instrucciones
                String[] parts = linea.split("\\*");
                linea=parts[0];
                linea = deleteSpacesIntermedium(linea);
            }
            Matcher checker2= espaciosBlanco.matcher(linea);
            Boolean check2= checker2.find();
            if(!check2){//Entra en caso de que la linea no sea una linea vacia
                linea = deleteSpacesIntermedium(linea);
                Matcher checker= Textoespaciado.matcher(linea);
                Boolean check1= checker.find();
                if(!check1){//Si la linea no tiene espacio al principio entra, ya que nos interesa guardar las variables, constantes y variables
                    if(linea.contains(" ")){//Si la linea contiene espacios intermedios siginifica que es la declaración de una variable o constante.
                        String [] fragmentarlinea= linea.split(" ");
                        if(fragmentarlinea[1].equals("EQU") || fragmentarlinea[1].equals("equ")||(fragmentarlinea[0].toLowerCase().equals("reset")&&fragmentarlinea[1].toLowerCase().equals("fcb"))){
                        }
                        else{
                            file.errores.add(new ErrorASC(9,i));//Error de margen
                        }        
                    }
                    else{//Si no contiene espacios intermedios, entonces es la declaración de una etiqueta.
                        Matcher com=comentariosUnicamente.matcher(linea);
                        Boolean ban = com.find();
                        if(!ban){
                            etiquetas.add(linea);
                        }
                    }    
                }            
            } 
        }
    }    
    public String deleteSpacesIntermedium(String linea){//Elimina los espacios que existan entre el principio y final de linea
        int sub;
        if(linea.charAt(linea.length()-1) == ' '){
            String lineaF = " ";
            for (int i=linea.length(); i > 0 ; i--){
                if(linea.charAt(i-1) == ' '){
                    lineaF = linea.substring(0, i-1);
                }else{
                    break;
                }
            linea = lineaF; 
            }
        }    
 
        
        ArrayList<Character> lineaFinal = new ArrayList<>(); 
        for (int i=0 ; i < linea.length() ; i ++){
            if (linea.charAt(i) == (' ')){
                if(!(linea.charAt(i+1) == (' '))){
                    lineaFinal.add(linea.charAt(i));
                }
            }else{
                lineaFinal.add(linea.charAt(i));
            }  
        }
        StringBuilder builder = new StringBuilder(lineaFinal.size());
        for(Character ch: lineaFinal){
            builder.append(ch);
        }
        linea =builder.toString();
        
        return linea;
    }
    public void initEtiq(){//Inicializa la lista de etiquetas con sus respectivas localidades
        Datos dato=null;
        for(int i=0;i<datos2.size();i++){
            if(datos2.get(i).etiqueta!=null){
                dato=datos2.get(i);
                System.out.println(dato.etiqueta+" "+dato.localidad);
                fileASC.EtiquetaLocalidad.put(dato.etiqueta,dato.localidad);
            }
        }
    
    }
    public static void resetStaticValues(){//Reinciciar todo el proceso.
        CompiladorASC.compilacion.clear();
        CompiladorASC.compilacionLST.clear();
        CompiladorASC.datos2.clear();
        CompiladorASC.saltos.clear();
        CompiladorASC.lst.clear();
        CompiladorASC.etiquetas.clear();
        CompiladorASC.fileASC =  new FileMan();
        
        
    }
    
}
    
    


      
    
    

