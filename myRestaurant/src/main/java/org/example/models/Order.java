package org.example.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private long id;

    public Status getStatus() {
        return status;
    }

    private Status status;

    public User getUser() {
        return user;
    }

    private User user;

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setOrderDishes(List<OrderDish> orderDishes) {
        this.orderDishes = orderDishes;
    }

    private List<OrderDish> orderDishes;

    public long getId() {
        return id;
    }

    public Order(long id, Status status, User user) {
        this.id = id;
        this.status = status;
        this.user = user;
        this.orderDishes = new ArrayList<>();
    }

    public static Status statusByName(String statusName) {
        switch (statusName) {
            case "создан":
                return Status.CREATED;
            case "принят":
                return Status.ACCEPTED;
            case "готовится":
                return Status.PREPARING;
            case "готов":
                return Status.READY;
            case "отдан":
                return Status.GIVEN;
            default:
                return null;
        }
    }

    public static Order getOrderByResultSetAndUser(ResultSet resultSet, User user) throws SQLException {
        long id = resultSet.getLong("id");
        String statusName = resultSet.getString("status");
        return new Order(id, statusByName(statusName), user);
    }

    public static String getStatusName(Status status) {
        switch (status) {
            case ACCEPTED:
                return "принят";
            case PREPARING:
                return "готовится";
            case CREATED:
                return "создан";
            case READY:
                return "готов";
            case GIVEN:
                return "отдан";
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return "Заказ № " + id + "; статус: " + getStatusName(status);
    }

    public List<OrderDish> getOrderDishes() {
        return orderDishes;
    }

    public int amount() {
        int sum = 0;
        for (OrderDish orderDish : orderDishes) {
            sum += orderDish.amount();
        }
        return sum;
    }

    public int totalComplexity() {
        int sum = 0;
        for (OrderDish orderDish : orderDishes) {
            sum += orderDish.amount();
        }
        return sum;
    }
}
