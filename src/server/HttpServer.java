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
            System.out.println("server started");
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
            } catch (Throwable t) {
            }
            System.err.println("Client processing finished");
        }


        private void readInputHeaders() {
            try (InputStreamReader ss = new InputStreamReader(is);
                 BufferedReader br = new BufferedReader(ss)) {

                StringBuilder request = new StringBuilder();

                while (true) {
                    String line = br.readLine();
                    if (line.isBlank()) {
                        break;
                    }
                    request.append(line).append("\r\n");
                }

                System.out.println("Request accepted");
                System.out.println(request);

                if (request.toString().contains("OPTIONS")) {
                    writeOptionsResponse();
                } else if (request.toString().contains("application-history")) {
                    if (request.toString().contains("POST")) {
                        String[] numbOfSigns = request.toString().split(" ");
                        int number = 0;
                        for (int i = 0; i < numbOfSigns.length; i++){
                            if (numbOfSigns[i].contains("Content-Length")){
                                number = Integer.parseInt(numbOfSigns[i+1].replaceAll("[^0-9]", ""));
                                break;
                            }
                        }
                        StringBuilder params = new StringBuilder();
                        for(int i = 0; i < number; i++){
                            params.append((char) br.read());
                        }
                        String[] appValue = params.toString().split(":");
                        int count = 0;
                        for (int i = 0; i < appValue.length; i++){
                            if (appValue[i].contains("count")){
                                count = Integer.parseInt(appValue[i+1].replaceAll("[^0-9]", ""));
                                break;
                            }
                        }
                        ClientExample.createApplications(count);
                    } else {
                        getApplicationHistory();
                    }
                } else if (request.toString().contains("employees")) {
                    getEmployees();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private void writeResponse(String s) {
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Server: IdentityServer/2019-02-11\r\n" +
                    "Access-Control-Allow-Origin: *\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Content-Length: " + s.length() + "\r\n" +
                    "Connection: close\r\n\r\n";
            String result = response + s;
            try {
                OutputStreamWriter out = new OutputStreamWriter(os);
                out.write(result);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void writeOptionsResponse() {
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Server: IdentityServer/2019-02-11\r\n" +
                    "Access-Control-Allow-Headers: *\r\n" +
                    "Access-Control-Allow-Origin: *\r\n" +
                    "Access-Control-Allow-Methods: OPTIONS, GET, PUT, POST, DELETE\r\n" +
                    "Content-Length: 0\r\n" +
                    "Connection: close\r\n\r\n";
            try {
                OutputStreamWriter out = new OutputStreamWriter(os);
                out.write(response);
                out.flush();
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

            json.accumulate("waitingCount", waitingRequests.size()).accumulate("pendingCount", pendingRequests.size());

            JSONArray applications = new JSONArray();

            for (Request request : doneRequests) {
                JSONObject application = new JSONObject();
                application.accumulate("id", request.getId())
                        .accumulate("performedBy", request.getEmployee())
                        .accumulate("endDate", request.getEndTime().toString())
                        .accumulate("expectedTime", request.getLeadTime())
                        .accumulate("takenTime", (Duration.between(request.getCreationTime(), request.getEndTime()).getSeconds()));
                applications.put(application);
            }
            json.put("applications", applications);
            writeResponse(json.toString());
        }

        private void getEmployees() {
            List<Operator> operators = OperatorDao.getInstance().getAll();
            JSONArray jsonArray = new JSONArray();
            int requestId;
            for (Operator operator : operators) {
                requestId = operator.getRequestId();
                jsonArray.put(new JSONObject()
                        .accumulate("id", operator.getId())
                        .accumulate("name", operator.getName())
                        .accumulate("applicationId", requestId == 0 ? null : requestId)
                        .accumulate("role", operator.getRole().toString().toLowerCase()));
            }

            List<Manager> managers = ManagerDao.getInstance().getAll();
            for (Manager manager : managers) {
                requestId = manager.getRequestId();
                jsonArray.put(new JSONObject()
                        .accumulate("id", manager.getId())
                        .accumulate("name", manager.getName())
                        .accumulate("applicationId", requestId == 0 ? null : requestId)
                        .accumulate("role", manager.getRole().toString().toLowerCase()));
            }

            List<Director> directors = DirectorDao.getInstance().getAll();
            for (Director director : directors) {
                requestId = director.getRequestId();
                jsonArray.put(new JSONObject()
                        .accumulate("id", director.getId())
                        .accumulate("name", director.getName())
                        .accumulate("applicationId", requestId == 0 ? null : requestId)
                        .accumulate("role", director.getRole().toString().toLowerCase()));
            }
            writeResponse(jsonArray.toString());
        }

    }
}
