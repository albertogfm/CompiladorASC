/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graficas;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

/**
 *
 * @author alber
 */
public class ErrorUI extends JPanel {
    protected JTextArea textArea;
    private final static String newline = "\n";

    public ErrorUI(ArrayList<String> lista) {
        super(new GridBagLayout());
        
        textArea = new JTextArea(20, 50);
        textArea.setEditable(false);
        textArea.setFont(new Font("Serif",Font.PLAIN,20));
        for(int i=0 ; i< lista.size();i++)
            textArea.append(lista.get(i));
        
        JScrollPane scrollPane = new JScrollPane(textArea);

        //Add Components to this panel.
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;

        c.fill = GridBagConstraints.HORIZONTAL;
        

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(scrollPane, c);
    }
}
