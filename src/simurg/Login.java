package simurg;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JFrame {

    private JPanel panel1;
    private JTextField txtName;
    private JLabel lName;
    private JTextField txtAddress;
    private JButton loginButton;
    private JTextField txtPort;

    public Login(){

        add(panel1);
        setSize(400,380);
        setTitle("The SChat Login");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setVisible(true);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String name = txtName.getText().trim().isEmpty() ? "" : txtName.getText().trim();
                String address = txtAddress.getText().trim().isEmpty() ? "" : txtAddress.getText().trim();;
                int port = txtPort.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtPort.getText().trim());

                login(name, address, port);
            }
        });

    }

    private void login(String name, String address, int port) {
        dispose();
        new ClientWindow(name, address, port);
    }

    public static void main(String[] args){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Login l = new Login();
            }
        });
    }

}
