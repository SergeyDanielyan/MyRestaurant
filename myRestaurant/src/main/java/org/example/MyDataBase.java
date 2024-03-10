package org.example;

import org.example.models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MyDataBase {
    private Statement statement;
    private PreparedStatement preparedStatement;
    private Connection connection;


    public MyDataBase() {
        final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/restaurant";
        final String USER = "postgres";
        final String PASS = "qwft67h9";

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return;
        }

        try {
            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();
        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return;
        }
    }

    private static class DataBaseHolder {
        public static final MyDataBase HOLDER_INSTANCE = new MyDataBase();
    }

    public static MyDataBase getInstance() {
        return DataBaseHolder.HOLDER_INSTANCE;
    }

    public Order createOrder(User user) throws SQLException {
        deleteCreatedOrder(user);
        preparedStatement = connection.prepareStatement("INSERT INTO orders (status, user_id) VALUES ('создан', "
                + user.getId() + ")");
        preparedStatement.executeUpdate();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM orders WHERE status = 'создан' AND user_id = "
                + user.getId());
        if (!resultSet.next()) {
            return null;
        }
        return Order.getOrderByResultSetAndUser(resultSet, user);
    }

    public User createUser(String username, String password) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE username = '" + username + "'");
        if (resultSet.next()) {
            return null;
        }
        preparedStatement = connection.prepareStatement("INSERT INTO users (username, password, " +
                "is_admin) VALUES ('" + username + "', '" + password + "', FALSE)");
        preparedStatement.executeUpdate();
        resultSet = statement.executeQuery("SELECT * FROM users WHERE username = '" + username + "'");
        if (!resultSet.next()) {
            return null;
        }
        return new User(resultSet.getLong("id"), resultSet.getString("username"),
                resultSet.getString("password"));
    }

    public User getUser(String username, String password) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE username = '" +
                username + "'");
        if (!resultSet.next()) {
            return null;
        }
        User user = User.getUserByResultSet(resultSet);
        if (!user.getPassword().equals(password)) {
            return null;
        }
        return user;
    }

    public List<Order> getOrdersList(User user) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM orders WHERE user_id = "
                + user.getId());
        List<Order> orders = new ArrayList<>();
        while (resultSet.next()) {
            orders.add(Order.getOrderByResultSetAndUser(resultSet, user));
        }
        return orders;
    }

    public List<Dish> getDishesList() throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM dishes");
        List<Dish> dishes = new ArrayList<>();
        while (resultSet.next()) {
            dishes.add(Dish.getDishByResultSet(resultSet));
        }
        return dishes;
    }

    public Dish addDish(String name, int price, int complexity, int number) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM dishes WHERE name = '" + name + "'");
        if (resultSet.next()) {
            return null;
        }
        preparedStatement = connection.prepareStatement("INSERT INTO dishes (name, price, complexity, number)" +
                " VALUES ('" + name + "', " + price + ", " + complexity + ", " + number + ")");
        preparedStatement.executeUpdate();
        resultSet = statement.executeQuery("SELECT * FROM dishes WHERE name = '" + name + "'");
        if (!resultSet.next()) {
            return null;
        }
        return new Dish(resultSet.getLong("id"), resultSet.getString("name"),
                resultSet.getInt("price"), resultSet.getInt("complexity"),
                resultSet.getInt("number"));
    }

    public boolean deleteDish(String name) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM dishes WHERE name = '" + name + "'");
        if (!resultSet.next()) {
            return false;
        }
        preparedStatement = connection.prepareStatement("DELETE FROM dishes WHERE name = '" + name + "'");
        preparedStatement.executeUpdate();
        return true;
    }

    public Dish updateDish(String name, int price, int complexity, int number) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM dishes WHERE name = '" + name + "'");
        if (!resultSet.next()) {
            return null;
        }
        preparedStatement = connection.prepareStatement("UPDATE dishes SET price = " + price + ", " +
                "complexity = " + complexity + ", number = " + number);
        preparedStatement.executeUpdate();
        resultSet = statement.executeQuery("SELECT * FROM dishes WHERE name = '" + name + "'");
        if (!resultSet.next()) {
            return null;
        }
        return new Dish(resultSet.getLong("id"), resultSet.getString("name"),
                resultSet.getInt("price"), resultSet.getInt("complexity"),
                resultSet.getInt("number"));
    }

    public Dish getDishById(long id) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM dishes WHERE id = " + id);
        if (!resultSet.next()) {
            return null;
        }
        return Dish.getDishByResultSet(resultSet);
    }

    public Dish getDishByName(String name) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM dishes WHERE name = '" + name + "'");
        if (!resultSet.next()) {
            return null;
        }
        return Dish.getDishByResultSet(resultSet);
    }

    public List<OrderDish> getOrderDishListByOrder(Order order) throws SQLException {
        order.getOrderDishes().clear();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM order_dish WHERE order_id = " + order.getId());
        List<Long> dishesId = new ArrayList<>();
        while (resultSet.next()) {
            dishesId.add(resultSet.getLong("dish_id"));
        }
        for (Long dishId : dishesId) {
            order.getOrderDishes().add(getOrderDishByOrderAndDish(order, getDishById(dishId)));
        }
        return order.getOrderDishes();
    }

    public Order getOrderByIdAndUser(long id, User user) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM orders WHERE id = " + id);
        if (!resultSet.next()) {
            return null;
        }
        Order order = Order.getOrderByResultSetAndUser(resultSet, user);
        order.setOrderDishes(getOrderDishListByOrder(order));
        return order;
    }

    public OrderDish createOrderDishByNameAndNumber(String name, int number, Order order) throws SQLException {
        Dish dish = getDishByName(name);
        ResultSet resultSet = statement.executeQuery("SELECT * FROM order_dish WHERE order_id = " + order.getId()
                + " AND dish_id = " + dish.getId());
        if (resultSet.next()) {
            OrderDish orderDish = OrderDish.getOrderDishByResultSetAndOrderAndDish(resultSet, order, dish);
            preparedStatement = connection.prepareStatement("UPDATE order_dish SET number = "
                    + (orderDish.getNumber() + number) + " WHERE id = " + orderDish.getId());
            preparedStatement.executeUpdate();
            return null;
        }
        preparedStatement = connection.prepareStatement("INSERT INTO order_dish (number, order_id, dish_id) VALUES"
                + " (" + number + ", " + order.getId() + ", " + dish.getId() + ")");
        preparedStatement.executeUpdate();
        resultSet = statement.executeQuery("SELECT * FROM order_dish WHERE order_id = " + order.getId() + " AND "
                + "dish_id = " + dish.getId());
        if (!resultSet.next()) {
            return null;
        }
        return OrderDish.getOrderDishByResultSetAndOrderAndDish(resultSet, order, dish);
    }

    public OrderDish getOrderDishByOrderAndDish(Order order, Dish dish) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM order_dish WHERE order_id = " + order.getId()
                + " AND dish_id = " + dish.getId());
        if (!resultSet.next()) {
            return null;
        }
        return OrderDish.getOrderDishByResultSetAndOrderAndDish(resultSet, order, dish);
    }

    public void deleteOrderDishByDishName(String name) throws SQLException {
        Dish dish = getDishByName(name);
        preparedStatement = connection.prepareStatement("DELETE FROM order_dish WHERE dish_id = " + dish.getId());
        preparedStatement.executeUpdate();
    }

    public void setOrderStatus(Order order, Status status) throws SQLException {
        order.setStatus(status);
        preparedStatement = connection.prepareStatement("UPDATE orders SET status = '" + Order.getStatusName(status)
                + "' WHERE id = " + order.getId());
        preparedStatement.executeUpdate();
    }

    public void deleteOrderDishByOrder(Order order) throws SQLException {
        preparedStatement
                = connection.prepareStatement("DELETE FROM order_dish WHERE order_id = " + order.getId());
        preparedStatement.executeUpdate();
    }

    public void deleteOrder(Order order) throws SQLException {
        deleteOrderDishByOrder(order);
        preparedStatement = connection.prepareStatement("DELETE FROM orders WHERE id = " + order.getId());
        preparedStatement.executeUpdate();
    }

    public void deleteCreatedOrder(User user) throws SQLException {
        List<Order> orders = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM orders WHERE status = 'создан' AND user_id = "
                + user.getId());
        while (resultSet.next()) {
            orders.add(Order.getOrderByResultSetAndUser(resultSet, user));
        }
        for (Order order : orders) {
            deleteOrder(order);
        }
    }
}
