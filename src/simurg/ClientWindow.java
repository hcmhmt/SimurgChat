package simurg;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;

public class ClientWindow extends JFrame implements Runnable{

    private JPanel ClientPanel;
    private JButton btn_send;
    private JTextField txt_message;
    private JTextArea txt_history;

    private Client client;

    private Thread listen, run;

    private boolean running = false;

    public ClientWindow(String name, String address, int port){

        client = new Client(name, address, port);

        boolean connected = client.openConnection();
        if( !connected ){
            System.err.println("Connection failed!");
            console("Connection failed!");
        }

        createWindow(900, 500, "The SChat Client Side");
        console("Attempting a connection to " + address + ":" + port + ", user : " + name);

        String send_message = "/c/" + name;
        client.send(send_message.getBytes());

        running = true;
        run = new Thread(this, "Running");
        run.start();
    }

    private void createWindow(int width, int height, String title){
        add(ClientPanel);
        setSize(width,height);
        setTitle(title);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        txt_history.setEditable(false);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        btn_send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = txt_message.getText().trim();
                send(message, true);
            }
        });
        txt_message.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    String message = txt_message.getText().trim();
                    send(message, true);
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                String disconnect = "/d/" + client.getID() + "/e/";
                send(disconnect, false);
                running = false;
                client.close();
            }
        });

        setVisible(true);
    }

    private void send(String message, boolean status){
        if(message.isEmpty()) return;

        if(status){
            message = client.getName() + " : " + message;
            message = "/m/" + message;
        }

        client.send(message.getBytes());
        txt_message.setText("");
    }

    private void listen(){
        listen = new Thread("Listen") {
            public void run(){
                while (running) {
                    String message = client.receive();
                    if(message.startsWith("/c/")){
                        client.setID(Integer.parseInt(message.trim().split("/c/|/e/")[1]));
                        console("Successfull connected to the server! ID: " + client.getID());
                    } else if (message.startsWith("/m/")) {
                        String text = message.substring(3);
                        text = text.split("/e/")[0];
                        console(text);
                    }
                }
            }
        };
        listen.start();
    }

    private void console(String message){

        txt_history.append(message + "\n\r");
        txt_history.setCaretPosition(txt_history.getDocument().getLength());

    }

    @Override
    public void run() {
        listen();
    }
}
