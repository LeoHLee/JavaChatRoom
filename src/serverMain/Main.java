package serverMain;

import database.*;
import message.MessageManager;
import serverNet.*;

import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLSyntaxErrorException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static serverNet.IPCrawler.*;

public class Main {

    public static void main(String[] args) {
        //Get database password and connect to database
        String pswd=JOptionPane.showInputDialog("Input MySQL root password");
        try {
            database.initial.initialize(pswd);
            message.initial.initialize(pswd);
        } catch (SQLSyntaxErrorException e) {
            System.out.println("Database already exists");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,e.getMessage());
            return;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,"Failed to connect to database");
            return;
        }

        //build GUI
        JFrame gui=new JFrame();
        gui.setLayout(new BorderLayout());
        JPanel labels=new JPanel();
        labels.setSize(400,200);
        labels.setLayout(new GridLayout(3,1));
        JLabel ipLabel1=new JLabel("IP: Pending...");
        JLabel ipLabel2=new JLabel("Port: Pending...");
        JLabel notice=new JLabel("Tell your clients the above information!");
        ipLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        ipLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        notice.setHorizontalAlignment(SwingConstants.CENTER);
        labels.add(ipLabel1,0);
        labels.add(ipLabel2,1);
        labels.add(notice,2);
        JTextArea text=new JTextArea();
        text.setEditable(false);
        JScrollPane scrollPane=new JScrollPane(text);
        gui.add(labels,BorderLayout.NORTH);
        gui.add(scrollPane,BorderLayout.CENTER);
        gui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gui.setSize(400,550);
        gui.setTitle("Server");
        gui.setVisible(true);
        String lip=getLocalIP();
        ipLabel1.setText("IP: "+lip);
        if(!getInternetIP().equals(lip))
            notice.setText("Warning: NOT Internet IP! Try natapp.cn for Internet service.");

        //Launch server
        ServerNetManager network;
        try {
            try {
                network = new ServerNetManager(13060);
            } catch (IOException e) {
                network = new ServerNetManager(0);
            }
            ipLabel2.setText("Port: "+network.getPort());
        } catch (IOException e) {
            System.err.println("Failed to launch server network!");
            return;
        }
		network.parser=new ServerBeanParser(
                network,
                new AccountManager(pswd),
                new FriendSysManager(pswd),
                new MessageManager(pswd),
                new MailVerifier("Java2020Group9@163.com","CNKFUFWDLSCRSKYG", "smtp.163.com")
        );

        network.log=(s)->
            SwingUtilities.invokeLater(()->text.append(
                    new SimpleDateFormat("HH:mm:ss").format(new Date())+" "+s+'\n'));
        try {
            network.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

