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

public class FileMan extends JFrame{
    //Atributos
    public ArrayList<String> lineasArchivoASC = new ArrayList<>();
    public ArrayList<String> opCodesFile = new ArrayList<>();
    //Functions
    public boolean leerArchivo(String nombreAr) { //Lee el archivo que fue selecionado y le asigna el contenido de este al ArrayList lineasArchivoASC
        File file = new File(nombreAr);
        if(!file.exists()){
                System.out.println("\tNo se encontró el archivo");
                return false;
        }
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
            File file = new File(".\\files\\output\\hola.S19");
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
   public void escribirArchivoLST(){
        try {
            File file = new File(".\\files\\output\\hola.LST");
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
    public String fileSelector(){// Opens a JFrame to select a file in our directory
        FileDialog fc;
        fc = new FileDialog(this, "Choose a file", FileDialog.LOAD);
        fc.setDirectory("C:\\");
        fc.setFile("*.asc");
        fc.setVisible(true);
        String fn = fc.getFile();
        if(fn==null){
            return null;
        }
        return fc.getDirectory()+fc.getFile();
    }
}
