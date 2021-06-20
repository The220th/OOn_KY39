package labgui;

import java.lang.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;

/**
 * Панель для выбора даты
 * 
 */
@SuppressWarnings( "deprecation" )
public class DateChooser extends JPanel
{
    private String[] yComboStr;
    private String[] mComboStr;
    private String[] dComboStr;

    private JComboBox yCombo;
    private JComboBox mCombo;
    private JComboBox dCombo;


    private JTextField textDate;

    private int selectedYear;
    private int selectedMonth;
    private int selectedDate;

    public DateChooser()
    {
        int currentY = (new Date()).getYear() + 1900;
        yComboStr = get_yComboStr(currentY-100, currentY+1);
        mComboStr = get_mComboStr();
        dComboStr = get_dComboStr();

        yCombo = new JComboBox<String>(yComboStr);
        mCombo = new JComboBox<String>(mComboStr);
        dCombo = new JComboBox<String>(dComboStr);
        textDate = new JTextField();

        ActionListener yActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox)e.getSource();
                selectedYear = Integer.parseInt((String)box.getSelectedItem());
                textDate.setText("You selected: " + doStringDate(getDate()));
            }
        };
        yCombo.addActionListener(yActionListener);

        ActionListener mActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox)e.getSource();
                selectedMonth = Integer.parseInt((String)box.getSelectedItem());
                textDate.setText("You selected: " + doStringDate(getDate()));
            }
        };
        mCombo.addActionListener(mActionListener);

        ActionListener dActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox)e.getSource();
                selectedDate = Integer.parseInt((String)box.getSelectedItem());
                textDate.setText("You selected: " + doStringDate(getDate()));
            }
        };
        dCombo.addActionListener(dActionListener);

        //yCombo.setSelectedIndex(0);
        //mCombo.setSelectedIndex(0);
        //dCombo.setSelectedIndex(0);
        setDate(new Date());

        this.setLayout(new GridLayout(2, 1/*, 15, 15*/));

        JPanel downPanel = new JPanel();
        downPanel.setLayout(new GridLayout(1, 3/*, 15, 15*/));

        JPanel dPanel = new JPanel();
        dPanel.add(new JLabel("Day: "));
        dPanel.add(dCombo);

        JPanel mPanel = new JPanel();
        mPanel.add(new JLabel("Month: "));
        mPanel.add(mCombo);

        JPanel yPanel = new JPanel();
        yPanel.add(new JLabel("Year: "));
        yPanel.add(yCombo);

        downPanel.add(dPanel);
        downPanel.add(mPanel);
        downPanel.add(yPanel);

        textDate.setText("Today is " + doStringDate(new Date()));
        textDate.setEditable(false);

        this.add(textDate);
        this.add(downPanel);

    }

    /**
     * Получить дату, выбранную на панели
     * 
     * @return дата, выбранная на панели
     */
    public Date getDate()
    {
        Date resDate = new Date();
        resDate.setYear(selectedYear - 1900);
        resDate.setMonth(selectedMonth-1);
        resDate.setDate(selectedDate);
        return resDate;
    }

    /**
     * Установить дату на панели
     * 
     * @param date дата, которую нужно установить на панели
     */
    public void setDate(Date date)
    {
        int y = date.getYear() + 1900;
        int m = date.getMonth() + 1;
        int day = date.getDate();

        yCombo.setSelectedItem("" + y);
        mCombo.setSelectedItem("" + m);
        dCombo.setSelectedItem("" + day);
    }

    private String[] get_yComboStr(int b, int e)
    {
        int i, j;
        String[] res = new String[(e-b)+1];
        for(i = e, j = 0; i >= b; --i, ++j)
            res[j] = "" + i;
        return res;
    }

    private String[] get_mComboStr()
    {
        int b = 1;
        int e = 12;
        int i, j;
        String[] res = new String[(e-b)+1];
        for(i = b, j = 0; i <= e; ++i, ++j)
            res[j] = "" + i;
        return res;
    }

    private String[] get_dComboStr()
    {
        int b = 1;
        int e = 31;
        int i, j;
        String[] res = new String[(e-b)+1];
        for(i = b, j = 0; i <= e; ++i, ++j)
            res[j] = "" + i;
        return res;
    }

    /**
     * Преобразует дату в строку
     * 
     * @param d преобразуемая дата
     * @return строка с датой d
     */
    public static String doStringDate(Date d)
    {
        String toOut = "";
        toOut += (d.getDate()>9?d.getDate():"0"+d.getDate()) + ".";
        toOut += (d.getMonth()+1>9?d.getMonth()+1:"0"+(d.getMonth()+1)) + ".";
        toOut += (d.getYear()+1900);
        return toOut;
    }
}