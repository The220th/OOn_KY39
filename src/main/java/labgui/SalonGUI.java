/*
Задание 21. Разработать ПК (программный комплекс) для администратора салона красоты. 
В ПК должна храниться информация о клиентах салона, мастерах с указанием их специализации, 
услугах и их ценах. 
Администратор может добавлять, изменять или удалять эту информацию. 
Ему могут понадобиться следующие сведения и возможности:
    • Просмотр расписания работы салона на выбранный день
    • Просмотр загруженности определённого мастера на день/неделю/месяц
    • Запись клиента на процедуру к определённому мастеру
    • Отчет о работе салона за неделю/месяц: 
        сколько клиентов было обслужено, 
        сколько денег пришло, 
        какие мастера самые востребованные 
        и какие – самые доходные
https://imgur.com/dPRseoY
*/

package labgui;

import java.net.URL;
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
 * Главное окно приложения
 * 
 */
public class SalonGUI
{
    private static final Logger log = Logger.getLogger(SalonGUI.class);

    private static SalonGUI salonGUI;
    private Salon salon;

    private static String[] employeesCol = {"ID", "Full name", "Gender", "Passport", "Birthday", "Specializations", "Active"};
    private static String[] clientsCol = {"ID", "Full name", "Gender", "Passport", "Birthday", "Priority", "BANNED"};
    private static String[] servicesCol = {"ID", "Service type", "Date", "Employee", "Employee\'s cut", "Client", "Price", "Relevance"};
    private static String[] serviceTypesCol = {"ID", "Description", "Price", "Percent, %", "Relevance"};

    private boolean DBLOADED;

    private JFrame mainFrame;
    private JButton saveButton;
    private JButton openButton;
    private JButton addButton;
    private JButton editButton;
    //private JButton deleteButton;
    private JButton taskButton;

    private JPanel bottomPanel;
    private JLabel bottomLabel;
    private JTextField bottomText;
    private JButton bottomButton;
    private JCheckBox consideredCheckBox;

    private JToolBar soaedToolBar;

    private JPanel currentTable;

    private DefaultTableModel employeeModel;
    private JTable employeeTable;
    private JScrollPane employeePane;

    private DefaultTableModel clientModel;
    private JTable clientTable;
    private JScrollPane clientPane;

    private DefaultTableModel serviceModel;
    private JTable serviceTable;
    private JScrollPane servicePane;

    private DefaultTableModel serviceTypeModel;
    private JTable serviceTypeTable;
    private JScrollPane serviceTypePane;

    private JMenuBar chooseTableMenuBar;
    private JMenu chooseTableMenu;

    private EditEmployee employeeEditor;
    private EditClient clientEditor;
    private EditService serviceEditor;
    private EditServiceType serviceTypeEditor;

    private AddEmployee employeeAdder;
    private AddClient clientAdder;
    private AddService serviceAdder;
    private AddServiceType serviceTypeAdder;

    private TaskHundler taskHundler;

    private JScrollPane curPane;

    private String filter;
    private String[] filters;
    //true = не выводить неактивные, забанненые, неактуальные
    private boolean CONSIDERED;

    private LoadingMessage loadLoading;
    private LoadingMessage saveLoading;

    /**
     * Запуск GUI
     * 
     */
    public static void start()
    {
        salonGUI = new SalonGUI();
        salonGUI.show();
    }

