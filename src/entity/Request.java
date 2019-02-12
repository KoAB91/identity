package entity;

import java.time.*;

public class Request {

    // номер заявки
    private int id;
    // id клиента
    private int clientId;
    //время создания заявки
    private LocalDateTime creationTime;
    // время начала обработки заявки
    private LocalDateTime startProcTime;
    // время выполнения заявки
    private int leadTime;
    // время окончания выполнения заявки
    private LocalDateTime endTime;
    // статус заявки
    private RequestStatus requestStatus;
    // сотрудник, который взял заявку в обработку
    private String employee;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public int getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(int leadTime) {
        this.leadTime = leadTime;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public LocalDateTime getStartProcTime() {
        return startProcTime;
    }

    public void setStartProcTime(LocalDateTime startProcTime) {
        this.startProcTime = startProcTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", creationTime=" + creationTime +
                ", startProcTime=" + startProcTime +
                ", leadTime=" + leadTime +
                ", endTime=" + endTime +
                ", requestStatus=" + requestStatus +
                ", employee='" + employee + '\'' +
                '}';
    }
}

