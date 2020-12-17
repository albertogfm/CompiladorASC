package regex;

import java.util.*;
import java.util.regex.*;
import filemanagment.*;

public class ArchivoRegex{ //CLASE COMPILADOR
    FileMan opcode;
    int numeroceldas;
    public ArchivoRegex(){} //Constructor de la clase    
    public String matcher(String nombre,String oper, String direc){ //Método que se encarga de identificar el modo de direccionamiento a través de expresiones regulares y de validarlos
        String mnemonico;
        if(direc==null ){
            mnemonico = nombre+" "+oper;
      
            
            Pattern expresionINH = Pattern.compile("^([a-zA-Z]( ){3,6})$"); // Identifica el modo de direccionamiento INHERENTE
            Pattern expresionIMM = Pattern.compile("^([a-zA-Z]{3,6}( )(((#)(\\$)[0-9a-fA-F]{1,3})|((#)(\\$)[0-9a-fA-F]{4})|((#)[0-9]{2})|((#)[0-9]{4})|((#)(’)[a-zA-Z])))$");// Identifica el modo de direccionamiento INMEDIATO
            Pattern expresionEXT = Pattern.compile("^([a-zA-Z]{3,6}( )((\\$)([0-9a-fA-F]{1,4})|(’[a-zA-Z])|([0-9]{2,4})))$");// Identifica el modo de direcciconamiento EXTENDIDO
            Pattern expresionDIR = Pattern.compile("^([a-zA-Z]{3,6}( )((\\$)[0-9a-fA-F]{2}|([0-9]{2})|(’[a-zA-Z])))$"); // Identifica el modo de direccionamiento  DIRECTO
            Pattern expresionREL = Pattern.compile("^([a-zA-Z]{3,6}( )[a-zA-Z]*)$");// Identifica el modo de direccionamiento el RELATIVO
            Pattern expresionINDX = Pattern.compile("^([a-zA-Z]{3,6}( )(((\\$)([0-9a-fA-F]{2})(,[xX]))|(([0-9]{2})(,[xX]))))$");// Identifica el modo de direccionamiento INDEXADO RESPECTO A "Y"
            Pattern expresionINDY = Pattern.compile("^([a-zA-Z]{3,6}( )(((\\$)([0-9a-fA-F]{2})(,[yY]))|(([0-9]{2})(,[yY]))))$");// Identifica el modo de direccionamiento INDEXADO RESPECTO A "X"
            Pattern comentarios = Pattern.compile("((\\*)[a-zA-Z0-9\\*( )]*)");//Identifica los comentarios

            Matcher inh = expresionINH.matcher(mnemonico);//Valida la expresión INHERENTE
            Matcher imm = expresionIMM.matcher(mnemonico);//Valida la expresión INMEDIATO
            Matcher ext = expresionEXT.matcher(mnemonico);//Valida la expresión EXTENDIDO
            Matcher dir = expresionDIR.matcher(mnemonico);//Valida la expresión DIRCETO
            Matcher rel = expresionREL.matcher(mnemonico);//Valida la expresión RELATIVO       
            Matcher indx = expresionINDX.matcher(mnemonico);//Valida la expresión INDEXADO RESPECTO A "X"
            Matcher indy = expresionINDY.matcher(mnemonico);//Valida la expresión INDEXADO RESPECTO A "Y"
        
        
      //En esta sección identificamos el modo de direccionamiento, y con el método readOpcodes obtenemos el opcode del mnemónico de acuerdo a su modo de direccionamiento  
            if(inh.find()){
                direc="inh";  
            }
            if(imm.find()){
                direc="imm";        
            }
            if(ext.find()) { 
                direc="ext";     
            }  
            if(dir.find()){
                direc="dir";    
            }
            if(rel.find()){
                direc="rel";   
            }
            if(indx.find()){
                direc="indx";         
            }
            if(indy.find()){
                direc="indy";          
            }
            return direc;
        }
        return direc; 
}

}


