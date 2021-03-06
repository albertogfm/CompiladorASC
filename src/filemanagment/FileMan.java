package filemanagment;

import java.awt.FileDialog;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import javax.swing.JFrame;
import java.util.HashMap;
import errores.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import regex.Validador;
import compiladorasc.CompiladorASC;
import java.util.Collections;

public class FileMan extends JFrame{
    //Atributos Estáticos
    public static Queue <Datos> instrucciones = new LinkedList<>();
    public static HashMap<String,String> constantesYvariables = new HashMap<>();
    public static HashMap<String,String> EtiquetaLocalidad = new HashMap<>();
    public static Queue <String> poolOfConstAndVar = new LinkedList<>();
    public static ArrayList <ErrorASC> errores = new ArrayList<>();
    public static int endInLine;
    public static ArrayList<Integer> firstOrg = new ArrayList<>();
    //Atributos
    public ArrayList<String> lineasArchivoASC = new ArrayList<>();
    public ArrayList<String> opCodesFile = new ArrayList<>();
    public String fileName;
    public String dirToWrite;
    //Funciones
    //Lee el archivo que fue selecionado y le asigna el contenido de este al ArrayList lineasArchivoASC
    public boolean leerArchivo(String nombreAr) { 
        File file = new File(nombreAr);
        if(!file.exists()){
                System.out.println("\tNo se encontró el archivo");
                return false;
        }
        this.lineasArchivoASC.clear();
        try {

            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                
                String linea = sc.nextLine();//Lee el contenido del archivo
                this.lineasArchivoASC.add(linea);
            }
            sc.close();

        }catch (FileNotFoundException e) {
            System.out.println("Scanner unable to use");
        }
        return true;
    }
    //Este es el método que hace la escritura del archivo S19 con formato pedido
    public void escribirArchivoS19(){
        for(int i=0;i< CompiladorASC.compilacionLST.size();i++){
            System.out.println(CompiladorASC.compilacionLST.get(i));
        }
        try {
            File file = new File(this.fileName+".S19");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file,false);
            BufferedWriter bw = new BufferedWriter(fw);
            int localidadStart = 8000;
            int numLinAppend = this.opCodesFile.size()/15;
            int resto = this.opCodesFile.size()%15;
            int contador = 0;
            for (int i=0; i< numLinAppend ; i++){
                bw.append("<"+localidadStart+">" );
                for(int j=0 ;j<15;j++){
                    bw.append(" "+this.opCodesFile.get(contador)+" ");
                    contador++;
                }
                bw.append("\n");
                localidadStart+=10;
            }
            if(resto > 0){
                bw.append("<"+localidadStart+">" );
                for (int i=0;i<resto ; i++){  
                    bw.append(" "+this.opCodesFile.get(contador)+" ");
                    contador++;
                }
            }     
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Metodo para escribir el htmlS19
    public void escribirArchivoS19html(){
        try {
            File file = new File(this.fileName+".html");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file,false);
            BufferedWriter bw = new BufferedWriter(fw);
            int localidadStart = 8000;
            int numLinAppend = this.opCodesFile.size()/15;
            int resto = this.opCodesFile.size()%15;
            int contador = 0;
            bw.append("<!DOCTYPE html> \n <html> \n <head> \n <link rel=\"stylesheet\" href=\""+this.fileName +".css\">"
                    + "</head> <header><h1>Archivo S19<h1></header>"
                    + "<body>"
                    + "<div class=\"codigo\">");
            for (int i=0; i< numLinAppend ; i++){
                bw.append("<linea><"+localidadStart+"></linea>" );
                
                
                for(int j=0 ;j<15;j++){
                    if(CompiladorASC.compilacionLST.get(contador).length()== 4){
                    bw.append("<opcode> "+this.opCodesFile.get(contador)+" </opcode>" );
                    }else{
                        bw.append(" <oper> "+this.opCodesFile.get(contador)+" </oper> ");
                    }
                    contador++;
                }
                bw.append("\n <br>");
                localidadStart+=10;
            }
            if(resto > 0){
                bw.append("<linea><"+localidadStart+"></opcode>" );
                for (int i=0;i<resto ; i++){
                    if(CompiladorASC.compilacionLST.get(contador).length()== 4){
                    bw.append("<opcode> "+this.opCodesFile.get(contador)+" </opcode>" );
                    }else{
                        bw.append(" <oper> "+this.opCodesFile.get(contador)+" </oper> ");
                    }
                    contador++;
                }
            }
            bw.append("</div> \n <div class=\"codigo\"><opcode>Este color indica que se trata del OPCODE</opcode><br>"
                    + "<oper>Este color indica que se trata de los OPERANDOS</oper></div></body> \n </html>");
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            File file = new File(this.fileName+".css");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file,false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append("html {\n" +
            "	background-color:#F0F0F0 ;\n" +
            "}\n" +
            "h1 {\n" +
            "	color: #000;\n" +
            "}\n" +
            ".codigo {\n" +
            "  background-color: #C6C8D1;\n" +
            "  margin: 1rem;\n" +
            "  padding-left: 20rem;\n" +
            "  padding-top: 2rem;\n" +
            "  padding-bottom: 2rem;\n" +
            "  border: 2px solid #000000;\n" +
            "\n" +
            "}\n" +
            "header{\n" +
            "	background-color: #C6C8D1;\n" +
            "	margin: 20px;\n" +
            "	text-align: center;\n" +
            "	border: 2px solid #000000;\n" +
            "	/* height: 50px; */\n" +
            "}\n" +
            "header h1{\n" +
            "	color:#000000;\n" +
            "}\n" +
            "\n" +
            "opcode{\n" +
            "	font-family: Arial;\n" +
            "	font-size: 25px;\n" +
            "	color:#044C80;\n" +
            "}\n" +
            "oper{\n" +
            "	font-family: Arial;\n" +
            "	font-size: 25px;\n" +
            "	color:#2396EB;\n" +
            "}\n" +
            "linea{\n" +
            "	font-family: Arial, Bold;\n" +
            "	font-size: 28px;\n" +
            "	color:#070B52;\n" +
            "}");
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Este es el método que hace la escritura del archivo LST
    public void escribirArchivoLST(ArrayList<Datos> datos){   
        Queue<Datos> datosQ = new LinkedList<>();
        Queue<String> compLST = new LinkedList<>();
        ArrayList<String> arrayOfVarToSort = new ArrayList<>();
        int contadorDeVar = FileMan.poolOfConstAndVar.size(), caso;
        String linea = "";
        String lastDir="";
        int contadorOrgs=FileMan.firstOrg.size(),h=0,contadorDatos=0;
        
        Validador checker = new Validador();
        //Vaciado de Listas
        for(int i=0; i<datos.size();i++)
            datosQ.add(datos.get(i));
        for(int i=0; i<CompiladorASC.compilacionLST.size();i++)
            compLST.add(CompiladorASC.compilacionLST.get(i));
        //Patterns a Buscar
        Pattern comentariosUnicamente = Pattern.compile("^(( )*(\\*)[a-zA-Z0-9\\*_,( )]*)$");
        Pattern espaciosBlanco = Pattern.compile("^( )*$");
        Pattern comentarios = Pattern.compile("(()(\\*)+[A-Za-z0-9_]*)");
       
        //Validador val = new Validador();
        try {
            File file = new File(this.fileName+".LST");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, false);
            BufferedWriter bw = new BufferedWriter(fw);
            int maxString =String.valueOf(this.lineasArchivoASC.size()).length();
            System.out.println("el:"+ maxString);
            for(int i=0; i< this.lineasArchivoASC.size() ; i++){
                linea = this.lineasArchivoASC.get(i);
                String lineaToPrint = linea;
                Matcher onlycomentario = comentariosUnicamente.matcher(linea);
                Matcher espacios = espaciosBlanco.matcher(linea);
                
                if(espacios.find() || onlycomentario.find()){ //Caso de que la linea es espacio en blanco o solo un comentario
                    //System.out.println("eu:"+String.valueOf(i+1).length());
                    for(int k=0; k< maxString-String.valueOf(i+1).length(); k++)
                        bw.append(" ");
                    bw.append(String.valueOf(i+1)+"|");
                    bw.append("                    ");
                    bw.append(lineaToPrint);
                    bw.newLine();                     
                }else{ //Separador de  casos
                    Matcher comentario = comentarios.matcher(linea);
                    if(comentario.find()){//Ignora el comentario y se queda con la linea de instrucciones
                        String[] parts = linea.split("\\*");
                        linea=parts[0];
                    }
                    caso = checker.Reconoce(linea,i);
                    int contaSpaces=0;
                    linea = checker.deleteSpacesIntermedium(linea);
                    switch(caso){
                        case 1://Caso de imprimir constantes y variables
                            String varToFind = FileMan.poolOfConstAndVar.poll();
                            arrayOfVarToSort.add(varToFind);
                            String valorVar =FileMan.constantesYvariables.get(varToFind);
                            for(int k=0; k< maxString-String.valueOf(i+1).length(); k++)
                                bw.append(" ");
                            bw.append(String.valueOf(i+1)+"|"+valorVar.substring(1).toUpperCase());
                            bw.append("                    ");//4 tabs
                            bw.append(lineaToPrint);
                            bw.newLine();
                            
                            break;
                        case 2:// instruccion
                            int contadorTabs = 0;
                            while(linea.startsWith(" ")){
                                linea=linea.substring(1);
                            }
                            while(linea.endsWith(" ")){
                                linea=linea.substring(0,linea.length()-1);
                            }
                            //System.out.println(linea);
                            String[] org = linea.split(" ");
                            for(int k=0; k< maxString-String.valueOf(i+1).length(); k++)
                                bw.append(" ");
                            if(org[0].equals("ORG")|| org[0].equals("org")){
                                System.out.println("--"+org[1]);
                                bw.append(String.valueOf(i+1)+"|"+org[1].substring(1).toUpperCase());
                                bw.append("                    ");//4 tabs
                                bw.append(lineaToPrint);
                                bw.newLine();
                                lastDir=org[1];
                            }else{
                                Datos data = datosQ.poll();
                                if(data.opcode.equals("0")){
                                    lastDir = data.localidad;
                                    bw.append(String.valueOf(i+1)+"|"+data.localidad.toUpperCase()+"|");
                                    for(int j=0;j<data.operandos.size();j++){
                                        String operando = data.operandos.get(j);
                                        if(operando.startsWith("$"))
                                            operando=operando.substring(1);
                                        bw.append(operando+" ");
                                    }
                                    bw.append("\t\t\t   "+lineaToPrint);
                                    bw.newLine();
                                }else{
                                    try{
                                        
                                        lastDir = data.localidad;
                                        bw.append(String.valueOf(i+1)+"|"+data.localidad.toUpperCase()+"|");
                                        if(compLST.peek().startsWith("-") && compLST.peek().endsWith("-")){
                                            String opcode = compLST.poll();
                                            opcode = opcode.substring(1, 3);
                                            bw.append( opcode );
                                            contadorTabs++;
                                        }else{
                                            String opcode = compLST.poll();
                                            opcode = opcode.substring(1);
                                            bw.append(opcode +" ");
                                            contadorTabs++;
                                            opcode = compLST.poll();
                                            opcode = opcode.substring(0,2);
                                            bw.append(opcode);
                                            contadorTabs++;
                                        }
                                        if(!data.direccionamiento.equals("inh")){

                                            while(!compLST.peek().startsWith("-"))
                                                bw.append(" "+compLST.poll());
                                            contadorTabs++;

                                        }else{
                                            contadorTabs =(contadorTabs*2)+1;
                                            for(int y=0 ; y<16-contadorTabs ; y++)
                                                bw.append(" ");
                                        }

                                    }catch (NullPointerException e){

                                    }
                                    bw.append("\t\t\t\t"+lineaToPrint);
                                    bw.newLine();
                                }    
                             }
                             break;
                        case 3://etiqueta
                            for(int k=0; k< maxString-String.valueOf(i+1).length(); k++)
                                bw.append(" ");
                            if(lastDir.contains("$")){
                                String[] separar = lastDir.split("\\$");
                                lastDir = separar[1];
                            }
                            bw.append(String.valueOf(i+1)+"|"+lastDir);
                            bw.append("                    ");//4 tabs
                               bw.append(lineaToPrint);
                            bw.newLine();
                            break;
                        case 4:// end
                            for(int k=0; k< maxString-String.valueOf(i+1).length(); k++)
                                bw.append(" ");
                            if(lastDir.startsWith("$"))
                                lastDir = lastDir.substring(1);
                            bw.append(String.valueOf(i+1)+"|"+lastDir.toUpperCase());
                            bw.append("                    ");//4 tabs
                            bw.append(lineaToPrint);
                            bw.newLine();
                            break;
                         case 5:
                            break;
                         case 6:// reset
                            try{
                                for(int k=0; k< maxString-String.valueOf(i+1).length(); k++)
                                    bw.append(" ");
                                if(lastDir.startsWith("$"))
                                    lastDir = lastDir.substring(1);
                                bw.append(String.valueOf(i+1)+"|"+lastDir+" ");
                                //
                                Datos data = datosQ.poll();
                                bw.append(data.operandos.get(0).substring(1));
                                bw.append(data.operandos.get(1).substring(1));
                                bw.append("                    ");//4 tabs
                                bw.append(lineaToPrint);
                                bw.newLine();
                            }catch(NullPointerException e){
                                System.out.println(e.getMessage());
                            }
                            break;
                     }
                 }
                 //bw.newLine();
            }
            //SyMBOL table
            for(int i=0; i < CompiladorASC.etiquetas.size();i++){
                arrayOfVarToSort.add(CompiladorASC.etiquetas.get(i));
            }
            
            Collections.sort(arrayOfVarToSort);
            for(int i=0;i< arrayOfVarToSort.size();i++){
                System.out.println(arrayOfVarToSort.get(i));
            }
            if(arrayOfVarToSort.size() > 0){//Caso De Mas de UnA Variable
                bw.newLine();
                bw.append("SYMBOL TABLE:  Total Entries=   "+arrayOfVarToSort.size());
                bw.newLine();
                if(arrayOfVarToSort.size()%2 == 0){ //Si hay variables pares
                    int izquierda = 0, derecha= arrayOfVarToSort.size()/2;
                    for(int j=0; j<arrayOfVarToSort.size()/2;j++){
                        String var1 = arrayOfVarToSort.get(izquierda);
                        String var2 = arrayOfVarToSort.get(derecha);
                        bw.append(var1);
                        for(int i=0; i< 20- var1.length(); i++)
                            bw.append(" ");
                        String valorDirLista1, valorDirLista2;
                        if( FileMan.constantesYvariables.containsKey(var1)){
                            valorDirLista1 = FileMan.constantesYvariables.get(var1);
                        }else{
                            valorDirLista1 = FileMan.EtiquetaLocalidad.get(var1);
                        }
                        if( FileMan.constantesYvariables.containsKey(var2)){
                            valorDirLista2 = FileMan.constantesYvariables.get(var2);
                        }else{
                            valorDirLista2 = FileMan.EtiquetaLocalidad.get(var2);
                        }
                        
                        if(valorDirLista1.startsWith("$"))
                            valorDirLista1 = valorDirLista1.substring(1);
                        if(valorDirLista2.startsWith("$"))
                            valorDirLista2 = valorDirLista2.substring(1);
                        bw.append(valorDirLista1.toUpperCase()+"    ");
                        bw.append(var2);
                        for(int i=0; i< 20- var2.length(); i++)
                            bw.append(" ");
                        bw.append(valorDirLista2.toUpperCase()+"    ");
                        bw.newLine();
                        izquierda++;
                        derecha++;
                    }
                }else{
                    if(arrayOfVarToSort.size() == 1){//Caso de una sola variable
                        String var = arrayOfVarToSort.get(0);
                        bw.append(var);
                        String valorDirLista1;
                        if( FileMan.constantesYvariables.containsKey(var)){
                            valorDirLista1 = FileMan.constantesYvariables.get(var);
                        }else{
                            valorDirLista1 = FileMan.EtiquetaLocalidad.get(var);
                        }
                        if(valorDirLista1.startsWith("$"))
                            valorDirLista1 = valorDirLista1.substring(1);
                        for(int i=0; i< 20- var.length(); i++)
                                bw.append(" ");
                        bw.append(valorDirLista1.toUpperCase()+"    ");
                    }else{//Caso de 3 o Mas Variables impartes
                        int izquierda = 0, derecha= (arrayOfVarToSort.size()+1)/2;
                        for(int j=0; j<(arrayOfVarToSort.size()-1)/2;j++){
                            String var1 = arrayOfVarToSort.get(izquierda);
                            String var2 = arrayOfVarToSort.get(derecha);
                            bw.append(var1);
                            for(int i=0; i< 20- var1.length(); i++)
                                bw.append(" ");
                            String valorDirLista1, valorDirLista2;
                            if( FileMan.constantesYvariables.containsKey(var1)){
                                valorDirLista1 = FileMan.constantesYvariables.get(var1);
                            }else{
                                valorDirLista1 = FileMan.EtiquetaLocalidad.get(var1);
                            }
                            if( FileMan.constantesYvariables.containsKey(var2)){
                                valorDirLista2 = FileMan.constantesYvariables.get(var2);
                            }else{
                                valorDirLista2 = FileMan.EtiquetaLocalidad.get(var2);
                            }
                            if(valorDirLista1.startsWith("$"))
                                valorDirLista1 = valorDirLista1.substring(1);
                            if(valorDirLista2.startsWith("$"))
                               valorDirLista2 = valorDirLista2.substring(1);
                            bw.append(valorDirLista1.toUpperCase()+"    ");
                            bw.append(var2);
                            for(int i=0; i< 20- var2.length(); i++)
                                bw.append(" ");
                            bw.append(valorDirLista2.toUpperCase()+"    ");
                            bw.newLine();
                            izquierda++;
                            derecha++;
                        }
                        String var1 = arrayOfVarToSort.get(izquierda);
                        bw.append(var1);
                        for(int i=0; i< 20- var1.length(); i++)
                            bw.append(" ");
                        String valorDirLista1;
                        if( FileMan.constantesYvariables.containsKey(var1)){
                            valorDirLista1 = FileMan.constantesYvariables.get(var1);
                        }else{
                            valorDirLista1 = FileMan.EtiquetaLocalidad.get(var1);
                        }
                        if(valorDirLista1.startsWith("$"))
                            valorDirLista1 = valorDirLista1.substring(1);
                        bw.append(valorDirLista1.toUpperCase()+"    ");
                    }    
                }
            }
            bw.newLine();
            bw.newLine();
            bw.append("Total errors: 0");
            bw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
    //Recibe los nemónicos y devuelve si tiene nemónico o no
    public String readOpcodes(String nemon, String modo) {
        File file = new File(".\\files\\opcodes\\"+modo+".csv");
        if(!file.exists()){
                System.out.println("\tNo se encontró el archivo");
                return null;
        }
        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String linea = sc.nextLine();//Lee el contenido del archivo
                String[] nemonYopcode = linea.split(",");
                if(nemon.equals(nemonYopcode[0])){
                    sc.close();
                    return nemonYopcode[1];
                }
            }
            sc.close();

        }catch (FileNotFoundException e) {
            System.out.println("Scanner unable to use");
        }       
        return null;
    }
    public boolean readNemon(String nemon) {
        File file = new File(".\\files\\opcodes\\NEMON.csv");
        if(!file.exists()){
                System.out.println("\tNo se encontró el archivo.....");
                return false;
        }
        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String linea = sc.nextLine();//Lee el contenido del archiv
                if(nemon.equals(linea.toLowerCase())){
                    sc.close();
                    return true;
                }
            }
            sc.close();

        }catch (FileNotFoundException e) {
            System.out.println("Scanner unable to use");
        }       
        return false;
    }
    public String fileSelector(){// Opens a JFrame to select a file in our directory
        FileDialog fc;
        fc = new FileDialog(this, "Escoga el archivo a compilar", FileDialog.LOAD);
        fc.setIconImage(new ImageIcon(getClass().getResource("/graficas/Motorola-Logo.png")).getImage());
        fc.setDirectory("C:\\");
        fc.setFile("*.asc");
        fc.setVisible(true);
        String fn = fc.getFile();
        if(fn==null){
            return null;
        }
        String[] fileN = fc.getFile().split("(.(ASC|asc))",0);
        this.fileName = fc.getDirectory()+fileN[0];
        System.out.println(fc.getDirectory()+fileN[0]);
        return fc.getDirectory()+fc.getFile();
    }
    public String dirSelector(){// Opens a JFrame to select a file in our directory
        FileDialog fc;
        fc = new FileDialog(this, "Choose a file", FileDialog.SAVE);
        fc.setDirectory("C:\\");
        fc.setVisible(true);
        String fn = fc.getFile();
        if(fn==null){
            return null;
        }
        return fc.getDirectory();
    }
    //Escribe el archivo txt de errores s hay
    public void escribirErrores(){
        try {
            File file = new File(this.fileName+"-Errores-Al-Compilar"+".txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append("*******************************************************\n");
            bw.append("*** EL CÓDIGO FUENTE CONTIENE LOS SIGUIENTES ERRORES***\n");
            bw.append("*******************************************************\n");
            for (int i=0 ; i< this.errores.size() ; i++)
                bw.append(this.errores.get(i).toString());
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Hace reset de los elementos fileman
    public void resetFileman(){ //Elimina todo el contenido de FileMan
        this.lineasArchivoASC.clear();
        this.opCodesFile.clear();
        this.instrucciones.clear();
        this.constantesYvariables.clear();
        this.EtiquetaLocalidad.clear();
        this.errores.clear();
        this.poolOfConstAndVar.clear();
        this.dirToWrite = null;
        this.fileName = null;
        this.firstOrg.clear();
        this.fileName = "";
    }
    
    public ArrayList<String> listOfErrorToText(){ //Convierte la lista de errores a una lista con String de errores
        ArrayList<String> errorListString = new ArrayList<>();
        errorListString.add("******************************************************************\n");
        errorListString.add("*** EL CÓDIGO FUENTE CONTIENE LOS SIGUIENTES ERRORES***\n");
        errorListString.add("******************************************************************\n");
        for (int i=0 ; i< this.errores.size() ; i++)
                errorListString.add(this.errores.get(i).toString());
        return errorListString;
    }
}