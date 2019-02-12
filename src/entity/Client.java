package entity;

import java.util.List;

public class Client {

    private int id;
    private String login;
    private String password;
    private List<Request> requests;

    private static final long LIMIT = 10000000000L;
    private static long last = 0;

    private static long generID() {
        long id = System.currentTimeMillis() % LIMIT;
        if ( id <= last ) {
            id = (last + 1) % LIMIT;
        }
        return last = id;
    }

    public Client(){
        id = (int) Client.generID();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
