package dao;

import entity.Director;
import entity.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DirectorDao implements IDao<Director> {

    private static String connectUrl = MyConfig.setConfig("url");
    private static String user = MyConfig.setConfig("user");
    private static String password = MyConfig.setConfig("password");
    private Connection connection;
    private static DirectorDao instance = null;

    public static DirectorDao getInstance() {
        if (instance == null)
            instance = new DirectorDao();
        return instance;
    }

    private DirectorDao() {
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
        String sql = "CREATE TABLE IF NOT EXISTS Director (" +
                "id INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL," +
                "name VARCHAR(20) NOT NULL," +
                "role VARCHAR(20) NOT NULL," +
                "requestId INTEGER);";
        try (Statement statement = this.connection.createStatement()) {
            int row = statement.executeUpdate(sql);
//            System.out.println(row);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(Director director) {
        String sql = "INSERT INTO Director (id, name, role, requestId)" + "VALUES (?, ?, ?, ?);";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, director.getId());
            statement.setString(2, director.getName());
            statement.setString(3, director.getRole().name());
            statement.setInt(4, 0);
            int row = statement.executeUpdate();
//            System.out.println(row);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Director> getAll() {
        String sql = "SELECT * FROM Director;";
        try (Statement statement = this.connection.createStatement()) {
            List<Director> directorList = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Director director = new Director();
                director.setId(resultSet.getInt("id"));
                director.setName(resultSet.getString("name"));
                director.setRole(Role.valueOf(resultSet.getString("role")));
                director.setRequestId(resultSet.getInt("requestId"));
                directorList.add(director);
            }
            return directorList;

        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public Director getById(int id) {
        String sql = "SELECT * FROM Director WHERE id=?;";
        Director director = new Director();
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                director.setId(id);
                director.setName(resultSet.getString("name"));
                director.setRole(Role.valueOf(resultSet.getString("role")));
                director.setRequestId(resultSet.getInt("requestId"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return director;
    }

    public Director getNotBusy() {
        String sql = "SELECT * FROM Director WHERE requestId=0;";
        Director director = new Director();
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                director.setId(resultSet.getInt("id"));
                director.setName(resultSet.getString("name"));
                director.setRole(Role.valueOf(resultSet.getString("role")));
            }
            return director;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <Integer> void update(int id, Integer requestId) {
        String sql = "UPDATE Director SET requestId=? WHERE id=?";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, ((int) requestId));
            statement.setInt(2, id);
            int row = statement.executeUpdate();
//            System.out.println(row);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Director WHERE id=?;";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}