package simurg;

import java.io.IOException;
import java.net.*;

public class Client {

    private String name, address;
    private int port;
    private int ID = -1;

    private DatagramSocket socket;
    private InetAddress ip;

    private Thread send;

    // Start : Methods
    public Client(String name, String address, int port){
        this.name = name;
        this.address = address;
        this.port = port;
    }

    public boolean openConnection(){

        try {
            socket = new DatagramSocket();
            ip = InetAddress.getByName(address);
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
            return false;
        }

        return true ;
    }

    public String receive(){

        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data,data.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String message = new String(packet.getData());

        return message;
    }

    public void send(final byte[] data){

        send = new Thread("send"){
            public void run(){
                DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        send.start();

    }

    public void close(){
        new Thread(() -> {
            synchronized (socket){
                socket.close();
            }
        }).start();
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public int getID() {
        return ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