    private void show()
    {
        DBLOADED = false;

        log.info("Preparing main frame...");

        mainFrame = new JFrame("Frame of salon");
        mainFrame.setSize(1280, 720);
        mainFrame.setLocation(50, 51);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        openButton = new JButton(new ImageIcon(SalonGUI.class.getResource("/img/openButton.png")));
        openButton.setToolTipText("Open DB");
        openButton.addActionListener(event -> 
        {
            loadSalon();
            if(salon != null)
            {
                PrapareTables();
                curPane = /*curPane==null?*/employeePane/*:curPane*/;
                refresh(curPane);
                DBLOADED = true;
            }
        });

        saveButton = new JButton(new ImageIcon(SalonGUI.class.getResource("/img/saveButton.png")));
        saveButton.setToolTipText("Save into DB");
        saveButton.addActionListener(event -> {if(checkPrepare() == true) saveSalon();});

        addButton = new JButton(new ImageIcon(SalonGUI.class.getResource("/img/addButton.png")));
        addButton.setToolTipText("Add row");
        addButton.addActionListener(event -> {if(checkPrepare() == true) addItem(curPane);});

        editButton = new JButton(new ImageIcon(SalonGUI.class.getResource("/img/editButton.png")));
        editButton.setToolTipText("Edit row");
        editButton.addActionListener(event -> {if(checkPrepare() == true) editItem(curPane);});

        //deleteButton = new JButton(new ImageIcon(SalonGUI.class.getResource("/img/deleteButton.png")));
        //deleteButton.setToolTipText("Delete row");
        //deleteButton.addActionListener(event -> {System.out.println("TunA YDAJluJlOCb...");});

        taskButton = new JButton(new ImageIcon(SalonGUI.class.getResource("/img/taskButton.png")));
        taskButton.setToolTipText("Task");
        taskButton.addActionListener(event -> {if(checkPrepare() == true) doTask();});

        soaedToolBar = new JToolBar("Tool bar");
        soaedToolBar.add(openButton);
        soaedToolBar.add(saveButton);
        soaedToolBar.add(addButton);
        soaedToolBar.add(editButton);
        //soaedToolBar.add(deleteButton);
        soaedToolBar.add(taskButton);

        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(soaedToolBar, BorderLayout.NORTH);

        //PrapareTables();
        currentTable = new JPanel();
        mainFrame.add(currentTable, BorderLayout.CENTER);

        bottomLabel = new JLabel("Search: ");
        bottomText = new JTextField("", 25);
        bottomText.addActionListener(eve ->
        {
            if(checkPrepare() == true)
            {
                filter = bottomText.getText();
                filters = filter.split(" ");
                refreshTables();
            }
        });
        bottomButton = new JButton(new ImageIcon(SalonGUI.class.getResource("/img/searchButton.png")));
        bottomButton.setToolTipText("Find text");
        bottomButton.addActionListener(event -> 
                                    {
                                        if(checkPrepare() == true)
                                        {
                                            filter = bottomText.getText();
                                            filters = filter.split(" ");
                                            refreshTables();
                                            //System.out.println("TunA uWET...");
                                        }
                                    });
        consideredCheckBox = new JCheckBox("Considered");
        CONSIDERED = true;
        consideredCheckBox.setSelected(CONSIDERED);
        consideredCheckBox.addItemListener(new ItemListener() {    
            public void itemStateChanged(ItemEvent ie) {
                if(checkPrepare() == true)
                {
                    CONSIDERED = consideredCheckBox.isSelected();
                    refreshTables();
                }
            }
        });

        bottomPanel = new JPanel();
        bottomPanel.add(bottomLabel);
        bottomPanel.add(bottomText);
        bottomPanel.add(bottomButton);
        bottomPanel.add(consideredCheckBox);

        mainFrame.add(bottomPanel, BorderLayout.SOUTH);

        JMenuItem chooseEmployeesTable = new JMenuItem("Employees");
        chooseEmployeesTable.addActionListener(event -> 
        {
            if(checkPrepare() == true)
                {curPane = employeePane; refresh(curPane);}
        });

        JMenuItem chooseClientsTable = new JMenuItem("Clients");
        chooseClientsTable.addActionListener(event -> 
        {
            if(checkPrepare() == true)
                {curPane = clientPane; refresh(curPane);}
        });

        JMenuItem chooseServicesTable = new JMenuItem("Services");
        chooseServicesTable.addActionListener(event -> 
        {
            if(checkPrepare() == true)
                {curPane = servicePane; refresh(curPane);}
        });

        JMenuItem chooseServiceTypesTable = new JMenuItem("Service types");
        chooseServiceTypesTable.addActionListener(event -> 
        {
            if(checkPrepare() == true)
                {curPane = serviceTypePane; refresh(curPane);}
        });

        chooseTableMenu = new JMenu("Choose table");
        chooseTableMenu.add(chooseEmployeesTable);
        chooseTableMenu.add(chooseClientsTable);
        chooseTableMenu.add(chooseServicesTable);
        chooseTableMenu.add(chooseServiceTypesTable);

        chooseTableMenuBar = new JMenuBar();
        chooseTableMenuBar.add(chooseTableMenu);

        mainFrame.setJMenuBar(chooseTableMenuBar);

        loadLoading = new LoadingMessage(mainFrame, "Please wait while loading datas from DB...");
        saveLoading = new LoadingMessage(mainFrame, "Please wait while saving datas in DB...");

        mainFrame.setIconImage(new ImageIcon(SalonGUI.class.getResource("/img/icon.png")).getImage());

        log.info("Main frame was formed.");

	    mainFrame.setVisible(true);
    }

