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
 * Форма для добавления типа сервиса
 * 
 */
class AddServiceType extends JDialog
{
    private static final Logger log = Logger.getLogger(AddServiceType.class);

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

    public AddServiceType(JFrame owner)
    {
        super(owner, "Add specialization", false);

        log.info("Creating new AddServiceType frame...");

        mainPanel = new JPanel();
        /*
        description: []
        currentPrice: []
        percentageToEmployee: []
        Relevant: *
        [done]
        */
        mainPanel.setLayout(new GridLayout(5, 0, 15, 15));
        this.add(mainPanel);
        updateAdd();

        this.setLocationRelativeTo(owner);
        pack();

        log.info("AddServiceType frame was created.");
    }

    /**
     * Обновить содержимое формы
     * 
     */
    public void updateAdd()
    {
        mainPanel.removeAll();

        salonGUI = SalonGUI.getSalonGUI();
        salon = salonGUI.getSalon();

        //description
        JPanel dp = new JPanel();
        dp.add(new JLabel("Description: "));
        descriptionText = new JTextField("", 20);
        dp.add(descriptionText);
        mainPanel.add(dp);

        //currentPrice
        JPanel cpp = new JPanel();
        cpp.add(new JLabel("Price: "));
        currentPriceText = new JTextField("", 20);
        cpp.add(currentPriceText);
        mainPanel.add(cpp);

        //percentageToEmployee
        JPanel ptep = new JPanel();
        ptep.add(new JLabel("Percentage: "));
        percentageToEmployeeText = new JTextField("", 20);
        ptep.add(percentageToEmployeeText);
        mainPanel.add(ptep);

        //Relevant
        JPanel ap = new JPanel();
        ap.add(new JLabel("State: "));
        JCheckBox cb = new JCheckBox("relevant");
        state = true;
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
        doneButton.addActionListener(aeDone -> {addServiceType();});
        mainPanel.add(doneButton);

        mainPanel.updateUI();
    }

    public Dimension addPreferredSize()
    {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @SuppressWarnings( "deprecation" )
    private void addServiceType()
    {
        log.info("Adding new service type...");

        String description = "";
        double currentPrice = -1;
        float percentageToEmployee = -1;

        ServiceType st;

        try
        {
            //description
            description = Salon.checkName4Salon(descriptionText.getText());

            //currentPrice
            currentPrice = Double.parseDouble( currentPriceText.getText() );

            //percentageToEmployee
            percentageToEmployee = Float.parseFloat( percentageToEmployeeText.getText() );
            
            st = new ServiceType(description, currentPrice, percentageToEmployee, salon);
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
            log.error("Problem with adding service type: " + ignor);
            //ignor.printStackTrace();
            return;
        }
        

        //Relevant
        st.relevant(state);
        
        salon.addServiceType(st);

        try
        {
            HibHundler.initFactoryAndSession();
            HibHundler.saveObject(st);
            HibHundler.closeFactoryAndSession();
        }
        catch(/*Exception */Throwable ex)
        {
            log.error("Problem with save into DB service type: " + st + ": " + ex);
            //ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Problem with save into DB");
        }

        JOptionPane.showMessageDialog(this, "Service type (id=" + st.getID() + ") was saved in Data Base");

        salonGUI.refreshTablesOf_ServiceTypes();

        log.info("New service type added.");
    }
}