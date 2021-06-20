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
 * Форма для редактирования сотрудника
 * 
 */
class EditEmployee extends JDialog
{
    private static final Logger log = Logger.getLogger(EditEmployee.class);

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
    private JTextField permittedServicesText;

    public EditEmployee(JFrame owner, Employee e)
    {
        super(owner, "Edit employee", false);

        log.info("Creating new EditEmployee frame...");

        mainPanel = new JPanel();
        /*
        id
        firstName: []
        secondName: []
        birthday: []
        passport: []
        gender: * *
        ACTIVE: *
        permittedServices: []
        [done]
        */
        mainPanel.setLayout(new GridLayout(9, 0, 15, 15));
        this.add(mainPanel);
        updateEdit(e);

        this.setLocationRelativeTo(owner);
        pack();

        log.info("EditEmployee frame was created.");
    }

    /**
     * Обновить содержимое формы
     * 
     * @param e сотрудник, для которого обновляется
     */
    public void updateEdit(Employee e)
    {
        mainPanel.removeAll();

        salonGUI = SalonGUI.getSalonGUI();
        salon = salonGUI.getSalon();

        //id
        JPanel ip = new JPanel();
        ip.add(new JLabel("ID is " + e.getID()));
        mainPanel.add(ip);

        //firstName
        JPanel fnp = new JPanel();
        fnp.add(new JLabel("First name: "));
        firstNameText = new JTextField(e.getFirstName(), 20);
        fnp.add(firstNameText);
        mainPanel.add(fnp);

        //secondName
        JPanel cnp = new JPanel();
        cnp.add(new JLabel("Second name: "));
        secondNameText = new JTextField(e.getSecondName(), 20);
        cnp.add(secondNameText);
        mainPanel.add(cnp);

        //birthday
        JPanel bp = new JPanel();
        bp.add(new JLabel("Birthday: "));
        dateChooser = new DateChooser();
        dateChooser.setDate(e.getBirthday());
        bp.add(dateChooser);
        mainPanel.add(bp);

        //passport
        JPanel pp = new JPanel();
        pp.add(new JLabel("Passport: "));
        passportText = new JTextField(e.getPassport(), 20);
        pp.add(passportText);
        mainPanel.add(pp);

        //gender
        JPanel gp = new JPanel();

        gender = e.getGender();
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

        //ACTIVE
        JPanel ap = new JPanel();
        ap.add(new JLabel("State: "));
        JCheckBox cb = new JCheckBox("relevant");
        state = e.isActive();
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
        Set<ServiceType> mst = e.getMasteredServices();
        String buffS = "";
        for(ServiceType st : mst)
            buffS += st.getID() + " ";
        permittedServicesText = new JTextField(buffS, 15);
        psp.add(permittedServicesText);
        mainPanel.add(psp);

        //Button
        doneButton = new JButton("Done!");
        doneButton.addActionListener(aeDone -> {changeEmployee(e);});
        mainPanel.add(doneButton);

        mainPanel.updateUI();
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @SuppressWarnings( "deprecation" )
    private void changeEmployee(Employee e) 
    {
        try
        {
            log.info("Trying to edit employee...");

            //names
            e.setName(Salon.checkName4Salon(firstNameText.getText()), Salon.checkName4Salon(secondNameText.getText()));

            //birthday
            Date d = dateChooser.getDate();
            e.setBirthday(d);

            //passport
            e.setPassport(Salon.checkName4Salon(passportText.getText()));

            //gender
            e.setGender(gender);

            //ACTIVE
            e.active(state);

            //permittedServices
            String[] buffSs;
            if(!permittedServicesText.getText().trim().equals(""))
            {
                buffSs = permittedServicesText.getText().trim().split(" ");
                int[] pss = new int[buffSs.length];
                for(int i = 0; i < pss.length; ++i)
                    pss[i] = Integer.parseInt(buffSs[i]);
                e.forbidAllServices();
                for(int i = 0; i < pss.length; ++i)
                {
                	ServiceType buffST = salon.getServiceTypeByID(pss[i]);
                	if(buffST == null)
                		throw new NumberFormatException("" + pss[i]);
                    e.addService(buffST);
                }
            }
            else
                e.forbidAllServices();
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
            log.error("Problem with editing employee: " + e + ": " + ignor);
            //ignor.printStackTrace();
            return;
        }
        
        salonGUI.refreshTablesOf_Employees();

        log.info("Employee was edited.");
    }
}