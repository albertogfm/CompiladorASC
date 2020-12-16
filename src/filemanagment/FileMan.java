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

public class FileMan extends JFrame{
    //Atributos
    public ArrayList<String> lineasArchivoASC = new ArrayList<>();
    public ArrayList<String> opCodesFile = new ArrayList<>();
    public static Queue <Datos> instrucciones = new LinkedList<>();
    public static HashMap<String,String> constantesYvariables = new HashMap<>();
    public static Queue <String> poolOfConstAndVar = new LinkedList<>();
    public static ArrayList <ErrorASC> errores = new ArrayList<>();
    public String fileName;
    public String dirToWrite;
    public static int endInLine;
    public static ArrayList<Integer> firstOrg = new ArrayList<>();
    //Functions
    public boolean leerArchivo(String nombreAr) { //Lee el archivo que fue selecionado y le asigna el contenido de este al ArrayList lineasArchivoASC
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
    public void escribirArchivoS19(){
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
   	public void escribirArchivoLST(ArrayList<Datos> datos){   
            Queue<Datos> datosQ = new LinkedList<>();
            for(int i=0; i<datos.size();i++)
                datosQ.add(datos.get(i));
            Pattern comentariosUnicamente = Pattern.compile("^(( )*(\\*)[a-zA-Z0-9\\*_,( )]*)$");
            Pattern espaciosBlanco = Pattern.compile("^( )*$");
            Pattern comentarios = Pattern.compile("(()(\\*)+[A-Za-z0-9_]*)");//Matcher y Patern de Comentarios y espacios en blanco
            Validador checker = new Validador();
            int contadorDeVar = FileMan.poolOfConstAndVar.size(), caso;
            String linea = "";
            String lastDir="";
            int contadorOrgs=FileMan.firstOrg.size(),h=0,contadorDatos=0;
            //Validador val = new Validador();
            try {
                File file = new File(this.fileName+".LTS");
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
                    if(espacios.find() || onlycomentario.find()){
                        System.out.println("eu:"+String.valueOf(i+1).length());
                        for(int k=0; k< maxString-String.valueOf(i+1).length(); k++)
                            bw.append(" ");
                        bw.append(String.valueOf(i+1)+"|"+linea );
                        bw.newLine();                     
                    }else{
                        Matcher comentario = comentarios.matcher(linea);
                        if(comentario.find()){//Ignora el comentario y se queda con la linea de instrucciones
                            String[] parts = linea.split("\\*");
                            linea=parts[0];
                        }
                        caso = checker.Reconoce(linea,i);
                        int contaSpaces=0;
                        
                        switch(caso){
                            case 1://constantes y var
                                String varToFind = FileMan.poolOfConstAndVar.poll();
                                String valorVar =FileMan.constantesYvariables.get(varToFind);
                                for(int k=0; k< maxString-String.valueOf(i+1).length(); k++)
                                    bw.append(" ");
                                bw.append(String.valueOf(i+1)+"|"+valorVar.substring(1)+"|"+lineaToPrint );
                                bw.newLine();
                                break;
                            case 2:// instruccion
                                while(linea.startsWith(" ")){
                                    linea=linea.substring(1);
                                }
                                while(linea.endsWith(" ")){
                                    linea=linea.substring(0,linea.length()-1);
                                }
                                String[] org = linea.split(" ");
                                for(int k=0; k< maxString-String.valueOf(i+1).length(); k++)
                                    bw.append(" ");
                                if(org[0].equals("ORG")|| org[0].equals("org")){
                                    bw.append(String.valueOf(i+1)+"|"+org[1].substring(1)+"|"+ lineaToPrint);
                                    bw.newLine();
                                    lastDir=org[1];
                                }else{
                                    Datos data = datosQ.poll();
                                    lastDir = data.localidad;
                                    bw.append(String.valueOf(i+1)+"|"+data.localidad+"|"+data.opcode);
                                    if(!data.direccionamiento.equals("inh")){
                                        System.out.println((i+1) +"|"+ data.operandos.size());
                                        for(int j = 0 ; j< data.operandos.size(); j++){
                                            String op = data.operandos.get(j);
                                            if(op.contains("#")){
                                                String[] separar = op.split("#");
                                                op = separar[1];
                                            }
                                            if(op.contains("$")){
                                                String[] separar = op.split("\\$");
                                                op = separar[1];
                                            }
                                            bw.append(" "+op);
                                        }
                                    }
                                    bw.append("|"+lineaToPrint);
                                    bw.newLine();
                                }
                                break;
                            case 3://etiqueta
                                for(int k=0; k< maxString-String.valueOf(i+1).length(); k++)
                                    bw.append(" ");
                                bw.append(String.valueOf(i+1)+"|"+lastDir+"|"+lineaToPrint);
                                bw.newLine();
                                break;
                            case 4:// ende
                                for(int k=0; k< maxString-String.valueOf(i+1).length(); k++)
                                    bw.append(" ");
                                bw.append(String.valueOf(i+1)+"|"+lastDir+"|"+lineaToPrint);
                                bw.newLine();
                                break;
                            case 5:
                                break;
                            case 6:// reset
                                break;
                        }
                    }
                    //bw.newLine();
                }
                bw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
    public void resetFileman(){ //Elimina todo el contenido de FileMan
        this.lineasArchivoASC.clear();
        this.opCodesFile.clear();
        this.instrucciones.clear();
        this.constantesYvariables.clear();
        this.errores.clear();
        this.dirToWrite = null;
        this.fileName = null;   
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