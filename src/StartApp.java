import distrib.Distributor;
import server.HttpServer;



public class StartApp {
    public static void main(String[] args) {

        new Thread(new HttpServer()).start();
        new Thread(new Distributor()).start();
    }
}
