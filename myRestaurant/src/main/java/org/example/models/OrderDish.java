package org.example.models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDish {
    private long id;

    public int getNumber() {
        return number;
    }

    private int number;

    public Order getOrder() {
        return order;
    }

    public Dish getDish() {
        return dish;
    }

    Order order;
    Dish dish;

    public OrderDish(long id, int number, Order order, Dish dish) {
        this.id = id;
        this.number = number;
        this.order = order;
        this.dish = dish;
    }

    public static OrderDish getOrderDishByResultSetAndOrderAndDish(ResultSet resultSet, Order order, Dish dish)
            throws SQLException {
        long id = resultSet.getLong("id");
        int number = resultSet.getInt("number");
        return new OrderDish(id, number, order, dish);
    }

    public int amount() {
        return dish.getPrice() * number;
    }

    public int totalComplexity() {
        return dish.getComplexity() * number;
    }

    @Override
    public String toString() {
        return dish.getName() + ": " + number + " шт. - " + amount() + " руб.";
    }

    public long getId() {
        return id;
    }
}
