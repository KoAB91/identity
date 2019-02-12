package client;

import dao.RequestDao;
import entity.Request;
import entity.RequestStatus;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientExample {


    public static void createApplications(int count) {

        RequestDao requestDao = RequestDao.getInstance();
        requestDao.createTable();
        for (int i = 1; i <= count; i++){
            Request request = new Request();
            request.setCreationTime(LocalDateTime.now());
            request.setRequestStatus(RequestStatus.CREATED);
            request.setClientId((int) (Math.random()*1000) + 1);
            request.setLeadTime((int) (Math.random()*100)+30);
            requestDao.add(request);
        }
    }
}

