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
 * Форма для редактирования клиента
 * 
 */
class EditClient extends JDialog
{
    private static final Logger log = Logger.getLogger(EditClient.class);

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


    public EditClient(JFrame owner, Client c)
    {
        super(owner, "Edit client", false);

        log.info("Creating new EditClient frame...");

        mainPanel = new JPanel();
        /*
        id
        firstName: []
        secondName: []
        birthday: []
        passport: []
        gender: * *
        BANNED: *
        priority: []
        [done]
        */
        mainPanel.setLayout(new GridLayout(9, 0, 15, 15));
        this.add(mainPanel);
        updateEdit(c);

        this.setLocationRelativeTo(owner);
        pack();

        log.info("EditClient frame was created.");
    }

    /**
     * Обновить содержимое формы
     * 
     * @param c клиент, для которого обновляется
     */
    public void updateEdit(Client c)
    {
        mainPanel.removeAll();

        salonGUI = SalonGUI.getSalonGUI();
        salon = salonGUI.getSalon();

        //id
        JPanel ip = new JPanel();
        ip.add(new JLabel("ID is " + c.getID()));
        mainPanel.add(ip);

        //firstName
        JPanel fnp = new JPanel();
        fnp.add(new JLabel("First name: "));
        firstNameText = new JTextField(c.getFirstName(), 20);
        fnp.add(firstNameText);
        mainPanel.add(fnp);

        //secondName
        JPanel cnp = new JPanel();
        cnp.add(new JLabel("Second name: "));
        secondNameText = new JTextField(c.getSecondName(), 20);
        cnp.add(secondNameText);
        mainPanel.add(cnp);

        //birthday
        JPanel bp = new JPanel();
        bp.add(new JLabel("Birthday: "));
        dateChooser = new DateChooser();
        dateChooser.setDate(c.getBirthday());
        bp.add(dateChooser);
        mainPanel.add(bp);

        //passport
        JPanel pp = new JPanel();
        pp.add(new JLabel("Passport: "));
        passportText = new JTextField(c.getPassport(), 20);
        pp.add(passportText);
        mainPanel.add(pp);

        //gender
        JPanel gp = new JPanel();

        gender = c.getGender();
        JPanel gender4Ratio = new JPanel();
        JRadioButton genderMaleRatio = new JRadioButton("Male");
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
        if(gender == true)
            genderMaleRatio.setSelected(true);
        else
            genderFemaleRatio.setSelected(true);

        gp.add(new JLabel("Gender: "));
        gp.add(gender4Ratio);
        mainPanel.add(gp);

        //BANNED
        JPanel ap = new JPanel();
        ap.add(new JLabel("Banned: "));
        JCheckBox cb = new JCheckBox("is banned?");
        state = c.isBanned();
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
        priorityText = new JTextField("" + c.getPriority(), 20);
        prp.add(priorityText);
        mainPanel.add(prp);

        //Button
        doneButton = new JButton("Done!");
        doneButton.addActionListener(aeDone -> {changeClient(c);});
        mainPanel.add(doneButton);

        mainPanel.updateUI();
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @SuppressWarnings( "deprecation" )
    private void changeClient(Client c)
    {
        try
        {
            log.info("Trying to edit client...");

            //names
            c.setName(Salon.checkName4Salon(firstNameText.getText()), Salon.checkName4Salon(secondNameText.getText()));

            //birthday
            Date d = dateChooser.getDate();
            c.setBirthday(d);

            //passport
            c.setPassport(Salon.checkName4Salon(passportText.getText()));

            //gender
            c.setGender(gender);

            //BANNED
            c.ban(state);

            //priority
            c.setPriority( Integer.parseInt( priorityText.getText() ) );
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
            log.error("Problem with editing client: " + c + ": " + ignor);
            //ignor.printStackTrace();
            return;
        }
        
        salonGUI.refreshTablesOf_Clients();

        log.info("Client was edited.");
    }
}