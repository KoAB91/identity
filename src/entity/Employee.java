package entity;

abstract class Employee {

    // id сотрудника
    private int id;

    // имя сорудника
    private String name;

    // id заявки, которую он выполняет
    private int requestId;

    // роль сотрудника
    private Role role;

    public Employee(Role role){
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }


}