    private void PrapareTables()
    {
        log.info("Preparing tables...");

        initTablesOf_Employees();

        initTablesOf_Clients();

        initTablesOf_Services();

        initTablesOf_ServiceTypes();
        
        //currentTable = new JPanel();
        currentTable.setLayout(new BorderLayout());
        currentTable.add(employeePane);

        log.info("Table prepared.");
    }

    private boolean checkPrepare()
    {
        if(DBLOADED == true)
        {
            return true;
        }
        else
        {
            JOptionPane.showMessageDialog(mainFrame, "DB is not loaded. Please \"open\" database first.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    //==================================Employees=============================================================
    
    private void initTablesOf_Employees()
    {
        employeeModel = getEmployees_DefaultTableModel();

        employeeTable = new JTable(employeeModel);
        employeeTable.setAutoCreateRowSorter(true);
        employeeTable.setEnabled(true);
        DefaultTableCellRenderer r = (DefaultTableCellRenderer) employeeTable.getDefaultRenderer(Integer.class);
        r.setHorizontalAlignment(JLabel.LEFT);
        employeeTable.getTableHeader().setReorderingAllowed(false);
        employeePane = new JScrollPane(employeeTable);
    }

    /**
     * Обновляет таблицу сотрудников
     * 
     */
    public void refreshTablesOf_Employees()
    {
        employeeModel = getEmployees_DefaultTableModel();

        employeeTable.setModel(employeeModel);

        refresh(curPane);
    }

    private DefaultTableModel getEmployees_DefaultTableModel()
    {
        Object[][] employeesData = employees2Table(salon.getEmployees());
        DefaultTableModel employeeModel = new DefaultTableModel(employeesData, employeesCol)
        {
            @Override
            public boolean isCellEditable(int i, int i1) {
                return false;
            }

            @Override
            public Class getColumnClass(int column)
            {
                if(column == 0)
                    return Integer.class;
                else
                    return String.class;
            }
        };
        return employeeModel;
    }

    //==================================Clients=============================================================

    private void initTablesOf_Clients()
    {
        clientModel = getClients_DefaultTableModel();

        clientTable = new JTable(clientModel);
        clientTable.setAutoCreateRowSorter(true);
        clientTable.setEnabled(true);
        DefaultTableCellRenderer r = (DefaultTableCellRenderer) clientTable.getDefaultRenderer(Integer.class);
        r.setHorizontalAlignment(JLabel.LEFT);
        clientTable.getTableHeader().setReorderingAllowed(false);
        clientPane = new JScrollPane(clientTable);
    }

    /**
     * Обновляет таблицу клиентов
     * 
     */
    public void refreshTablesOf_Clients()
    {
        clientModel = getClients_DefaultTableModel();

        clientTable.setModel(clientModel);

        refresh(curPane);
    }

    private DefaultTableModel getClients_DefaultTableModel()
    {
        Object[][] clientsData = clients2Table(salon.getClients());
        DefaultTableModel clientModel = new DefaultTableModel(clientsData, clientsCol)
        {
            @Override
            public boolean isCellEditable(int i, int i1) {
                return false;
            }

            @Override
            public Class getColumnClass(int column)
            {
                if(column == 0 || column == 5)
                    return Integer.class;
                else
                    return String.class;
            }
        };
        return clientModel;
    }

    //==================================Services=============================================================

    private void initTablesOf_Services()
    {
        serviceModel = getServices_DefaultTableModel();
        
        serviceTable = new JTable(serviceModel);
        serviceTable.setAutoCreateRowSorter(true);
        serviceTable.setEnabled(true);
        DefaultTableCellRenderer r = (DefaultTableCellRenderer) serviceTable.getDefaultRenderer(Integer.class);
        r.setHorizontalAlignment(JLabel.LEFT);
        serviceTable.getTableHeader().setReorderingAllowed(false);
        servicePane = new JScrollPane(serviceTable);
    }

    /**
     * Обновляет таблицу сервисов
     * 
     */
    public void refreshTablesOf_Service()
    {
        serviceModel = getServices_DefaultTableModel();

        serviceTable.setModel(serviceModel);

        refresh(curPane);
    }

    private DefaultTableModel getServices_DefaultTableModel()
    {
        Object[][] servicesData = services2Table(salon.getDeals());
        DefaultTableModel serviceModel = new DefaultTableModel(servicesData, servicesCol)
        {
            @Override
            public boolean isCellEditable(int i, int i1) {
                return false;
            }

            @Override
            public Class getColumnClass(int column)
            {
                if(column == 0)
                    return Integer.class;
                else if(column == 4 || column == 6)
                    return Double.class;
                else
                    return String.class;
            }
        };
        return serviceModel;
    }

    //==================================ServiceTypes=============================================================

    private void initTablesOf_ServiceTypes()
    {
        serviceTypeModel = getServiceTypes_DefaultTableModel();

        serviceTypeTable = new JTable(serviceTypeModel);
        serviceTypeTable.setAutoCreateRowSorter(true);
        serviceTypeTable.setEnabled(true);
        DefaultTableCellRenderer r = (DefaultTableCellRenderer) serviceTypeTable.getDefaultRenderer(Integer.class);
        r.setHorizontalAlignment(JLabel.LEFT);
        serviceTypeTable.getTableHeader().setReorderingAllowed(false);
        serviceTypePane = new JScrollPane(serviceTypeTable);
    }

    /**
     * Обновляет таблицу типов сервисов
     * 
     */
    public void refreshTablesOf_ServiceTypes()
    {
        serviceTypeModel = getServiceTypes_DefaultTableModel();

        serviceTypeTable.setModel(serviceTypeModel);
        refresh(curPane);
    }

    private DefaultTableModel getServiceTypes_DefaultTableModel()
    {
        Object[][] serviceTypesData = serviceTypes2Table(salon.getProvidedServices());
        DefaultTableModel serviceTypeModel = new DefaultTableModel(serviceTypesData, serviceTypesCol)
        {
            @Override
            public boolean isCellEditable(int i, int i1) {
                return false;
            }

            @Override
            public Class getColumnClass(int column)
            {
                if(column == 0)
                    return Integer.class;
                else if(column == 2)
                    return Double.class;
                else if(column == 3)
                    return Float.class;
                else
                    return String.class;
            }
        };
        return serviceTypeModel;
    }

    //===============================================================================================

    private void refresh(JScrollPane cur)
    {
        currentTable.removeAll();
        currentTable.add(cur);
        currentTable.updateUI();
    }

    private void refreshTables()
    {
        if(curPane == employeePane)
        {
            refreshTablesOf_Employees();
        }
        else if(curPane == clientPane)
        {
            refreshTablesOf_Clients();
        }
        else if(curPane == servicePane)
        {
            refreshTablesOf_Service();
        }
        else if(curPane == serviceTypePane)
        {
            refreshTablesOf_ServiceTypes();
        }
    }

    private void loadSalon()
    {
        try
        {
            log.info("Trying to load salon from DB...");

            removeVisibleWindows();

            Runnable task = () ->
            {
                HibHundler.initFactoryAndSession();
                salon = HibHundler.loadSalon();
                //String toPrint = salon.toString();
                //System.out.println(toPrint);
                salon.initAllLazyRecords();
                HibHundler.closeFactoryAndSession();
                loadLoading.showLoad(false);
            };
            new Thread(task).start();

            loadLoading.showLoad(true);


            log.info("Loaded.");
        }
        catch(/*Exception */Throwable e)
        {
            log.error("Problem with load from DB: ", e);
            loadLoading.showLoad(false);
            //e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Problem with load from DB", "Error", JOptionPane.ERROR_MESSAGE);
        }

        /*Date fD = new Date(); fD.setYear(2005-1900); fD.setMonth(11); fD.setDate(1);
        Date eD = new Date(); eD.setYear(2005-1900); eD.setMonth(11); eD.setDate(31);
        for(int li = 0; li < 100; ++li) salon.genNewService(fD , eD);*/
    }

    private void loadSalon(int ignor)
    {
        log.info("Loading salon from GreareGenerator...");

        HibHundler.initFactoryAndSession();
        salon = new Salon("Salon Harry Dubua face");
        GreatGenerator.makeSalon(salon);
        HibHundler.saveSalon(salon);
        salon.initAllLazyRecords();
        //String toPrint = salon.toString();
        //System.out.println(toPrint);
        HibHundler.closeFactoryAndSession();

        log.info("Salon from GreareGenerator was loaded.");
    }

    private void saveSalon()
    {
        try
        {
            log.info("Trying to save salon in DB...");

            Runnable task = () ->
            {
                HibHundler.initFactoryAndSession();
                HibHundler.saveSalon(salon);
                HibHundler.closeFactoryAndSession();
                saveLoading.showLoad(false);
            };
            new Thread(task).start();

            saveLoading.showLoad(true);

            log.info("Salon saved in DB.");
        }
        catch(/*Exception */Throwable e)
        {
            log.error("Problem with save into DB: ", e);
            loadLoading.showLoad(false);
            //e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Problem with save into DB", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }

    private void removeVisibleWindows()
    {
        if(employeeEditor != null)
            employeeEditor.setVisible(false);
        if(clientEditor != null)
            clientEditor.setVisible(false);
        if(serviceEditor != null)
            serviceEditor.setVisible(false);
        if(serviceTypeEditor != null)
            serviceTypeEditor.setVisible(false);
        
        if(employeeAdder != null)
            employeeAdder.setVisible(false);
        if(clientAdder != null)
            clientAdder.setVisible(false);
        if(serviceAdder != null)
            serviceAdder.setVisible(false);
        if(serviceTypeAdder != null)
            serviceTypeAdder.setVisible(false);
    }

    private Object[][] employees2Table(Set<Employee> emps)
    {
        int i;
        int num = 7;
        Object[][] res = new Object[emps.size()][];
        for(i = 0; i < res.length; ++i)
            res[i] = new Object[num];
        i = 0;
        for(Employee e : emps)
        {
            if(filtering(e) == true)
            {
                res[i][0] = Integer.valueOf(e.getID());
                res[i][1] = (e.getName());
                res[i][2] = (e.getGender()==true?"M":"F");
                res[i][3] = (e.getPassport());
                res[i][4] = (Salon.doOnlyDate(e.getBirthday()));

                Set<ServiceType> mst = e.getMasteredServices();
                String buffS = "" + mst.size() + ": ";
                for(ServiceType st : mst)
                    buffS += st.getID() + " ";
                res[i][5] = (buffS);

                res[i][6] = ("" + e.isActive());
            }
            else
                res[i] = null;
            ++i;
        }
        return removeNull(res, num);
    }

    private Object[][] clients2Table(Set<Client> clients)
    {
        int i;
        int num = 7;
        Object[][] res = new Object[clients.size()][];
        for(i = 0; i < res.length; ++i)
            res[i] = new Object[num];
        i = 0;
        for(Client c : clients)
        {
            if(filtering(c) == true)
            {
                res[i][0] = Integer.valueOf(c.getID());
                res[i][1] = c.getName();
                res[i][2] = c.getGender()==true?"M":"F";
                res[i][3] = c.getPassport();
                res[i][4] = Salon.doOnlyDate(c.getBirthday());

                res[i][5] = Integer.valueOf(c.getPriority());

                res[i][6] = "" + c.isBanned();
            }
            else
                res[i] = null;
            ++i;
        }
        return removeNull(res, num);
    }

    private Object[][] services2Table(Set<Service> ss)
    {
        int i;
        int num = 8;
        Object[][] res = new Object[ss.size()][];
        for(i = 0; i < res.length; ++i)
            res[i] = new Object[num];
        i = 0;
        for(Service s : ss)
        {
            if(filtering(s) == true)
            {
                res[i][0] = Integer.valueOf(s.getID());
                res[i][1] = s.getServiceType().getDescription() + " - " + s.getServiceType().getID();
                res[i][2] = Salon.doOnlyDate(s.getDateBegin());
                res[i][3] = s.getEmployee().getName() + " - " + s.getEmployee().getID();
                res[i][4] = Double.valueOf(s.getCashReward());
                res[i][5] = s.getClient().getName() + " - " + s.getClient().getID();
                res[i][6] = Double.valueOf(s.getPrice());
                
                res[i][7] = "" + s.isRELEVANT();
            }
            else
                res[i] = null;
            ++i;
        }
        return removeNull(res, num);
    }

    private Object[][] serviceTypes2Table(Set<ServiceType> sts)
    {
        int i;
        int num = 5;
        Object[][] res = new Object[sts.size()][];
        for(i = 0; i < res.length; ++i)
            res[i] = new Object[num];
        i = 0;
        for(ServiceType st : sts)
        {
            if(filtering(st) == true)
            {
                res[i][0] = Integer.valueOf(st.getID());
                res[i][1] = st.getDescription();
                res[i][2] = Double.valueOf(st.getCurrentPrice());
                res[i][3] = Float.valueOf(st.getPercent());

                res[i][4] = "" + st.isRelevant();
            }
            else
                res[i] = null;
            ++i;
        }
        return removeNull(res, num);
    }

    /**
     * Получить салон
     * 
     * @return салон
     */
    public Salon getSalon()
    {
        return salon;
    }

    /**
     * Получить salonGUI
     * 
     * @return salonGUI
     */
    public static SalonGUI getSalonGUI()
    {
        return salonGUI;
    }

    //true = показывать
    private boolean filtering(Object obj)
    {
        boolean FIRSTCATCH;
        String prop;
        if(obj.getClass() == Employee.class)
        {
            Employee e = (Employee)obj;
            if(CONSIDERED == false)
                FIRSTCATCH = true;
            else if(e.isActive() == false)
                FIRSTCATCH = false;
            else
                FIRSTCATCH = true;
            if(FIRSTCATCH == false)
                return false;
            else
            {
                prop = makeEntityStringProperties(e);
                return checkFilterAndProperties(prop, filters);
            }
        }
        else if(obj.getClass() == Client.class)
        {
            Client c = (Client)obj;
            if(CONSIDERED == false)
                FIRSTCATCH = true;
            else if(c.isBanned() == true)
                FIRSTCATCH = false;
            else
                FIRSTCATCH = true;
            if(FIRSTCATCH == false)
                return false;
            else
            {
                prop = makeEntityStringProperties(c);
                return checkFilterAndProperties(prop, filters);
            }
        }
        else if(obj.getClass() == Service.class)
        {
            Service s = (Service)obj;
            if(CONSIDERED == false)
                FIRSTCATCH = true;
            else if(s.isRELEVANT() == false)
                FIRSTCATCH = false;
            else
                FIRSTCATCH = true;
            if(FIRSTCATCH == false)
                return false;
            else
            {
                prop = makeEntityStringProperties(s);
                return checkFilterAndProperties(prop, filters);
            }
        }
        else if(obj.getClass() == ServiceType.class)
        {
            ServiceType st = (ServiceType)obj;
            if(CONSIDERED == false)
                FIRSTCATCH = true;
            else if(st.isRelevant() == false)
                FIRSTCATCH = false;
            else
                FIRSTCATCH = true;
            if(FIRSTCATCH == false)
                return false;
            else
            {
                prop = makeEntityStringProperties(st);
                return checkFilterAndProperties(prop, filters);
            }
        }
        else
        {
            log.warn("Error in filtering: obj = " + obj);
            //System.out.println("Error in filtering: obj = " + obj);
            return false;
        }
    }

    //Совпадение = true
    private boolean checkFilterAndProperties(String prop, String[] filters)
    {
        int i;
        if(filter == null || filter.trim().equals(""))
            return true;
        for(i = 0; i < filters.length; ++i)
            if(prop.contains(filters[i]))
                return true;
        return false;
    }

    private static String makeEntityStringProperties(Object obj)
    {
        String res;
        if(obj.getClass() == Employee.class)
        {
            Employee e = (Employee)obj;
            res = e.getID() + " " + e.getName() + " " + e.getGender() + " " + (e.getGender()==true?"M":"F") + " " + e.getPassport() + " " + e.getBirthday() + " " + Salon.doNiceDate(e.getBirthday()) + " ";
            Set<ServiceType> sts = e.getMasteredServices();
            for(ServiceType st : sts)
                res += st.getID() + " ";
            return res;
        }
        else if(obj.getClass() == Client.class)
        {
            Client c = (Client)obj;
            res = c.getID() + " " + c.getName() + " " + c.getGender() + " " + (c.getGender()==true?"M":"F") + " " + c.getPassport() + " " + c.getBirthday() + " " + Salon.doNiceDate(c.getBirthday()) + " ";
            res += c.getPriority();
            return res;
        }
        else if(obj.getClass() == Service.class)
        {
            Service s = (Service)obj;
            res = s.getID() + " " + s.getDateBegin() + " " + Salon.doNiceDate(s.getDateBegin()) + " " + s.getPrice() + " " + s.getCashReward() + " " + s.getServiceType().getDescription() + " - " + s.getServiceType().getID() + " " + s.getEmployee().getName() + " - " + s.getEmployee().getID() + " " + s.getClient().getName() + " - " + s.getClient().getID();
            return res;
        }
        else if(obj.getClass() == ServiceType.class)
        {
            ServiceType st = (ServiceType)obj;
            res = st.getID() + " " + st.getDescription() + " " + st.getCurrentPrice() + " " + st.getPercent();
            return res;
        }
        else
        {
            log.warn("Error in makeEntityStringProperties: obj = " + obj);
            //System.out.println("Error in makeEntityStringProperties: obj = " + obj);
            return null;
        }
    }

    private static Object[][] removeNull(Object[][] o, int m)
    {
        int i, j;
        int countNull;
        countNull = 0;
        for(i = 0; i < o.length; ++i)
            if(o[i] != null)
                ++countNull;
        Object[][] res = new Object[countNull][];
        for(i = 0; i < res.length; ++i)
            res[i] = new Object[m];
        for(i = 0, j = 0; i < o.length; ++i)
            if(o[i] != null)
                res[j++] = o[i];
        return res;
    }

    private void editItem(JScrollPane cur)
    {
        log.info("Edit button choosed.");

        int selRow = -1;
        int selID = -1;

        if(cur == employeePane)
        {
            log.info("Editing employees choosed.");

            selRow = employeeTable.getSelectedRow();
            if(selRow == -1)
            {
                JOptionPane.showMessageDialog(mainFrame, "First select the row");
                return;
            }
            selID = Integer.parseInt(employeeTable.getValueAt(selRow, 0).toString());

            log.info("Editing employee " + employeeTable.getValueAt(selRow, 1).toString() + " - " + selID + ".");

            if(employeeEditor == null)
                employeeEditor = new EditEmployee(mainFrame, salon.getEmployeeByID(selID));
            else
                employeeEditor.updateEdit(salon.getEmployeeByID(selID));
            employeeEditor.setVisible(true);
        }
        else if(cur == clientPane)
        {   
            log.info("Editing clients choosed.");

            selRow = clientTable.getSelectedRow();
            if(selRow == -1)
            {
                JOptionPane.showMessageDialog(mainFrame, "First select the row");
                return;
            }
            selID = Integer.parseInt(clientTable.getValueAt(selRow, 0).toString());

            log.info("Editing client " + clientTable.getValueAt(selRow, 1).toString() + " - " + selID + ".");

            if(clientEditor == null)
                clientEditor = new EditClient(mainFrame, salon.getClientByID(selID));
            else
                clientEditor.updateEdit(salon.getClientByID(selID));
            clientEditor.setVisible(true);
        }
        else if(cur == servicePane)
        {
            log.info("Editing services choosed.");

            selRow = serviceTable.getSelectedRow();
            if(selRow == -1)
            {
                JOptionPane.showMessageDialog(mainFrame, "First select the row");
                return;
            }
            selID = Integer.parseInt(serviceTable.getValueAt(selRow, 0).toString());

            log.info("Editing service with ID = " + selID + ".");

            if(serviceEditor == null)
                serviceEditor = new EditService(mainFrame, salon.getServiceByID(selID));
            else
                serviceEditor.updateEdit(salon.getServiceByID(selID));
            serviceEditor.setVisible(true);
        }
        else if(cur == serviceTypePane)
        {
            log.info("Editing service types choosed.");

            selRow = serviceTypeTable.getSelectedRow();
            if(selRow == -1)
            {
                JOptionPane.showMessageDialog(mainFrame, "First select the row");
                return;
            }
            selID = Integer.parseInt(serviceTypeTable.getValueAt(selRow, 0).toString());

            log.info("Editing service type: " + serviceTypeTable.getValueAt(selRow, 1).toString() + " - " + selID + ".");

            if(serviceTypeEditor == null)
                serviceTypeEditor = new EditServiceType(mainFrame, salon.getServiceTypeByID(selID));
            else
                serviceTypeEditor.updateEdit(salon.getServiceTypeByID(selID));
            serviceTypeEditor.setVisible(true);
        }
        else
        {
            log.warn("Error in editItem: cur = " + cur);
            //System.out.println("Error in editItem: cur = " + cur);
        }
        //System.out.println("TunA u3MEHuJlOCb...");
    }

    private void addItem(JScrollPane cur)
    {
        log.info("Add button choosed.");

        if(cur == employeePane)
        {
            log.info("Adding employee.");

            if(employeeAdder == null)
                employeeAdder = new AddEmployee(mainFrame);
            else
                employeeAdder.updateAdd();
            employeeAdder.setVisible(true);

            log.info("Employee added.");
        }
        else if(cur == clientPane)
        {
            log.info("Adding client.");

            if(clientAdder == null)
                clientAdder = new AddClient(mainFrame);
            else
                clientAdder.updateAdd();
            clientAdder.setVisible(true);

            log.info("Client added.");
        }
        else if(cur == servicePane)
        {
            log.info("Adding service.");

            if(serviceAdder == null)
                serviceAdder = new AddService(mainFrame);
            else
                serviceAdder.updateAdd();
            serviceAdder.setVisible(true);

            log.info("Service added.");
        }
        else if(cur == serviceTypePane)
        {
            log.info("Adding service type.");

            if(serviceTypeAdder == null)
                serviceTypeAdder = new AddServiceType(mainFrame);
            else
                serviceTypeAdder.updateAdd();
            serviceTypeAdder.setVisible(true);

            log.info("Service type added.");
        }
        else
        {
            log.warn("Error in addItem: cur = " + cur);
            //System.out.println("Error in addItem: cur = " + cur);
        }
        //System.out.println("TunA DO6ABuJlOCb...");
    }

    private void doTask()
    {
        log.info("Task button choosed.");

        if(taskHundler == null)
            taskHundler = new TaskHundler(mainFrame);
        else
            taskHundler.update();
        taskHundler.setVisible(true);
    }
}
