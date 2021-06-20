package labgui;

import java.lang.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;

import lab.*;
import labgui.*;

import org.apache.log4j.Logger;

/**
 * Форма для редактирования сервиса
 * 
 */
class EditService extends JDialog
{
    private static final Logger log = Logger.getLogger(EditService.class);

    private SalonGUI salonGUI;
    private Salon salon;

    private final int DEFAULT_WIDTH = 200;
    private final int DEFAULT_HEIGHT = 200;

    private JPanel mainPanel;
    private JButton doneButton;

    private boolean state;

    public EditService(JFrame owner, Service s)
    {
        super(owner, "Edit specialization", false);

        log.info("Creating new EditService frame...");

        mainPanel = new JPanel();
        /*
        id
        REL: *
        [done]
        */
        mainPanel.setLayout(new GridLayout(3, 0, 15, 15));
        this.add(mainPanel);
        updateEdit(s);

        this.setLocationRelativeTo(owner);
        pack();

        log.info("EditService frame was created.");
    }

    /**
     * Обновить содержимое формы
     * 
     * @param s сервис, для которого обновляется
     */
    public void updateEdit(Service s)
    {
        mainPanel.removeAll();

        salonGUI = SalonGUI.getSalonGUI();
        salon = salonGUI.getSalon();

        //id
        JPanel ip = new JPanel();
        ip.add(new JLabel("ID is " + s.getID()));
        mainPanel.add(ip);

        //REL
        JPanel rp = new JPanel();
        rp.add(new JLabel("State: "));
        JCheckBox cb = new JCheckBox("relevant");
        state = s.isRELEVANT();
        cb.setSelected(state);
        cb.addItemListener(new ItemListener() {    
            public void itemStateChanged(ItemEvent ie) {
                state = cb.isSelected();
            }
        });
        rp.add(cb);
        mainPanel.add(rp);

        //Button
        doneButton = new JButton("Done!");
        doneButton.addActionListener(aeDone -> {changeService(s);});
        mainPanel.add(doneButton);

        mainPanel.updateUI();
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @SuppressWarnings( "deprecation" )
    private void changeService(Service s)
    {
        log.info("Trying to edit service...");

        //REL
        s.setRELEVANT(state);  
        
        salonGUI.refreshTablesOf_Service();

        log.info("Service was edited.");
    }
}