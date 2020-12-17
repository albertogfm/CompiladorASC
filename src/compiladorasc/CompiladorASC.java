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
public class CompiladorASC {
    static FileMan fileASC = new FileMan();
    public Queue <String> etiqueta = new LinkedList<>();
    public static ArrayList <String> etiquetas = new ArrayList<>();//Etiquetas declaradas
    public static ArrayList <String> compilacion = new ArrayList<>();
    public static ArrayList <String> compilacionLST = new ArrayList<>();
    public static ArrayList <Datos> datos2 = new ArrayList<>();
    public ArrayList <String> relativos = new ArrayList<>();
    public static Queue <Datos> saltos = new LinkedList<>();
    public static ArrayList <Datos> lst = new ArrayList <>();
     
    
    public void Compilador(FileMan file){//Recibe un FileMan
        ArrayList <String> lineasArc = file.lineasArchivoASC;
        boolean endNotExist = true, repetir2=true;
        int i;
        checkIfMarginCorrect(file);
        for(i=0;i<lineasArc.size();i++){
            String linea = lineasArc.get(i);
            endNotExist = select(linea,i);
            if(endNotExist==false)
                break;
        }
        if(endNotExist)
            file.errores.add(new ErrorASC(10,lineasArc.size()));
        //prePass(file);    
        if(file.errores.isEmpty()){     
            firstCheck(fileASC.instrucciones,file);
            SecondCheck(file);
            imprimirArray();
            if(etiquetas.size()!=0)
                initEtiq();
            //imprimirArrayLST();
            //imprimirEtiquetas();
            file.opCodesFile = this.compilacion;
        }
        else{
            System.out.println("Se detectaron "+file.errores.size()+" errores :C");
            for(int j=0 ; j< file.errores.size(); j++){
                System.out.println(file.errores.get(j).toString());
            }
            
        }
    }
    
