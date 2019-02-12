package client;

import dao.DirectorDao;
import dao.ManagerDao;
import dao.OperatorDao;
import dao.RequestDao;
import distrib.Distributor;
import entity.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        RequestDao requestDao = RequestDao.getInstance();
        requestDao.createTable();
        LocalDateTime time = null;
        for (int i = 0; i < 100; i++){
            Request request = new Request();
            request.setClientId(i);
            request.setCreationTime(LocalDateTime.now());
            request.setLeadTime((int) (Math.random()*100)+30);
            request.setRequestStatus(RequestStatus.CREATED);
            request.setEndTime(null);
            requestDao.add(request);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
