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

public class FileMan extends JFrame{
    //Atributos
    public ArrayList<String> lineasArchivoASC = new ArrayList<>();
    public ArrayList<String> opCodesFile = new ArrayList<>();
    public static Queue <Datos> instrucciones = new LinkedList<>();
    public static HashMap<String,String> constantesYvariables = new HashMap<>();
    public static ArrayList <ErrorASC> errores = new ArrayList<>();
    public String fileName;
    public String dirToWrite;
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
            

            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   	public void escribirArchivoLST(){
        try {
            File file = new File(this.fileName+".LTS");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, false);
            BufferedWriter bw = new BufferedWriter(fw);
            for(int i=0; i< this.lineasArchivoASC.size() ; i++){
                bw.append(this.lineasArchivoASC.get(i));
                bw.newLine();
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
            File file = new File(this.dirToWrite+this.fileName+"-Errores-Al-Compilar"+".txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append("hi");
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}