package org.example.models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Dish {
    public long getId() {
        return id;
    }

    private long id;

    public String getName() {
        return name;
    }

    private String name;

    public int getPrice() {
        return price;
    }

    public int getComplexity() {
        return complexity;
    }

    private int price;
    private int complexity;
    private int number;

    public Dish(long id, String name, int price, int complexity, int number) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.complexity = complexity;
        this.number = number;
    }

    public static Dish getDishByResultSet(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        int price = resultSet.getInt("price");
        int complexity = resultSet.getInt("complexity");
        int number = resultSet.getInt("number");
        return new Dish(id, name, price, complexity, number);
    }

    @Override
    public String toString() {
        return name + " | " + price + " руб. | " + number + " порций, готовится " + complexity + " мин.";
    }
}
