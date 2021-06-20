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
 * Форма для добавления клиента
 * 
 */
class AddClient extends JDialog
{
    private static final Logger log = Logger.getLogger(AddClient.class);

    private SalonGUI salonGUI;
    private Salon salon;

    private final int DEFAULT_WIDTH = 500;
    private final int DEFAULT_HEIGHT = 750;

    private JPanel mainPanel;
    private JButton doneButton;

    private JTextField firstNameText;
    private JTextField secondNameText;
    private DateChooser dateChooser;
    private JTextField passportText;
    private boolean gender;
    private boolean state;
    private JTextField priorityText;

    public AddClient(JFrame owner)
    {
        super(owner, "Add client", false);

        log.info("Creating new AddClient frame...");

        mainPanel = new JPanel();
        /*
        firstName: []
        secondName: []
        birthday: []
        passport: []
        gender: * *
        BANNED: *
        priority: []
        [done]
        */
        mainPanel.setLayout(new GridLayout(8, 0, 15, 15));
        this.add(mainPanel);
        updateAdd();

        this.setLocationRelativeTo(owner);
        pack();

        log.info("AddClient frame was created.");
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

        //firstName
        JPanel fnp = new JPanel();
        fnp.add(new JLabel("First name: "));
        firstNameText = new JTextField("", 20);
        fnp.add(firstNameText);
        mainPanel.add(fnp);

        //secondName
        JPanel cnp = new JPanel();
        cnp.add(new JLabel("Second name: "));
        secondNameText = new JTextField("", 20);
        cnp.add(secondNameText);
        mainPanel.add(cnp);

        //birthday
        JPanel bp = new JPanel();
        bp.add(new JLabel("Birthday: "));
        dateChooser = new DateChooser();
        bp.add(dateChooser);
        mainPanel.add(bp);

        //passport
        JPanel pp = new JPanel();
        pp.add(new JLabel("Passport: "));
        passportText = new JTextField("", 20);
        pp.add(passportText);
        mainPanel.add(pp);

        //gender
        JPanel gp = new JPanel();

        gender = true;
        JPanel gender4Ratio = new JPanel();
        JRadioButton genderMaleRatio = new JRadioButton("Male");
        genderMaleRatio.setSelected(true);
        JRadioButton genderFemaleRatio = new JRadioButton("Female");
        ActionListener genderActionListener = ae ->
        {
            if(genderMaleRatio.isSelected())
                gender = true;
            else if(genderFemaleRatio.isSelected())
                gender = false;
        };
        genderMaleRatio.addActionListener(genderActionListener);
        genderFemaleRatio.addActionListener(genderActionListener);
        ButtonGroup genderRatioGroup = new ButtonGroup();
        genderRatioGroup.add(genderMaleRatio);
        genderRatioGroup.add(genderFemaleRatio);
        gender4Ratio.add(genderMaleRatio);
        gender4Ratio.add(genderFemaleRatio);

        gp.add(new JLabel("Gender: "));
        gp.add(gender4Ratio);
        mainPanel.add(gp);

        //BANNED
        JPanel ap = new JPanel();
        ap.add(new JLabel("Banned: "));
        JCheckBox cb = new JCheckBox("is banned?");
        state = false;
        cb.setSelected(state);
        cb.addItemListener(new ItemListener() {    
            public void itemStateChanged(ItemEvent ie) {
                state = cb.isSelected();
            }
        });
        ap.add(cb);
        mainPanel.add(ap);

        //priority
        JPanel prp = new JPanel();
        prp.add(new JLabel("Priority: "));
        priorityText = new JTextField("", 20);
        prp.add(priorityText);
        mainPanel.add(prp);

        //Button
        doneButton = new JButton("Done!");
        doneButton.addActionListener(aeDone -> {addClient();});
        mainPanel.add(doneButton);

        mainPanel.updateUI();
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @SuppressWarnings( "deprecation" )
    private void addClient()
    {
        log.info("Adding new client...");

        String firstName = "";
        String secondName = "";
        Date d = new Date();
        String passport = "";
        int priority = -1;

        Client c;

        try
        {
            //names
            firstName = Salon.checkName4Salon(firstNameText.getText());
            secondName = Salon.checkName4Salon(secondNameText.getText());

            //birthday
            d = dateChooser.getDate();

            //passport
            passport = Salon.checkName4Salon(passportText.getText());

            //priority
            priority = Integer.parseInt( priorityText.getText() );
            
            c = new Client(firstName, secondName, gender, passport, d, priority, salon);
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
            log.error("Problem with adding client: " + ignor);
            //ignor.printStackTrace();
            return;
        }

        //BANNED
        c.ban(state);

        salon.addClient(c);

        try
        {
            HibHundler.initFactoryAndSession();
            HibHundler.saveObject(c);
            HibHundler.closeFactoryAndSession();
        }
        catch(/*Exception */Throwable ex)
        {
            log.error("Problem with save into DB client: " + c + ": " + ex);
            //ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Problem with save into DB");
            return;
        }

        JOptionPane.showMessageDialog(this, "Client (id=" + c.getID() + ") was saved in Data Base");
        
        salonGUI.refreshTablesOf_Clients();

        log.info("New client added.");
    }
}