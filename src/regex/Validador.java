/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package regex;
import java.util.regex.*;
import filemanagment.*;
import errores.*;
import java.util.ArrayList;
/**
 *
 * @author 1ZW05LA_RS3
 */
public class Validador {
    Error error;
    public int Reconoce(String instruccion,int linea){
        instruccion=deleteSpacesIntermedium(instruccion);
        FileMan file = new FileMan();
        Pattern constantesyVariables = Pattern.compile("^(([A-Za-z0-9_]*)( )+(EQU)( )+(((\\$)[0-9a-fA-F]{1,4})))$");
        Pattern instruccionASC = Pattern.compile("^(( )+[a-zA-Z0-9(\\$#)?( ),_]*)$");
        Pattern etiquetas = Pattern.compile("^([A-Za-z0-9_]*)$");
        Pattern fin = Pattern.compile("(( )+(END)(( )+(\\$)[0-9]{4})?)");
        Pattern reset = Pattern.compile("(( )?(reset|RESET))");        
        Matcher consyvar = constantesyVariables.matcher(instruccion);
        Matcher instruc = instruccionASC.matcher(instruccion);
        Matcher etiq = etiquetas.matcher(instruccion);
        Matcher finPograma = fin.matcher(instruccion);
        Matcher res = reset.matcher(instruccion);
        if(res.find()){
            return 6;
        }
            
        if(consyvar.find()){
            //System.out.println("consyvar"+instruccion);
            return 1;
        }

        if(finPograma.find()){
            //System.out.println("fin"+instruccion);
            return 4;
        }
        
        if(instruc.find()){
            //System.out.println("ins"+instruccion);
            return 2;
        }
        
        if(etiq.find()){
            //System.out.println("etiq"+instruccion);
            //if(file.readNemon(instruccion.toLowerCase())){
            //    System.out.println("Entre "+instruccion);
            //    file.errores.add(new ErrorASC(9,linea));
            //}
            return 3;
            }
        
        return 5;
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
}
