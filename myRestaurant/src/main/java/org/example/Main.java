package org.example;

import java.sql.*;

public class Main {
    private static Restaurant restaurant = Restaurant.getInstance();

    public static void main(String[] args) {
        restaurant.mainPage();
    }
}