package simurg.server;

public class ServerMain {

    private int port;
    private Server server;

    public ServerMain(int port){
        this.port = port;
        System.out.println(port);
        server = new Server(port);
    }

    public static void main(String[] args){

        int port;
        if(args.length != 1){
            System.out.println("Usage: java -jar SChat.jar [port]");
            return;
        }

        port = Integer.parseInt(args[0]);
        new ServerMain(port);

    }

}
