package labgui;

import java.lang.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;

/**
 * Форма для загрузки. Во время загрузки чего либо можно вызвать эту форму, чтобы пользователю было известно, что именно сейчас происходит
 * 
 */
public class LoadingMessage extends JDialog
{
    private final int DEFAULT_WIDTH = 330;
    private final int DEFAULT_HEIGHT = 100;

    private JPanel picPanel;
    private ArrayList<JLabel> imgs;

    private String msg;
    private JLabel msgLabel;

    private boolean ALIVE;

    public LoadingMessage(JFrame owner, String msg)
    {
        super(owner, "Loading...", true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        this.msg = msg;
        this.ALIVE = false;

        picPanel = new JPanel();

        imgs = new ArrayList<JLabel>();
        imgs.add( new JLabel(new ImageIcon(LoadingMessage.class.getResource("/img/loadPic1.png"))) );
        imgs.add( new JLabel(new ImageIcon(LoadingMessage.class.getResource("/img/loadPic2.png"))) );
        imgs.add( new JLabel(new ImageIcon(LoadingMessage.class.getResource("/img/loadPic3.png"))) );
        imgs.add( new JLabel(new ImageIcon(LoadingMessage.class.getResource("/img/loadPic4.png"))) );
        imgs.add( new JLabel(new ImageIcon(LoadingMessage.class.getResource("/img/loadPic3.png"))) );
        imgs.add( new JLabel(new ImageIcon(LoadingMessage.class.getResource("/img/loadPic4.png"))) );
        imgs.add( new JLabel(new ImageIcon(LoadingMessage.class.getResource("/img/loadPic3.png"))) );

        msgLabel = new JLabel(msg);
        
        this.add(picPanel);

        this.setLocationRelativeTo(owner);
        pack();
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Показать форму загрузки
     * 
     * @param SHOWED true=показать, false=скрыть
     */
    public void showLoad(boolean SHOWED)
    {
        if(SHOWED == true)
        {
            ALIVE = true;
            Runnable task = () ->
            {
                picPanelAnimator();
            };
            new Thread(task).start();
            this.setVisible(true);
        }
        else
        {
            ALIVE = false;
            this.setVisible(false);
        }
    }

    private void picPanelAnimator()
    {
        try
        {
            if(imgs.size() > 0)
            {
                int tick = 0;
                while(ALIVE)
                {
                    picPanel.removeAll();

                    picPanel.add(msgLabel);

                    picPanel.add( imgs.get(tick) );

                    tick = (tick+1)%imgs.size();

                    picPanel.updateUI();

                    Thread.sleep(500);
                }
            }
            else
                picPanel.add(msgLabel);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}