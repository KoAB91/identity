package dao;

import entity.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OperatorDao implements IDao<Operator> {

    private static String connectUrl = MyConfig.setConfig("url");
    private static String user = MyConfig.setConfig("user");
    private static String password = MyConfig.setConfig("password");
    private Connection connection;
    private static OperatorDao instance = null;

    public static OperatorDao getInstance() {
        if (instance == null)
            instance = new OperatorDao();
        return instance;
    }

    private OperatorDao() {
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
        String sql = "CREATE TABLE IF NOT EXISTS Operator (" +
                "id INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL," +
                "name VARCHAR(20) NOT NULL," +
                "role VARCHAR(20) NOT NULL," +
                "requestId INTEGER);";
        try (Statement statement = this.connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(Operator operator) {
        String sql = "INSERT INTO Operator (id, name, role, requestId)" + "VALUES (?, ?, ?, ?);";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, operator.getId());
            statement.setString(2, operator.getName());
            statement.setString(3, operator.getRole().name());
            statement.setInt(4, 0);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Operator> getAll() {
        String sql = "SELECT * FROM Operator;";
        try (Statement statement = this.connection.createStatement()) {
            List<Operator> operatorList = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Operator operator = new Operator();
                operator.setId(resultSet.getInt("id"));
                operator.setName(resultSet.getString("name"));
                operator.setRole(Role.valueOf(resultSet.getString("role")));
                operator.setRequestId(resultSet.getInt("requestId"));
                operatorList.add(operator);
            }
            return operatorList;

        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public Operator getById(int id) {
        String sql = "SELECT * FROM Operator WHERE id=?;";
        Operator operator = new Operator();
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                operator.setId(id);
                operator.setName(resultSet.getString("name"));
                operator.setRole(Role.valueOf(resultSet.getString("role")));
                operator.setRequestId(resultSet.getInt("requestId"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return operator;
    }

    public Operator getNotBusy() {
        String sql = "SELECT * FROM Operator WHERE requestId=0 LIMIT 0,1;";
        Operator operator = new Operator();
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                operator.setId(resultSet.getInt("id"));
                operator.setName(resultSet.getString("name"));
                operator.setRole(Role.valueOf(resultSet.getString("role")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return operator;
    }

    @Override
    public <Integer> void update(int id, Integer requestId) {
        String sql = "UPDATE Operator SET requestId=? WHERE id=?";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, (int) requestId);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Operator WHERE id=?;";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Operator> getNotWorking() {
        String sql = "SELECT * FROM Operator WHERE requestId=0;";
        try (Statement statement = this.connection.createStatement()) {
            List<Operator> operatorList = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Operator operator = new Operator();
                operator.setId(resultSet.getInt("id"));
                operator.setName(resultSet.getString("name"));
                operator.setRole(Role.valueOf(resultSet.getString("role")));
                operatorList.add(operator);
            }
            return operatorList;

        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
