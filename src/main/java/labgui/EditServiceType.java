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
 * Форма для редактирования типа сервиса
 * 
 */
class EditServiceType extends JDialog
{
    private static final Logger log = Logger.getLogger(EditServiceType.class);

    private SalonGUI salonGUI;
    private Salon salon;

    private final int DEFAULT_WIDTH = 400;
    private final int DEFAULT_HEIGHT = 400;

    private JPanel mainPanel;
    private JButton doneButton;

    private JTextField descriptionText;
    private JTextField currentPriceText;
    private JTextField percentageToEmployeeText;
    private boolean state;

    public EditServiceType(JFrame owner, ServiceType st)
    {
        super(owner, "Edit specialization", false);

        log.info("Creating new EditServiceType frame...");

        mainPanel = new JPanel();
        /*
        id
        description: []
        currentPrice: []
        percentageToEmployee: []
        Relevant: *
        [done]
        */
        mainPanel.setLayout(new GridLayout(6, 0, 15, 15));
        this.add(mainPanel);
        updateEdit(st);

        this.setLocationRelativeTo(owner);
        pack();

        log.info("EditServiceType frame was created.");
    }

    /**
     * Обновить содержимое формы
     * 
     * @param st тип сервиса, для которого обновляется
     */
    public void updateEdit(ServiceType st)
    {
        mainPanel.removeAll();

        salonGUI = SalonGUI.getSalonGUI();
        salon = salonGUI.getSalon();

        //id
        JPanel ip = new JPanel();
        ip.add(new JLabel("ID is " + st.getID()));
        mainPanel.add(ip);

        //description
        JPanel dp = new JPanel();
        dp.add(new JLabel("Description: "));
        descriptionText = new JTextField(st.getDescription(), 20);
        dp.add(descriptionText);
        mainPanel.add(dp);

        //currentPrice
        JPanel cpp = new JPanel();
        cpp.add(new JLabel("Price: "));
        currentPriceText = new JTextField("" + st.getCurrentPrice(), 20);
        cpp.add(currentPriceText);
        mainPanel.add(cpp);

        //percentageToEmployee
        JPanel ptep = new JPanel();
        ptep.add(new JLabel("Percentage: "));
        percentageToEmployeeText = new JTextField("" + st.getPercent(), 20);
        ptep.add(percentageToEmployeeText);
        mainPanel.add(ptep);

        //Relevant
        JPanel ap = new JPanel();
        ap.add(new JLabel("State: "));
        JCheckBox cb = new JCheckBox("relevant");
        state = st.isRelevant();
        cb.setSelected(state);
        cb.addItemListener(new ItemListener() {    
            public void itemStateChanged(ItemEvent ie) {
                state = cb.isSelected();
            }
        });
        ap.add(cb);
        mainPanel.add(ap);

        //Button
        doneButton = new JButton("Done!");
        doneButton.addActionListener(aeDone -> {changeServiceType(st);});
        mainPanel.add(doneButton);

        mainPanel.updateUI();
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @SuppressWarnings( "deprecation" )
    private void changeServiceType(ServiceType st)
    {
        try
        {
            log.info("Trying to edit service type...");

            //description
            st.changeDescription( Salon.checkName4Salon(descriptionText.getText()) );

            //currentPrice
            st.setPrice( Double.parseDouble( currentPriceText.getText() ) );

            //percentageToEmployee
            st.setPercent( Float.parseFloat( percentageToEmployeeText.getText() ) );
        }
        catch(NumberFormatException ne)
        {
            String msg = ne.getMessage();
            if(msg.indexOf("\"") != -1)
                msg = msg.substring(msg.indexOf("\""), msg.lastIndexOf("\"")+1);
            JOptionPane.showMessageDialog(this, "You inputted wrong number: " + msg + ".", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        catch(InvalidNameException ie)
        {
            JOptionPane.showMessageDialog(this, ie.getMessage() + "Bab symbol: " + ie.getInvalidSymbol(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        catch(Exception ignor)
        {
            log.error("Problem with editing service type: " + st + ": " + ignor);
            //ignor.printStackTrace();
            return;
        }
        
        //Relevant
        st.relevant(state);

        salonGUI.refreshTablesOf_ServiceTypes();

        log.info("Service type was edited.");
    }
}