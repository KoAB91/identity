package dao;

import entity.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientDao implements IDao<Client> {

    private static String connectUrl = MyConfig.setConfig("url");
    private static String user = MyConfig.setConfig("user");
    private static String password = MyConfig.setConfig("password");
    private Connection connection;
    private static ClientDao instance = null;

    public static ClientDao getInstance() {
        if (instance == null)
            instance = new ClientDao();
        return instance;
    }

    private ClientDao() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(connectUrl, user, password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Client (" +
                "id INTEGER PRIMARY KEY NOT NULL," +
                "login VARCHAR(50) NOT NULL," +
                "password VARCHAR(50) NOT NULL);";
        try (Statement statement = this.connection.createStatement()) {
            int row = statement.executeUpdate(sql);
            System.out.println(row);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(Client client) {
        String sql = "INSERT INTO Client (id, login, password)" + "VALUES (?, ?, ?);";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, client.getId());
            statement.setString(2, client.getLogin());
            statement.setString(3, client.getPassword());
            int row = statement.executeUpdate();
            System.out.println(row);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Client> getAll() {
        String sql = "SELECT * FROM Client;";
        try (Statement statement = this.connection.createStatement()) {
            List<Client> clientList = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Client client = new Client();
                client.setId(resultSet.getInt("id"));
                client.setLogin(resultSet.getString("login"));
                client.setPassword(resultSet.getString("password"));
                clientList.add(client);
            }
            return clientList;

        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public Client getById(int id) {
        String sql = "SELECT * FROM Client WHERE id=?;";
        Client client = new Client();
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            client.setId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return client;
    }


    @Override
    public <Integer> void update(int id, Integer requestId) {
//        String sql = "UPDATE Director SET requestId=? WHERE id=?";
//        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
//            statement.setInt(1, (int) requestId);
//            statement.setInt(2, id);
//            int row = statement.executeUpdate();
//            System.out.println(row);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Client WHERE id=?;";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}