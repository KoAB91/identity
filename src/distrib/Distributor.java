package distrib;

import dao.DirectorDao;
import dao.ManagerDao;
import dao.OperatorDao;
import dao.RequestDao;
import entity.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Distributor extends Thread{

    static ExecutorService executeIt = Executors.newFixedThreadPool(16);
    private static long timeToProccesingByOperator = 40;
    private static long timeToProccesingByManager = 60;

    @Override
    public void run() {
        distribute();
    }

    public static void distribute() {

        // получаем соединения с таблицами
        RequestDao requestDao = RequestDao.getInstance();
//        requestDao.createTable();
        OperatorDao operatorDao = OperatorDao.getInstance();
//        operatorDao.createTable();
//        for (int i = 1; i <= 10; i++){
//            Operator operator = new Operator();
//            operator.setName("Operator " + i);
//            operatorDao.add(operator);
//        }
        ManagerDao managerDao = ManagerDao.getInstance();
//        managerDao.createTable();
//        for (int i = 1; i <= 5; i++){
//            Manager manager = new Manager();
//            manager.setName("Manager " + i);
//            managerDao.add(manager);
//        }
        DirectorDao directorDao = DirectorDao.getInstance();
//        directorDao.createTable();
//        Director director1 = new Director();
//        director1.setName("Director");
//        directorDao.add(director1);

        List<Operator> operators = null;
        List<Manager> managers = null;
        Director director = null;
        List<Request> requests = null;

        main:
        while (true) {
            requests = requestDao.getAllByStatus(RequestStatus.CREATED);
            if (requests.size() == 0) {
                continue;
            }
            operators = operatorDao.getNotWorking();
            if (operators.size() > 0) {
                for (Operator operator : operators) {
                    if (!requests.isEmpty()) {
                        Request request = requests.get(0);
                        executeIt.execute(new OperatorHandler(requestDao, operatorDao, operator, request));
                        requests.remove(0);
                    } else continue main;
                }
            }
            if (!requests.isEmpty()) {
                if (!requests.get(0).getCreationTime().plusSeconds(timeToProccesingByOperator).isAfter(LocalDateTime.now())) {
                    continue;
                } else {
                    managers = managerDao.getNotWorking();
                    if (managers.size() > 0) {
                        for (Manager manager : managers) {
                            if (!requests.isEmpty()) {
                                Request request = requests.get(0);
                                executeIt.execute(new ManagerHandler(requestDao, managerDao, manager, request));
                                requests.remove(0);
                            } else continue main;
                        }
                    }
                }
            } else continue;
            if (!requests.isEmpty()) {
                if (!requests.get(0).getCreationTime().plusSeconds(timeToProccesingByManager).isAfter(LocalDateTime.now())) {
                    continue;
                } else {
                    director = directorDao.getNotBusy();
                    if (director != null) {
                        Request request = requests.get(0);
                        System.out.println("Заявка с индексом 0 = " + request.getId() + " идет к директору");
                        executeIt.execute(new DirectorHandler(requestDao, directorDao, director, request));
                        requests.remove(0);
                    }
                }
            }
            requests.clear();
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class OperatorHandler implements Runnable {

    private RequestDao requestDao;
    private OperatorDao operatorDao;
    private Operator operator;
    private Request request;

    public OperatorHandler(RequestDao requestDao, OperatorDao operatorDao, Operator operator, Request request) {
        this.requestDao = requestDao;
        this.operatorDao = operatorDao;
        this.operator = operator;
        this.request = request;
    }

    @Override
    public void run() {
        operatorDao.update(operator.getId(), request.getId());
        requestDao.update(request.getId(), RequestStatus.IN_PROCESSING);
        System.out.println("Статус заявки " + request.getId() + " измененен на PROCESSING");
        request.setStartProcTime(LocalDateTime.now());
        requestDao.setEmployee(request.getId(), operator.getName());
        System.out.println("Оператор " + operator.getId() + " взял заявку " + request.getId());
        try {
            Thread.sleep(request.getLeadTime() * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        operatorDao.update(operator.getId(), 0);
        requestDao.update(request.getId(), RequestStatus.DONE);
        System.out.println("Статус заявки " + request.getId() + " измененен на DONE");
    }
}

class ManagerHandler implements Runnable {

    RequestDao requestDao;
    ManagerDao managerDao;
    Manager manager;
    Request request;

    public ManagerHandler(RequestDao requestDao, ManagerDao managerDao, Manager manager, Request request) {
        this.requestDao = requestDao;
        this.managerDao = managerDao;
        this.manager = manager;
        this.request = request;
    }

    @Override
    public void run() {
        managerDao.update(manager.getId(), request.getId());
        requestDao.update(request.getId(), RequestStatus.IN_PROCESSING);
        System.out.println("Статус заявки " + request.getId() + " измененен на PROCESSING");
        request.setStartProcTime(LocalDateTime.now());
        requestDao.setEmployee(request.getId(), manager.getName());
        System.out.println("Менеджер " + manager.getId() + " взял заявку");
        try {
            Thread.sleep(request.getLeadTime() * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        managerDao.update(manager.getId(), 0);
        requestDao.update(request.getId(), RequestStatus.DONE);
        System.out.println("Статус заявки " + request.getId() + " измененен на DONE");
    }
}

class DirectorHandler implements Runnable {

    RequestDao requestDao;
    DirectorDao directorDao;
    Director director;
    Request request;

    public DirectorHandler(RequestDao requestDao, DirectorDao directorDao, Director director, Request request) {
        this.requestDao = requestDao;
        this.directorDao = directorDao;
        this.director = director;
        this.request = request;
    }

    @Override
    public void run() {
        directorDao.update(director.getId(), request.getId());
        requestDao.update(request.getId(), RequestStatus.IN_PROCESSING);
        System.out.println("Статус заявки " + request.getId() + " измененен на PROCESSING");
        requestDao.setEmployee(request.getId(), director.getName());
        System.out.println("Директор взял заявку");
        try {
            Thread.sleep(request.getLeadTime() * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        directorDao.update(director.getId(), 0);
        requestDao.update(request.getId(), RequestStatus.DONE);
        System.out.println("Статус заявки " + request.getId() + " измененен на DONE");
    }
}