    public boolean select (String linea, int numLinea){
        Datos dato;
        int caso;
        Validador checker = new Validador();
        Pattern comentariosUnicamente = Pattern.compile("^(( )*(\\*)[a-zA-Z0-9\\*_,( )]*)$");
        Matcher onlycomentario = comentariosUnicamente.matcher(linea);
        Pattern comentarios = Pattern.compile("(()(\\*)+[A-Za-z0-9_]*)");//Matcher y Patern de Comentarios y espacios en blanco
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
                linea=deleteSpacesIntermedium(linea);
                String[] parts = linea.split(" ");
                if(parts[1].equals("EQU")){
                    fileASC.constantesYvariables.put(parts[0],parts[2]);
                    FileMan.poolOfConstAndVar.add(parts[0]);
                }
                break;
            case 2://Intrucción 
                while(linea.startsWith(" ")){
                    linea=linea.substring(1);
                }
                while(linea.endsWith(" ")){
                    linea=linea.substring(0,linea.length()-1);
                }
                linea=deleteSpacesIntermedium(linea);
                dato = new Datos(linea,etiqueta,etiquetas,numLinea);
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
                //etiquetas.add(linea);
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
            if(element.mnemonico.equals("Directiva FCB")||element.mnemonico.equals("RESET")){}
            else{
            if(element.opcode.length() == 2){ //Opcode
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
            for(i=0;i<limite;i++){ //Operandos
                if(element.mnemonico.equals("Directiva FCB")){
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
                        saltos.add(element);
                        break;
                    }
                    if(etiquetas.contains(element.operandos.get(i))){ //Si es una etiqueta buscar su localidad
                        for(j=0;j<datos2.size();j++){
                            buscador=element.operandos.get(i);
                            if(datos2.get(j).etiqueta!=null){
                                if(datos2.get(j).etiqueta.equals(buscador)){//Cuando la encuentra
                                    compilacion.add(datos2.get(j).localidad.substring(0,2).toUpperCase());//Agrego la localidad en la tabla de compilacion
                                    compilacion.add(datos2.get(j).localidad.substring(2).toUpperCase());
                                    compilacionLST.add(datos2.get(j).localidad.substring(0,2).toUpperCase());//Agrego la localidad en la tabla de compilacion
                                    compilacionLST.add(datos2.get(j).localidad.substring(2).toUpperCase());
                                    break;
                                }
                            }     
                        }
                        
                    }
                    if(fileASC.constantesYvariables.containsKey(element.operandos.get(i))){ 
                        buscador=fileASC.constantesYvariables.get(element.operandos.get(i));
                            //System.out.println(fileASC.constantesYvariables.get(element.operandos.get(i)));
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
                    Convertidor(element.operandos.get(i));
                }
            }
        }
    }

    public void Convertidor(String numero){ //Checa si el operando es hexadecimal, si no la convierte
        String nuevo, convertido;
        int convertidor;      
        //¿Qué caso es?                                      
        if(numero.charAt(0)=='$' || numero.substring(0, 2).equals("#$")){
            if(numero.charAt(0)=='$'){
                nuevo = numero.substring(1);
                if(nuevo.length()==1 || nuevo.length()==3)
                    nuevo='0'+nuevo;
                if(nuevo.length() == 2){
                    compilacion.add(nuevo.toUpperCase());
                    compilacionLST.add(nuevo.toUpperCase());
                    return;
                }
                else{
                    compilacion.add(nuevo.substring(0,2).toUpperCase());//Opcode 8bits
                    compilacion.add(nuevo.substring(2).toUpperCase());//Opcode 16 bits
                    compilacionLST.add(nuevo.substring(0,2).toUpperCase());//Opcode 8bits
                    compilacionLST.add(nuevo.substring(2).toUpperCase());//Opcode 16 bits                   
                    return;
                }    
            }
        }
        if(numero.substring(0, 2).equals ("#$")){
            nuevo = numero.substring(2);
            if(nuevo.length()==1 || nuevo.length()==3)
                    nuevo='0'+nuevo;
            if(nuevo.length() == 2){
                compilacion.add(nuevo.toUpperCase());
                compilacionLST.add(nuevo.toUpperCase());
                return;
            }
            else{
                compilacion.add(nuevo.substring(0,2).toUpperCase());
                compilacion.add(nuevo.substring(2).toUpperCase());
                compilacionLST.add(nuevo.substring(0,2).toUpperCase());
                compilacionLST.add(nuevo.substring(2).toUpperCase());
                return;
            }    
        }
         //Convertir   
        else{
            if(numero.charAt(0)=='#')
                numero=numero.substring(1);
            convertidor = Integer.parseInt(numero);
            convertido = Integer.toHexString(convertidor);
            if(convertido.length()==1 || convertido.length()==3)
                convertido= '0'+convertido;
            if(convertido.length() <=2 ){
                compilacion.add(convertido.toUpperCase());
                compilacionLST.add(convertido.toUpperCase());
            }    
            else{
                compilacion.add(convertido.substring(0,2).toUpperCase());
                compilacion.add(convertido.substring(2).toUpperCase()); 
                compilacionLST.add(convertido.substring(0,2).toUpperCase());
                compilacionLST.add(convertido.substring(2).toUpperCase());
            }    
        }
    }


    public boolean mnemonicosREL2(String instruccion){
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
    public void imprimirArray(){
        for(int i=0;i<compilacion.size();i++)
            System.out.println(compilacion.get(i));
    }
    
    public void imprimirArrayLST(){
        for(int i=0;i<compilacionLST.size();i++)
            System.out.println(compilacionLST.get(i));
    }

    public void imprimirEtiquetas(){
        for(int i=0;i<etiquetas.size();i++)
            System.out.println(etiquetas.get(i));
    }
    

    public void SecondCheck(FileMan file){//Segunda pasada  
        int i, dif1,dif2,newBin,decimal=0,n=0,j,x, index=0,nuevaLoc; 
        String localidad=" ",hex;
        boolean rep = true;
        int complementoa2;
        while(rep){
            if(saltos.peek()==null){
                break;
            }
            Datos element = saltos.poll(); //Desencolamos
            for(i=0; i<element.operandos.size(); i++){
                String etiqueta = element.operandos.get(i);
                if(etiquetas.contains(etiqueta)){ //Identifica si la etiqueta existe en nuestra lista
                    for(j=0;j<datos2.size();j++){
                        if(datos2.get(j).etiqueta!=null)
                            if(datos2.get(j).etiqueta.equals(etiqueta)){
                                localidad=datos2.get(j).localidad;
                            }   
                    }
                } 
            }
            if((Integer.parseInt(element.localidad,16)>Integer.parseInt(localidad,16))||(Integer.parseInt(element.localidad,16)==Integer.parseInt(localidad,16))){//Salto negativo
                dif1=Integer.parseInt(element.localidad,16);//Lo que estamos analizando
                if(element.operandos.size()==1)
                    dif1+=1;
                else
                    dif1+=3;
                dif2=Integer.parseInt(localidad,16);//Localidad donde se encuentra la etiqueta
                int diferenciaNegativa=(dif2-dif1)-1;//Diferencia de localidades
                if(diferenciaNegativa==-127){
                    file.errores.add(new ErrorASC(8,element.numLinea));
                }
                else{
                    diferenciaNegativa=-1*diferenciaNegativa;
                    String binario = Long.toBinaryString(diferenciaNegativa);
                    while(binario.length()<8){
                        binario = '0'+binario;   
                    }
                    newBin = Integer.parseInt(twosCompliment(binario));
                    decimal=binarioDec(newBin);
                    hex=Integer.toHexString(decimal);
                    for(x=0;x<compilacion.size();x++){
                        if(compilacion.get(x).equals("--")){
                            compilacion.set(x,hex.toUpperCase());
                            compilacionLST.set(x,hex.toUpperCase());
                            break;
                        }
                    }
                }
            }     

            else{//Salto positivo
                dif1=Integer.parseInt(element.localidad,16);
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
                if(diferenciaPositiva==128){
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

    public String twosCompliment(String bin) {
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

    public int binarioDec(int binario){
       int resto, decimal=0, i=0;
       while (binario != 0){
           resto = binario % 10;
           decimal = decimal + (resto * (int) Math.pow(2, i));
           i++;
           binario = binario / 10;
       }
       return decimal;
    }

    public void checkIfMarginCorrect(FileMan file){//Error instrucción sin espacios
        Pattern Textoespaciado = Pattern.compile("^(( )+[a-zA-Z0-9(\\$#)?(’)( ),_]*)$");
        Pattern espaciosBlanco = Pattern.compile("^\\s*$");
        Pattern comentarios = Pattern.compile("(()(\\*)+[A-Za-z0-9_]*)");//Matcher y Patern de Comentarios y espacios en blanco
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
            if(!check2){
                linea = deleteSpacesIntermedium(linea);
                Matcher checker= Textoespaciado.matcher(linea);
                Boolean check1= checker.find();
                if(!check1){//Si no hace match con el regex de instrucción
                    if(linea.contains(" ")){//Checar si es una etiqueta
                        String [] fragmentarlinea= linea.split(" ");
                        if(fragmentarlinea[1].equals("EQU") || fragmentarlinea[1].equals("equ")||(fragmentarlinea[0].toLowerCase().equals("reset")&&fragmentarlinea[1].toLowerCase().equals("fcb"))){
                        }
                        else{
                            file.errores.add(new ErrorASC(9,i));//Error de "margen
                        }        
                    }
                    else{
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
    public String deleteSpacesIntermedium(String linea){
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
    public void initEtiq(){
        Datos dato=null;
        for(int i=0;i<datos2.size();i++){
            if(datos2.get(i).etiqueta!=null)
                dato=datos2.get(i);
                fileASC.EtiquetaLocalidad.put(dato.etiqueta,dato.localidad);
        }
    
    }
    
}
    
    


      
    
    

