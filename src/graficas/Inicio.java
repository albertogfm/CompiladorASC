/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graficas;

import filemanagment.FileMan;
import java.awt.FileDialog;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import compiladorasc.*;
import javax.swing.JFrame;

/**
 *
 * @author alber
 */
public class Inicio extends javax.swing.JFrame {
    FileMan file;
    boolean verif;
    /**
     * Creates new form Inicio
     */
    public Inicio() {
        initComponents();
        this.setIconImage(new ImageIcon(getClass().getResource("/graficas/Motorola-Logo.png")).getImage());
        file= new FileMan();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        textField2 = new java.awt.TextField();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        textField1 = new java.awt.TextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        logo = new javax.swing.JLabel();
        fileB = new javax.swing.JButton();
        compileB = new javax.swing.JButton();
        text = new javax.swing.JLabel();

        textField2.setText("textField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(119, 116, 116));

        jPanel2.setBackground(new java.awt.Color(196, 196, 196));

        textField1.setText("textField1");

        jLabel2.setText("jLabel2");

        jLabel3.setFont(new java.awt.Font("SimSun", 1, 48)); // NOI18N
        jLabel3.setText("COMPILADOR MC68HC11");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(398, 398, 398)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addContainerGap())
        );

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graficas/Motorola-Logo.png"))); // NOI18N

        fileB.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        fileB.setText("ARCHIVOS");
        fileB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileBActionPerformed(evt);
            }
        });

        compileB.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        compileB.setText("COMPILAR");
        compileB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compileBActionPerformed(evt);
            }
        });

        text.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        text.setText("PoweredBy: Hanna, Alberto, Cris, Eduardo");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(fileB, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(logo, javax.swing.GroupLayout.PREFERRED_SIZE, 726, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(compileB, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(257, 257, 257)
                        .addComponent(text)))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(335, 335, 335)
                        .addComponent(fileB, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(92, 92, 92)
                        .addComponent(logo, javax.swing.GroupLayout.PREFERRED_SIZE, 710, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(336, 336, 336)
                        .addComponent(compileB, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addComponent(text)
                .addGap(26, 26, 26))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fileBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileBActionPerformed
        String dir= file.fileSelector();
        if(dir == null){
            this.verif=false;
        }else{
            this.verif=true;
            file.leerArchivo(dir);
        }      
        System.out.println("You chose " + dir);
    }//GEN-LAST:event_fileBActionPerformed

    private void compileBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_compileBActionPerformed
        
        if(this.verif){
            System.out.println("Compilado");
            System.out.println("Compilado");
            ArrayList<String> arch = this.file.lineasArchivoASC;
            

            CompiladorASC compi = new CompiladorASC();
            compi.Compilador(this.file);
            boolean comp = this.file.errores.isEmpty();
            if(comp == true){
                
                //file.escribirArchivoLST(compi.datos2);
                file.escribirArchivoS19();
                System.out.println("END IN"+FileMan.endInLine);
                System.out.println("ORG IN"+FileMan.firstOrg.get(0));
                int sizeOfPool = FileMan.poolOfConstAndVar.size();
                System.out.println("CONST AND VAR : "+sizeOfPool);
                
                for(int j=0 ; j<sizeOfPool; j++){
                    System.out.println(j+".-"+FileMan.poolOfConstAndVar.poll());
                }
                JOptionPane.showMessageDialog(this,"El código fuente fue compilado con éxito","    COMPILADO",2);
            
            }else{
                file.escribirErrores();
                JOptionPane.showMessageDialog(this,"El código fuente contiene errores, cheacr el archivo con los errores","    ERROR AL COMPILAR",1);
                //
                JFrame frame = new JFrame("ERRORES");
                frame.add(new ErrorUI(file.listOfErrorToText()));
                frame.setIconImage(new ImageIcon(getClass().getResource("/graficas/Motorola-Logo.png")).getImage());
                //Display the window.
                frame.pack();
                frame.setVisible(true);
                //
            }

            this.verif=false;
        }else{
            JOptionPane.showMessageDialog(this,"Escoge un archivo primero","    NO HAY ARCHIVO CARGADO",2);
        }
        this.file.resetFileman();

    }//GEN-LAST:event_compileBActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Inicio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Inicio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Inicio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Inicio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Inicio ini = new Inicio();
                ini.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton compileB;
    private javax.swing.JButton fileB;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel logo;
    private javax.swing.JLabel text;
    private java.awt.TextField textField1;
    private java.awt.TextField textField2;
    // End of variables declaration//GEN-END:variables
}
