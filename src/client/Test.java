package client;

import dao.RequestDao;
import entity.*;

import java.time.LocalDateTime;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
//        RequestDao requestDao = RequestDao.getInstance();
//        requestDao.createTable();
//        LocalDateTime time = null;
//        for (int i = 0; i < 100; i++){
//            Request request = new Request();
//            request.setClientId(i);
//            request.setCreationTime(LocalDateTime.now());
//            request.setLeadTime((int) (Math.random()*100)+30);
//            request.setRequestStatus(RequestStatus.CREATED);
//            request.setEndTime(null);
//            requestDao.add(request);
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        String request = "POST /applications HTTP/1.1\n" +
                "Host: localhost:8010\n" +
                "Connection: keep-alive\n" +
                "Content-Length: 14\n" +
                "Accept: application/json, text/plain, */*\n" +
                "Origin: http://localhost:4200\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.96 Safari/537.36\n" +
                "Content-Type: application/json\n" +
                "Referer: http://localhost:4200/application-history\n" +
                "Accept-Encoding: gzip, deflate, br\n" +
                "Accept-Language: ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7\n";

        String[] strings = request.split(" ");
//        System.out.println(Arrays.toString(strings));
        String str = null;
        for (int i = 0; i < strings.length; i++){
            if (strings[i].contains("Content-Length")){
                str = strings[i+1].replaceAll("[^0-9]", "");
                break;
            }
        }
        int count = Integer.parseInt(str);
        System.out.println(count);

    }
}
