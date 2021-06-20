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
 * Форма для добавления сервиса
 * 
 */
class AddService extends JDialog
{
    private static final Logger log = Logger.getLogger(AddService.class);

    private SalonGUI salonGUI;
    private Salon salon;

    private final int DEFAULT_WIDTH = 500;
    private final int DEFAULT_HEIGHT = 700;

    private JPanel mainPanel;
    private JButton doneButton;

    private JTextField clientCostText;
    private JTextField employeeSalaryText;
    private DateChooser dateChooser;

    private int selectedServiceType;
    private int selectedEmployee;
    private int selectedClient;

    private boolean state;

    public AddService(JFrame owner)
    {
        super(owner, "Add specialization", false);

        log.info("Creating new AddService frame...");

        mainPanel = new JPanel();
        /*
        what: [combo]
        whom: [combo]
        who: [combo]
        cost: []
        salary: []
        date: []
        REL: *
        [done]
        */
        mainPanel.setLayout(new GridLayout(8, 1, 15, 15));
        this.add(mainPanel);
        updateAdd();

        this.setLocationRelativeTo(owner);
        pack();

        log.info("AddService frame was created.");
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

        selectedServiceType = -1;
        selectedClient = -1;
        selectedEmployee = -1;
        clientCostText = new JTextField("", 20);
        employeeSalaryText = new JTextField("", 20);
        //==creatPlate==
        int i;

        //ServiceTypes
        JPanel stsp = new JPanel();
        Set<ServiceType> sts = salon.getProvidedServices();
        String[] serviceTypesCombo = new String[sts.size()];
        i = 0;
        for(ServiceType item : sts)
        {
            serviceTypesCombo[i] = item.getDescription() + " - " + item.getID();
            ++i;
        }
        Arrays.sort(serviceTypesCombo, (String a, String b) -> {return a.compareTo(b);});
        JComboBox serviceTypesComboBox = new JComboBox<String>(serviceTypesCombo);
        ActionListener serviceTypeActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox)e.getSource();
                selectedServiceType = takeIDfromCombo((String)box.getSelectedItem());
                clientCostText.setText("" + salon.getServiceTypeByID(selectedServiceType).getCurrentPrice());
                setEmployeeSalaryText();
            }
        };
        serviceTypesComboBox.addActionListener(serviceTypeActionListener);
        stsp.add(new JLabel("What: "));
        stsp.add(serviceTypesComboBox);
        mainPanel.add(stsp);

        //Clients
        JPanel csp = new JPanel();
        Set<Client> cs = salon.getClients();
        String[] clientsCombo = new String[cs.size()];
        i = 0;
        for(Client item : cs)
        {
            clientsCombo[i] = item.getName() + " - " + item.getID();
            ++i;
        }
        Arrays.sort(clientsCombo, (String a, String b) -> {return a.compareTo(b);});
        JComboBox clientsComboBox = new JComboBox<String>(clientsCombo);
        ActionListener clientActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox)e.getSource();
                selectedClient = takeIDfromCombo((String)box.getSelectedItem());
            }
        };
        clientsComboBox.addActionListener(clientActionListener);
        csp.add(new JLabel("Whom: "));
        csp.add(clientsComboBox);
        mainPanel.add(csp);

        //Employees
        JPanel esp = new JPanel();
        Set<Employee> es = salon.getEmployees();
        String[] employeesCombo = new String[es.size()];
        i = 0;
        for(Employee item : es)
        {
            employeesCombo[i] = item.getName() + " - " + item.getID();
            ++i;
        }
        Arrays.sort(employeesCombo, (String a, String b) -> {return a.compareTo(b);});
        JComboBox employeesComboBox = new JComboBox<String>(employeesCombo);
        ActionListener employeeActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox)e.getSource();
                selectedEmployee = takeIDfromCombo((String)box.getSelectedItem());
            }
        };
        employeesComboBox.addActionListener(employeeActionListener);
        esp.add(new JLabel("Who: "));
        esp.add(employeesComboBox);
        mainPanel.add(esp);


        //cost
        JPanel costp = new JPanel();
        costp.add(new JLabel("Client cost: "));
        clientCostText.addActionListener(eve ->
        {
            setEmployeeSalaryText();
        });
        costp.add(clientCostText);
        mainPanel.add(costp);

        //salary
        JPanel sap = new JPanel();
        sap.add(new JLabel("Employee\'s cut: "));
        sap.add(employeeSalaryText);
        mainPanel.add(sap);

        
        //date
        JPanel datep = new JPanel();
        datep.add(new JLabel("Date: "));
        dateChooser = new DateChooser();
        datep.add(dateChooser);
        mainPanel.add(datep);

        //====

        //REL
        JPanel rp = new JPanel();
        rp.add(new JLabel("State: "));
        JCheckBox cb = new JCheckBox("relevant");
        state = true;
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
        doneButton.addActionListener(aeDone -> {addService();});
        mainPanel.add(doneButton);

        mainPanel.updateUI();
    }

    private static int takeIDfromCombo(String s)
    {
        int res;
        int i = s.lastIndexOf(" - ");
        String buffS = s.substring(i + 3, s.length());
        res = Integer.parseInt(buffS);
        return res;
    }

    private void setEmployeeSalaryText()
    {
        if(selectedServiceType != -1)
        {
            try
            {
                double d = Double.parseDouble(clientCostText.getText());
                float percent = salon.getServiceTypeByID(selectedServiceType).getPercent();
                employeeSalaryText.setText("" + d*percent*0.01);
            }
            catch(Exception ignor)
            {
            }
        }

    }

    public Dimension getPreferredSize()
    {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @SuppressWarnings( "deprecation" )
    private void addService()
    {
        log.info("Adding new service...");

        if(selectedServiceType == -1 || selectedClient == -1 || selectedEmployee == -1)
        {
            JOptionPane.showMessageDialog(this, "Please fill in all the fields.");
            return;
        }

        ServiceType st = null;
        Client c = null;
        Employee e = null;
        double clientCosts = -1;
        double employeeSalary = -1;
        Date db = new Date();

        Service s;

        try
        {
            st = salon.getServiceTypeByID(selectedServiceType);
            c = salon.getClientByID(selectedClient);
            e = salon.getEmployeeByID(selectedEmployee);
            clientCosts = Double.parseDouble(clientCostText.getText());
            employeeSalary = Double.parseDouble(employeeSalaryText.getText());

            //date
            db = dateChooser.getDate();

            s = new Service(st, db, e, employeeSalary, c, clientCosts, salon);
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
            log.error("Problem with adding service: " + ignor);
            //ignor.printStackTrace();
            return;
        }

        //REL
        s.setRELEVANT(state);

        salon.addService(s);

        try
        {
            HibHundler.initFactoryAndSession();
            HibHundler.saveObject(s);
            HibHundler.closeFactoryAndSession();
        }
        catch(/*Exception */Throwable ex)
        {
            log.error("Problem with save into DB service: " + s + ": " + ex);
            //ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Problem with save into DB");
        }

        JOptionPane.showMessageDialog(this, "Service (id=" + s.getID() + ") was saved in Data Base");

        salonGUI.refreshTablesOf_Service();

        log.info("New service added.");
    }
}