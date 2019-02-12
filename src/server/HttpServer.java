package server;

import client.ClientExample;
import dao.DirectorDao;
import dao.ManagerDao;
import dao.OperatorDao;
import dao.RequestDao;
import entity.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.List;


public class HttpServer extends Thread {

    @Override
    public void run() {
        startServer();
    }

    public static void startServer() {
        try {
            ServerSocket socket = new ServerSocket(8010);
            while (true) {
                Socket client = socket.accept();
                System.err.println("Client accepted");
                new Thread(new SocketProcessor(client)).start();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static class SocketProcessor implements Runnable {

        private Socket s;
        private InputStream is;
        private OutputStream os;

        private SocketProcessor(Socket s) throws Throwable {
            this.s = s;
            this.is = s.getInputStream();
            this.os = s.getOutputStream();
        }

        public void run() {
            try {
                readInputHeaders();
//                writeResponse("<html><body><h1>Welcome to Identity!</h1></body></html>");
            } catch (Throwable t) {
//            } finally {
//                try {
//                    s.close();
//                } catch (Throwable t) {
//                }
            }
            System.err.println("Client processing finished");
        }


        private void readInputHeaders() throws Throwable {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while (true) {
                String line = br.readLine();
                if (line == null || line.trim().length() == 0) {
                    break;
                }
                System.out.println(line);

                if (line.contains("application-history")) {
                    getApplicationHistory();
                }
                if (line.contains("employees")) {
                    getEmployees();
                }
                if (line.contains("create-applications")){
                    int i = 2;
                    ClientExample.createApplications(i);
                }
            }
        }

        private void writeResponse(String s) {
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Server: IdentityServer/2019-02-11\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + s.length() + "\r\n" +
                    "Connection: close\r\n\r\n";
            String result = response + s;
            try {
                os.write(result.getBytes());
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        private void getApplicationHistory() {
            RequestDao requestDao = RequestDao.getInstance();
            List<Request> waitingRequests = requestDao.getAllByStatus(RequestStatus.CREATED);
            List<Request> pendingRequests = requestDao.getAllByStatus(RequestStatus.IN_PROCESSING);
            List<Request> doneRequests = requestDao.getAllByStatus(RequestStatus.DONE);

            JSONObject json = new JSONObject();

            json.put("waitingCount", waitingRequests.size()).put("pendingCount", pendingRequests.size());

            JSONArray apllications = new JSONArray();

            for (Request request : doneRequests) {
                JSONObject apllication = new JSONObject();
                apllication.put("", new JSONObject().put("id", request.getId())
                        .put("perfomedBy", request.getEmployee())
                        .put("endDate", request.getEndTime().toString())
                        .put("expectedTime", request.getLeadTime())
                        .put("takenTime", (Duration.between(request.getCreationTime(), request.getEndTime()).getSeconds())));
                apllications.put(apllication);
            }
            json.put("apllications", apllications);
            writeResponse(json.toString());
        }

        private void getEmployees() {
            List<Operator> operators = OperatorDao.getInstance().getAll();
            JSONObject json = new JSONObject();
            for (Operator operator : operators) {
                json.put("", new JSONObject()
                        .put("id", operator.getId())
                        .put("name", operator.getName())
                        .put("applicationId", operator.getRequestId())
                        .put("role", operator.getRole().toString().toLowerCase()).toString());
            }

            List<Manager> managers = ManagerDao.getInstance().getAll();
            for (Manager manager : managers) {
                json.put("", new JSONObject()
                        .put("id", manager.getId())
                        .put("name", manager.getName())
                        .put("applicationId", manager.getRequestId())
                        .put("role", manager.getRole().toString().toLowerCase()).toString());
            }

            List<Director> directors = DirectorDao.getInstance().getAll();
            for (Director director : directors) {
                json.put("", new JSONObject()
                        .put("id", director.getId())
                        .put("name", director.getName())
                        .put("applicationId", director.getRequestId())
                        .put("role", director.getRole().toString().toLowerCase()).toString());
            }

            writeResponse(json.toString());
        }

    }
}
