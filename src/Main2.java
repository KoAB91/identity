import dao.RequestDao;
import distrib.Distributor;
import entity.Request;
import entity.RequestStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Main2 {
    public static void main(String[] args) {
        RequestDao requestDao = RequestDao.getInstance();
//        requestDao.createTable();
//        for (int i = 0; i < 30; i++){
//            Request request = new Request();
//            request.setClientId(i);
//            request.setCreationTime(LocalDateTime.now());
//            request.setLeadTime(10);
//            request.setRequestStatus(RequestStatus.CREATED);
//            request.setEndTime(null);
//            requestDao.add(request);
//        }
//        Distributor.distribute();

        List<Request> waitingRequests = requestDao.getAllByStatus(RequestStatus.CREATED);
        List<Request> pendingRequests = requestDao.getAllByStatus(RequestStatus.IN_PROCESSING);
        List<Request> doneRequests = requestDao.getAllByStatus(RequestStatus.DONE);


        JSONArray jsonAr = new JSONArray();
        JSONObject json = new JSONObject();
        JSONObject json1 = new JSONObject();

        json.put("waitingCount", waitingRequests.size());
        jsonAr.put(json);
        json1.put("pendingCount", pendingRequests.size());
        jsonAr.put(json1);
//        JSONArray apllications = new JSONArray();
//
//        for (Request request : doneRequests) {
//            JSONObject apllication = new JSONObject();
//            apllication.append("", new JSONObject().append("append", request.getId())
//                                         .append("perfomedBy", request.getEmployee())
//                                         .append("endDate", request.getEndTime().toString())
//                                         .append("expectedTime", request.getLeadTime())
//                                         .append("takenTime", (Duration.between(request.getCreationTime(), request.getEndTime()).getSeconds())));
//            apllications.put(apllication);
//        }
//        json.accumulate("apllications", apllications);
        System.out.println(jsonAr);
        String jsonString = new JSONObject().put("waitingCount", waitingRequests.size()).put("pendingCount", pendingRequests.size()).toString();
        System.out.println(jsonString);
    }
}
