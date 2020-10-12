package simurg.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable{

    private List<ServerClient> clientList = new ArrayList<ServerClient>();

    private DatagramSocket socket;
    private int port;
    private Thread run, manage, send, receive;
    private boolean running = false;

    public Server(int port){
        this.port = port;
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        run = new Thread(this , "Server");
        run.start();
    }

    public void run(){
        running = true;
        System.out.println("Server started on port " + port);
        manageClients();
        receive();
    }

    private void manageClients() {
        manage = new Thread("Manage"){
            public void run(){
                while(running){

                }
            }
        };
        manage.start();
    }

    private void receive() {
        receive = new Thread("Receive"){
            public void run(){
                while(running){
                    byte[] data = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    try {
                        socket.receive(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    process(packet);
                }
            }
        };
        receive.start();
    }

    private void sendToAll(String message){
        for ( int i = 0 ; i < clientList.size() ; i++ ) {
            ServerClient client = clientList.get(i);
            send(message.getBytes(), client.address, client.port);
        }
    }

    private void send (byte[] data , final InetAddress address, final int port) {
        send = new Thread("send"){
            public void run(){
                DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        send.start();
    }

    private void send(String message, InetAddress address, int port){
        message += "/e/";
        send(message.getBytes(), address, port);
    }

    private void process(DatagramPacket packet) {

        String string = new String(packet.getData());
        if(string.startsWith("/c/")){
            int id = UniqueIdentifier.getIdentifier();
            clientList.add(new ServerClient(string.substring(3,string.length()), packet.getAddress(), packet.getPort(),id));
            String ID = "/c/" + id;
            send(ID, packet.getAddress(), packet.getPort());
        }   else if (string.startsWith("/m/")) {
            sendToAll(string);
        }   else if (string.startsWith("/d/")) {
            String id = string.split("/d/|/e/")[1];
            disconnect(Integer.parseInt(id), true);
        }   else {
            System.out.println(string);
        }

    }

    private void disconnect (int id, boolean status) {
        ServerClient c = null;
        for (int i = 0 ; i < clientList.size() ; i++ ){
            if(clientList.get(i).getID() == id){
                c = clientList.get(i);
                clientList.remove(i);
                break;
            }
        }

        String message = "";
        if(status){
            message = "Client " + c.name + " (" + c.getID() + ") @ " + c.address.toString() + ":" + c.port + " disconnected!";
        } else{
            message = "Client " + c.name + " (" + c.getID() + ") @ " + c.address.toString() + ":" + c.port + " timed out!";
        }

        System.out.println(message);

    }

}
