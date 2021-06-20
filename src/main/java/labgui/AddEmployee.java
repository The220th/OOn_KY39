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
 * Форма для добавления сотрудника
 * 
 */
class AddEmployee extends JDialog
{
    private static final Logger log = Logger.getLogger(AddEmployee.class);

    private SalonGUI salonGUI;
    private Salon salon;

    private final int DEFAULT_WIDTH = 500;
    private final int DEFAULT_HEIGHT = 700;

    private JPanel mainPanel;
    private JButton doneButton;

    private JTextField firstNameText;
    private JTextField secondNameText;
    private DateChooser dateChooser;
    private JTextField passportText;
    private boolean gender;
    private boolean state;
    private JTextField permittedServicesText;

    public AddEmployee(JFrame owner)
    {
        super(owner, "Add employee", false);

        log.info("Creating new AddEmployee frame...");

        mainPanel = new JPanel();
        /*
        firstName: []
        secondName: []
        birthday: []
        passport: []
        gender: * *
        ACTIVE: *
        permittedServices: []
        [done]
        */
        mainPanel.setLayout(new GridLayout(8, 0, 15, 15));
        this.add(mainPanel);
        updateAdd();

        this.setLocationRelativeTo(owner);
        pack();

        log.info("AddEmployee frame was created.");
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

        //ACTIVE
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

        //permittedServices
        JPanel psp = new JPanel();
        psp.add(new JLabel("Permitted services: "));
        permittedServicesText = new JTextField("", 15);
        psp.add(permittedServicesText);
        mainPanel.add(psp);

        //Button
        doneButton = new JButton("Done!");
        doneButton.addActionListener(aeDone -> {addEmployee();});
        mainPanel.add(doneButton);

        mainPanel.updateUI();
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @SuppressWarnings( "deprecation" )
    private void addEmployee() 
    {
        log.info("Adding new employee...");

        String firstName = "";
        String secondName = "";
        Date d = new Date();
        String passport = "";
        String[] buffSs = null;
        Set<ServiceType> sts = null;

        Employee e;

        try
        {
            //names
            firstName = Salon.checkName4Salon(firstNameText.getText());
            secondName = Salon.checkName4Salon(secondNameText.getText());

            //birthday
            d = dateChooser.getDate();

            //passport
            passport = Salon.checkName4Salon(passportText.getText());

            //permittedServices
            sts = new HashSet<ServiceType>();
            if(!permittedServicesText.getText().trim().equals(""))
            {
                buffSs = permittedServicesText.getText().trim().split(" ");
                int[] pss = new int[buffSs.length];
                for(int i = 0; i < pss.length; ++i)
                    pss[i] = Integer.parseInt(buffSs[i]);
                for(int i = 0; i < pss.length; ++i)
                {
                    ServiceType buffST = salon.getServiceTypeByID(pss[i]);
                    if(buffST == null)
                        throw new NumberFormatException("" + pss[i]);
                    sts.add(buffST);
                }
            }

            e = new Employee(firstName, secondName, gender, passport, d, sts, salon);
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
            log.error("Problem with adding employee: " + ignor);
            //ignor.printStackTrace();
            return;
        }
        
        //ACTIVE
        e.active(state);

        salon.addEmployee(e);

        try
        {
            HibHundler.initFactoryAndSession();
            HibHundler.saveObject(e);
            HibHundler.closeFactoryAndSession();
        }
        catch(/*Exception */Throwable ex)
        {
            log.error("Problem with save into DB employee: " + e + ": " + ex);
            //ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Problem with save into DB");
            return;
        }

        JOptionPane.showMessageDialog(this, "Employee (id=" + e.getID() + ") was saved in Data Base");

        salonGUI.refreshTablesOf_Employees();

        log.info("New employee added.");
    }
}