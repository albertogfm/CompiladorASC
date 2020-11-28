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
    static FileMan fileASC = new FileMan();
    public Queue <String> etiqueta = new LinkedList<>();
    public static ArrayList <String> etiquetas = new ArrayList<>();
    public static ArrayList <String> compilacion = new ArrayList<>();
    public static ArrayList <Datos> datos2 = new ArrayList<>();
    public ArrayList <String> relativos = new ArrayList<>();
    
    public void Compilador(FileMan file){//Recibe un FileMan
        ArrayList <String> lineasArc = file.lineasArchivoASC;
        boolean repetir = true, repetir2=true;
        int i=0;
        
        while(repetir){
            String linea = lineasArc.get(i);
            i+=1;
            repetir = select(linea);
        }
        firstCheck(fileASC.instrucciones);
        imprimirArray();
    }
    
    public boolean select (String linea){
        Datos dato;
        int caso;
        Validador checker = new Validador();
        Pattern comentariosUnicamente = Pattern.compile("^((\\*)[a-zA-Z0-9_\\*( )]*)$");
        Matcher onlycomentario = comentariosUnicamente.matcher(linea);
        Pattern comentarios = Pattern.compile("((\\*)[a-zA-Z0-9_\\*( )]*)");//Matcher y Patern de Comentarios y espacios en blanco
        Matcher comentario = comentarios.matcher(linea);
        Pattern espaciosBlanco = Pattern.compile("^( )*$");
        Matcher espacios = espaciosBlanco.matcher(linea);

        if(espacios.find() || onlycomentario.find()){ //Ignora los espacios en blanco}
            return true;}

        if(comentario.find()){//Ignora el comentario y se queda con la linea de instrucciones
            String[] parts = linea.split("*");
            linea=parts[0];
        }
        caso = checker.Reconoce(linea);//Solo linea 
        switch(caso){
            case 1://Constante y variable
                String[] parts = linea.split(" ");
                if(parts[1].equals("EQU"))
                    fileASC.constantesYvariables.put(parts[0],parts[2]);
                break;
            case 2://Intrucción 
                while(linea.startsWith(" ")){
                    linea=linea.substring(1);
                }
                while(linea.endsWith(" ")){
                    linea=linea.substring(0,linea.length()-1);
                }
                dato = new Datos(linea,etiqueta,etiquetas);
                if(dato.mnemonico!=null){
                    dato.ImprimirDatos();
                    fileASC.instrucciones.add(dato);
                    datos2.add(dato);
                }
                break;
            case 3://Etiquetas
                etiqueta.add(linea);
                etiquetas.add(linea);
                break;
            case 4://Fin 
                return false;
        }
        return true;
    }

    public void firstCheck(Queue <Datos> datos){ //Agregar elementos a la pila
        int i,j,u=0;
        boolean rep= true;
        while(rep){
            u+=1;
            
            Datos element = datos.poll(); //Desencolamos
            if(datos.peek()==null)
                rep=false;
            String buscador;
            Pattern TagorCons = Pattern.compile("^([A-Za-z]+)([0-9]*)");    
            if(element.opcode.length() == 2) //Opcode
                compilacion.add(element.opcode);
            if(element.opcode.length() == 4){
                compilacion.add(element.opcode.substring(0,2));
                compilacion.add(element.opcode.substring(2));
            }
            int limite=element.operandos.size();
            for(i=0;i<limite;i++){ //Operandos
                Matcher checker = TagorCons.matcher(element.operandos.get(i));//Consultar si es una etiqueta, constante o variable
                if(element.operandos.get(i).equals(" "))
                    break;
                if(checker.find()){
                    if(mnemonicosREL2(element.mnemonico)){
                        compilacion.add("--");
                        break;
                    }
                    if(etiquetas.contains(element.operandos.get(i))){
                        for(j=0;j<datos2.size();j++){
                            buscador=element.operandos.get(i);
                            if(datos2.get(j).etiqueta!=null){
                                if(datos2.get(j).etiqueta.equals(buscador)){
                                compilacion.add(datos2.get(j).localidad.substring(0,2));
                                compilacion.add(datos2.get(j).localidad.substring(2));
                                break;
                                }
                            }     
                        }
                        
                    }
                    if(fileASC.constantesYvariables.containsKey(element.operandos.get(i))){ 
                        buscador=fileASC.constantesYvariables.get(element.operandos.get(i));
                            if(buscador.length() <= 2)
                                compilacion.add(buscador);
                            else{
                                compilacion.add(buscador.substring(0,2));
                                compilacion.add(buscador.substring(2,4));
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
                if(nuevo.length() == 2){
                    compilacion.add(nuevo.toUpperCase());
                    return;
                }
                else{
                    compilacion.add(nuevo.substring(0,2).toUpperCase());//Opcode 8bits
                    compilacion.add(nuevo.substring(2).toUpperCase());//Opcode 16 bits
                    return;
                }    
            }
        }
        if(numero.substring(0, 2).equals ("#$")){
            nuevo = numero.substring(2);
            if(nuevo.length() == 2){
                compilacion.add(nuevo.toUpperCase());
                return;
            }
            else{
                compilacion.add(nuevo.substring(0,2).toUpperCase());
                compilacion.add(nuevo.substring(2).toUpperCase());
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
            if(convertido.length() <=2 )
                compilacion.add(convertido.toUpperCase());
            else{
                compilacion.add(convertido.substring(0,2).toUpperCase());
                compilacion.add(convertido.substring(2).toUpperCase());    
            }    
        }
    }
    public void secondCheck(){
        int i,j;
        String searchTAG=null, foundLoc; 
        for(i=0;i<datos2.size();i++){
            Datos datoREL = datos2.get(i);
            if(mnemonicosREL2(datoREL.mnemonico))
                searchTAG = datoREL.operandos.get(0);
            if(datoREL.mnemonico.equals("bclr") || datoREL.mnemonico.equals("bset"))
                searchTAG = datoREL.operandos.get(2);
            if(searchTAG != null){
                for(j=0;i<datos2.size();i++){
                    if(searchTAG.equals(datos2.get(j).etiqueta))
                        foundLoc = datos2.get(j).localidad;
                }              
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
    /*public void Salto(String init, String End){
        int inicio = Integer.parseInt(init,16);
        int fin = Integer.parseInt(fin,16);
        if( inicio < fin)
            
    }*/
}

      
    
    

