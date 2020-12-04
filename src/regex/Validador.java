/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package regex;
import java.util.regex.*;
import filemanagment.*;
import errores.*;
/**
 *
 * @author 1ZW05LA_RS3
 */
public class Validador {
    Error error;
    public int Reconoce(String instruccion,int linea){
        FileMan file = new FileMan();
        Pattern constantesyVariables = Pattern.compile("^(([A-Za-z0-9_]*)( )+(EQU)( )+(((\\$)[0-9a-fA-F]{1,4})))$");
        Pattern instruccionASC = Pattern.compile("^(( )+[a-zA-Z0-9(\\$#)?( ),]*)$");
        Pattern etiquetas = Pattern.compile("[A-Za-z0-9]*");
        Pattern fin = Pattern.compile("(( )+(END)(( )+(\\$)[0-9]{4})?)");

        Matcher consyvar = constantesyVariables.matcher(instruccion);
        Matcher instruc = instruccionASC.matcher(instruccion);
        Matcher etiq = etiquetas.matcher(instruccion);
        Matcher finPograma = fin.matcher(instruccion);
        
        if(consyvar.find())
            return 1;

        if(finPograma.find())
            return 4;
        
        if(instruc.find()){
            return 2;
        }
        
        if(etiq.find()){
            if(file.readNemon(instruccion.toLowerCase())){
                System.out.println("Entre "+instruccion);
                file.errores.add(new ErrorASC(9,linea));
            }
            return 3;
            }
        
        return 5;
    }
    
    
}
