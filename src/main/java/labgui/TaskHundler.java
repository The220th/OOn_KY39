package labgui;

import java.lang.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.io.File;


import lab.*;
import labgui.*;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * Форма для составления статистики работы салона красоты и для составления отчётов
 * 
 */
class TaskHundler extends JDialog
{
    private static final Logger log = Logger.getLogger(TaskHundler.class);

    private final int DEFAULT_WIDTH = 1280;
    private final int DEFAULT_HEIGHT = 720;

    private SalonGUI salonGUI;
    private Salon salon;

    private JPanel mainPanel;
    private JPanel tablePanel;
    private JPanel upPanel;
    private JPanel downPanel;

    private JButton testB;
    private DateChooser dc;

    private Employee choosedEmployeeWorkload;
    private Employee choosedEmployeeRecord;
    private Client choosedClientRecord;

    private boolean REPORTING;

    private boolean READY_bool;

    Set<Service> lastChoosedServices;

    /*Day = 0
      Week = 1
      Month = 2*/
    private int D_W_M;

    public TaskHundler(JFrame owner)
    {
        super(owner, "Task", true);

        log.info("Creating new task frame...");

        mainPanel = new JPanel();
        tablePanel = new JPanel();

        salonGUI = SalonGUI.getSalonGUI();
        salon = salonGUI.getSalon();

        mainPanel.setLayout(new BorderLayout());
        tablePanel.setLayout(new BorderLayout());
        
        upPanel = new JPanel();

        update();

        downPanel = new JPanel();

        mainPanel.add(upPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(downPanel, BorderLayout.SOUTH);

        this.add(mainPanel);
        this.setLocationRelativeTo(owner);
        pack();
    }

    /**
     * Обновить содержимое формы
     * 
     */
    public void update()
    {
        log.info("Updating all components on task frame.");

        READY_bool = false;

        upPanel.removeAll();

        prepareRasp(upPanel);

        prepareEmployeeRasp(upPanel);

        prepareClientRasp(upPanel);

        prepare_D_W_M(upPanel);

        dc = new DateChooser();
        upPanel.add(dc);

        prepareReportButton(upPanel);

        upPanel.updateUI();
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    private void prepareRasp(JPanel upPanel)
    {
        JButton showRaspButton = new JButton("Show schedule");

        showRaspButton.addActionListener(event -> 
        {
            if(D_W_M == 0)
                makeTable( salon.showServicesPerDay(dc.getDate()) );
            else if(D_W_M == 1)
                makeTable( salon.showServicesPerWeek(dc.getDate()) );
            else if(D_W_M == 2)
                makeTable( salon.showServicesPerMonth(dc.getDate()) );
        });

        upPanel.add(showRaspButton);
    }

    private void prepareEmployeeRasp(JPanel upPanel)
    {
        JButton showRaspButton = new JButton("Employee\'s workload");

        showRaspButton.addActionListener(event -> 
        {
            if(choosedEmployeeWorkload != null)
            {
                if(D_W_M == 0)
                    makeTable( salon.calculateWorkloadDay(dc.getDate(), choosedEmployeeWorkload) );
                else if(D_W_M == 1)
                    makeTable( salon.calculateWorkloadWeek(dc.getDate(), choosedEmployeeWorkload) );
                else if(D_W_M == 2)
                    makeTable( salon.calculateWorkloadMonth(dc.getDate(), choosedEmployeeWorkload) );
            }
        });

        String[] eComboStr = prepareEmployeeList(salon.getEmployees());
        JComboBox eCombo = new JComboBox<String>(eComboStr);
        ActionListener eComboActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox)e.getSource();
                choosedEmployeeWorkload = salon.getEmployeeByID( takeIDfromCombo((String)box.getSelectedItem()) );
            }
        };
        eCombo.addActionListener(eComboActionListener);

        JPanel eRaspPanel = new JPanel();
        eRaspPanel.setLayout(new GridLayout(2, 1));

