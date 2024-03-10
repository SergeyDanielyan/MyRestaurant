package org.example.models;

import org.example.models.Order;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class User {
    private long id;
    private String username;
    private String password;
    private boolean isAdmin;

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    private List<Order> orders;

    public User(long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        isAdmin = false;
        orders = new ArrayList<>();
    }

    public User(long id, String username, String password, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        orders = new ArrayList<>();
    }

    public static User getUserByResultSet(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        boolean isAdmin = resultSet.getBoolean("is_admin");
        return new User(id, username, password, isAdmin);
    }
}