        eRaspPanel.add(showRaspButton);
        eRaspPanel.add(eCombo);
        upPanel.add(eRaspPanel);
    }

    private void prepareClientRasp(JPanel upPanel)
    {
        JButton showRaspButton = new JButton("Specialist\'s appointment");

        showRaspButton.addActionListener(event -> 
        {
            if(choosedEmployeeRecord != null && choosedClientRecord != null)
            {
                if(D_W_M == 0)
                    makeTable( salon.calculateClientRaspDay(dc.getDate(), choosedEmployeeRecord, choosedClientRecord) );
                else if(D_W_M == 1)
                    makeTable( salon.calculateClientRaspWeek(dc.getDate(), choosedEmployeeRecord, choosedClientRecord) );
                else if(D_W_M == 2)
                    makeTable( salon.calculateClientRaspMonth(dc.getDate(), choosedEmployeeRecord, choosedClientRecord) );
            }
        });

        String[] eComboStr = prepareEmployeeList(salon.getEmployees());
        JComboBox eCombo = new JComboBox<String>(eComboStr);
        ActionListener eComboActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox)e.getSource();
                choosedEmployeeRecord = salon.getEmployeeByID( takeIDfromCombo((String)box.getSelectedItem()) );
            }
        };
        eCombo.addActionListener(eComboActionListener);

        String[] cComboStr = prepareClientList(salon.getClients());
        JComboBox cCombo = new JComboBox<String>(cComboStr);
        ActionListener cComboActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox)e.getSource();
                choosedClientRecord = salon.getClientByID( takeIDfromCombo((String)box.getSelectedItem()) );
            }
        };
        cCombo.addActionListener(cComboActionListener);

        JPanel ecRaspPanel = new JPanel();
        ecRaspPanel.setLayout(new GridLayout(3, 1));

        ecRaspPanel.add(showRaspButton);
        ecRaspPanel.add(eCombo);
        ecRaspPanel.add(cCombo);
        upPanel.add(ecRaspPanel);
    }

    private static String[] prepareEmployeeList(Set<Employee> es)
    {
        int i;
        String[] res = new String[es.size()];
        i = 0;
        for(Employee e : es)
            res[i++] = e.getName() + " - " + e.getID();
        
        Arrays.sort(res, (String a, String b) -> {return a.compareTo(b);});

        return res;
    }

    private static String[] prepareClientList(Set<Client> cs)
    {
        int i;
        String[] res = new String[cs.size()];
        i = 0;
        for(Client c : cs)
            res[i++] = c.getName() + " - " + c.getID();
        
        Arrays.sort(res, (String a, String b) -> {return a.compareTo(b);});

        return res;
    }

    private static int takeIDfromCombo(String s)
    {
        int res;
        int i = s.lastIndexOf(" - ");
        String buffS = s.substring(i + 3, s.length());
        res = Integer.parseInt(buffS);
        return res;
    }

    private void prepare_D_W_M(JPanel upPanel)
    {
        JPanel RatioPanel = new JPanel();
        RatioPanel.setLayout(new GridLayout(3, 1));

        JRadioButton MonthRatio = new JRadioButton("Month");
        JRadioButton WeekRatio = new JRadioButton("Week");
        JRadioButton DayRatio = new JRadioButton("Day");

        ActionListener dwmListener = ae ->
        {
            if(MonthRatio.isSelected())
                D_W_M = 2;
            else if(WeekRatio.isSelected())
                D_W_M = 1;
            else if(DayRatio.isSelected())
                D_W_M = 0;
        };

        MonthRatio.addActionListener(dwmListener);
        WeekRatio.addActionListener(dwmListener);
        DayRatio.addActionListener(dwmListener);

        MonthRatio.setSelected(true);
        D_W_M = 2;

        ButtonGroup dwmRatioGroup = new ButtonGroup();
        dwmRatioGroup.add(MonthRatio);
        dwmRatioGroup.add(WeekRatio);
        dwmRatioGroup.add(DayRatio);

        RatioPanel.add(MonthRatio);
        RatioPanel.add(WeekRatio);
        RatioPanel.add(DayRatio);

        upPanel.add(RatioPanel);
    }

    private void prepareReportButton(JPanel upPanel)
    {
        JButton reportButton = new JButton(new ImageIcon(SalonGUI.class.getResource("/img/pdfButton.png")));
        reportButton.setToolTipText("Make report");
        REPORTING = false;
        reportButton.addActionListener(event -> 
        {
            if(REPORTING == false)
            {
                log.info("Begin do OT4ET PDF");
                
                if(READY_bool == true)
                {
                    REPORTING = true;
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Select where you want to save the report");
                    FileFilter pdf = new FileNameExtensionFilter("PDF file(.pdf)", "pdf");
                    fileChooser.addChoosableFileFilter(pdf);
                    fileChooser.setCurrentDirectory(new File("."));
                    int choice = fileChooser.showSaveDialog(this);
                    if(choice == JFileChooser.APPROVE_OPTION)
                    {
                        File file = fileChooser.getSelectedFile();
                        String OT4ET_PDF_path = file.getAbsolutePath();
                        Runnable task = () ->
                        {
                            ReportMaker.makeReport(salon, lastChoosedServices, D_W_M, dc.getDate(), OT4ET_PDF_path/*"./Report.pdf"*/);
                        };
                        new Thread(task).start();

                        JOptionPane.showMessageDialog(this, "OT4ET PDF Generated", "Generated", JOptionPane.INFORMATION_MESSAGE);
                    }

                    REPORTING = false;
                }
                else
                    JOptionPane.showMessageDialog(this, "First create the tables for the report", "Error", JOptionPane.ERROR_MESSAGE);

            }
        });
        
        upPanel.add(reportButton);
    }

    private void makeTable(Set<Service> services)
    {
        log.info("Preparing task table...");

        lastChoosedServices = services;

        tablePanel.removeAll();

        String[] servicesCol = {"ID", "Service type", "Date", "Employee", "Employee\'s cut", "Client", "Price", "Relevance"};
        Object[][] servicesData = services2Table(services);
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

        JTable serviceTable = new JTable(serviceModel);
        serviceTable.setAutoCreateRowSorter(true);
        serviceTable.setEnabled(true);
        DefaultTableCellRenderer r = (DefaultTableCellRenderer) serviceTable.getDefaultRenderer(Integer.class);
        r.setHorizontalAlignment(JLabel.LEFT);
        serviceTable.getTableHeader().setReorderingAllowed(false);

        tablePanel.add(new JScrollPane(serviceTable));
        tablePanel.updateUI();

        makeStatistics(downPanel);

        READY_bool = true;

        log.info("Task table was prepared.");
    }

    private void makeStatistics(JPanel downPanel)
    {
        log.info("Making statistics...");

        downPanel.removeAll();

        //=============money===============
        JPanel income = new JPanel();
        income.setLayout(new GridLayout(2, 1));

        double inWithoutPer = 0;
        double inWithPer = 0;
        if(D_W_M == 0)
        {
            inWithoutPer = salon.calculateIncomeCashDay(dc.getDate(), false);
            inWithPer = salon.calculateIncomeCashDay(dc.getDate(), true);
        }
        else if(D_W_M == 1)
        {
            inWithoutPer = salon.calculateIncomeCashWeek(dc.getDate(), false);
            inWithPer = salon.calculateIncomeCashWeek(dc.getDate(), true);
        }
        else if(D_W_M == 2)
        {
            inWithoutPer = salon.calculateIncomeCashMonth(dc.getDate(), false);
            inWithPer = salon.calculateIncomeCashMonth(dc.getDate(), true);
        }

        income.add(new JLabel("Income: " + inWithoutPer));
        income.add(new JLabel("With percent: " + inWithPer));
        downPanel.add(income);

        //=============number of clients===============
        JPanel number = new JPanel();
        number.setLayout(new GridLayout(2, 1));

        int unum = 0;
        int num = 0;
        if(D_W_M == 0)
        {
            unum = salon.calculateСlientsNumDay(dc.getDate(), true);
            num = salon.calculateСlientsNumDay(dc.getDate(), false);
        }
        else if(D_W_M == 1)
        {
            unum = salon.calculateСlientsNumWeek(dc.getDate(), true);
            num = salon.calculateСlientsNumWeek(dc.getDate(), false);
        }
        else if(D_W_M == 2)
        {
            unum = salon.calculateСlientsNumMonth(dc.getDate(), true);
            num = salon.calculateСlientsNumMonth(dc.getDate(), false);
        }

        number.add(new JLabel("Number of clients: " + num));
        number.add(new JLabel("Unique: " + unum));
        downPanel.add(number);

        //=============Best===============
        JPanel bests = new JPanel();
        bests.setLayout(new GridLayout(2, 1));

        Employee eBestMoney = null;
        Employee eBestTraffic = null;

        if(D_W_M == 0)
        {
            eBestMoney = salon.cal_BestCashEmployee_Day(dc.getDate());
            eBestTraffic = salon.cal_BestTrafficEmployee_Day(dc.getDate());
        }
        else if(D_W_M == 1)
        {
            eBestMoney = salon.cal_BestCashEmployee_Week(dc.getDate());
            eBestTraffic = salon.cal_BestTrafficEmployee_Week(dc.getDate());
        }
        else if(D_W_M == 2)
        {
            eBestMoney = salon.cal_BestCashEmployee_Month(dc.getDate());
            eBestTraffic = salon.cal_BestTrafficEmployee_Month(dc.getDate());
        }

        bests.add(new JLabel("Best money: " + (eBestMoney == null?"None":eBestMoney.getName() + " - " + eBestMoney.getID()) ));
        bests.add(new JLabel("Best traffic: " + (eBestTraffic == null?"None":eBestTraffic.getName() + " - " + eBestTraffic.getID()) ));
        downPanel.add(bests);

        downPanel.updateUI();

        log.info("Statistics was formed");
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
                res[i][0] = Integer.valueOf(s.getID());
                res[i][1] = s.getServiceType().getDescription() + " - " + s.getServiceType().getID();
                res[i][2] = Salon.doOnlyDate(s.getDateBegin());
                res[i][3] = s.getEmployee().getName() + " - " + s.getEmployee().getID();
                res[i][4] = Double.valueOf(s.getCashReward());
                res[i][5] = s.getClient().getName() + " - " + s.getClient().getID();
                res[i][6] = Double.valueOf(s.getPrice());
                
                res[i][7] = "" + s.isRELEVANT();
            ++i;
        }
        return res;
    }
}