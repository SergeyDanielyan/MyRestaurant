package org.example;

import org.example.models.*;

import javax.annotation.processing.SupportedSourceVersion;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Restaurant {
    long totalAmount = 0;
    private static Scanner in = new Scanner(System.in);
    private MyDataBase myDataBase = MyDataBase.getInstance();
    private OrderStatusUpdater orderStatusUpdater = OrderStatusUpdater.getInstance();

    private User myUser = null;

    private static class RestaurantHolder {
        public static final Restaurant HOLDER_INSTANCE = new Restaurant();
    }

    public static Restaurant getInstance() {
        return RestaurantHolder.HOLDER_INSTANCE;
    }


    public void registration() {
        System.out.println("Регистрация");
        System.out.print("Введите имя пользователя: ");
        String username = in.nextLine();
        System.out.print("Введите пароль: ");
        String password = in.nextLine();
        try {
            User user = myDataBase.createUser(username, password);
            if (user == null) {
                System.out.println("Пользователь с таким именем пользователя уже существует. Нажмите " +
                        "ENTER, чтобы вернуться на главную страницу");
                in.nextLine();
            } else {
                myUser = user;
            }
        } catch (SQLException e) {
            System.out.println("Произошла ошибка, связанная с SQL: " + e.getMessage());
            System.out.println("Нажмите ENTER, чтобы вернуться на главную страницу");
            in.nextLine();
            in.nextLine();
        }
    }

    public void authorization() {
        System.out.println("Авторизация");
        System.out.print("Введите имя пользователя: ");
        String username = in.nextLine();
        System.out.print("Введите пароль: ");
        String password = in.nextLine();
        try {
            User user = myDataBase.getUser(username, password);
            if (user == null) {
                System.out.println("Пользователя с таким именем пользователя и паролем нет в системе. " +
                        "Нажмите ENTER, чтобы вернуться на главную страницу");
                in.nextLine();
            } else {
                user.setOrders(myDataBase.getOrdersList(user));
                myUser = user;
            }
        } catch (SQLException e) {
            System.out.println("Произошла ошибка, связанная с SQL: " + e.getMessage());
            System.out.println("Нажмите ENTER, чтобы вернуться на главную страницу");
            in.nextLine();
        }
    }

    public void menu() {
        try {
            System.out.println("Список блюд:");
            List<Dish> dishes = myDataBase.getDishesList();
            if (dishes.size() == 0) {
                System.out.println("Список пуст");
            }
            for (Dish dish : dishes) {
                System.out.println(dish);
            }
            System.out.println("Нажмите ENTER, чтобы вернуться на главную страницу");
            in.nextLine();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка, связанная с SQL: " + e.getMessage());
            System.out.println("Нажмите ENTER, чтобы вернуться на главную страницу");
            in.nextLine();
        }
    }

    public void changeMenu() {
        while (true) {
            try {
                System.out.println("Список блюд:");
                List<Dish> dishes = myDataBase.getDishesList();
                if (dishes.size() == 0) {
                    System.out.println("Список пуст");
                }
                for (Dish dish : dishes) {
                    System.out.println(dish);
                }
                System.out.println();
                System.out.println("Напишите 'добавить name price complexity number' чтобы добавить новый тип блюда с" +
                        " заданным именем, ценой, временем выполнения (в рублях) и количеством.");
                System.out.println("Напишите 'удалить name' чтобы удалить тип блюда.");
                System.out.println("Напишите 'изменить name price complexity number' чтобы задать блюду с" +
                        " именем name цену, время выполнения (в рублях) и количество.");
                System.out.println("Напишите что-либо другое, чтобы вернуться на главную страницу");
                String queryName = in.next();
                String name;
                Dish dish;
                int price, complexity, number;
                switch (queryName) {
                    case "добавить":
                        name = in.next();
                        price = in.nextInt();
                        complexity = in.nextInt();
                        number = in.nextInt();
                        dish = myDataBase.addDish(name, price, complexity, number);
                        if (dish == null) {
                            System.out.println("Такое блюдо уже существует. Нажмите ENTER, чтобы вернуться на " +
                                    "главную страницу");
                        }
                        break;
                    case "удалить":
                        name = in.next();
                        if (!myDataBase.deleteDish(name)) {
                            System.out.println("Такого блюда не существует. Нажмите ENTER, чтобы вернуться на " +
                                    "главную страницу");
                        }
                        break;
                    case "изменить":
                        name = in.next();
                        price = in.nextInt();
                        complexity = in.nextInt();
                        number = in.nextInt();
                        dish = myDataBase.updateDish(name, price, complexity, number);
                        if (dish == null) {
                            System.out.println("Такого блюда не существует. Нажмите ENTER, чтобы вернуться на " +
                                    "главную страницу");
                        }
                        break;
                    default:
                        return;
                }
            } catch (SQLException e) {
                System.out.println("Произошла ошибка, связанная с SQL: " + e.getMessage());
                System.out.println("Нажмите ENTER, чтобы вернуться на главную страницу");
                in.nextLine();
            }
        }
    }

    public void orderInfo(long id) {
        try {
            Order order = myDataBase.getOrderByIdAndUser(id, myUser);
            System.out.println("Информация о заказе № " + id + ":");
            System.out.println("Статус: " + Order.getStatusName(order.getStatus()));
            System.out.println("Блюда: ");
            for (OrderDish orderDish : order.getOrderDishes()) {
                System.out.println(orderDish);
            }
            System.out.println("Общая стоимость: " + order.amount() + " руб.");
        } catch (SQLException e) {
            System.out.println("Произошла ошибка, связанная с SQL: " + e.getMessage());
            System.out.println("Нажмите ENTER, чтобы вернуться на предыдущую страницу");
            in.nextLine();
        }
    }

    public void createOrder() {
        try {
            Order order = myDataBase.createOrder(myUser);
            List<OrderDish> orderDishes = new ArrayList<>();
            while (true) {
                orderInfo(order.getId());
                System.out.println();
                System.out.println("Напишите 'добавить name number', чтобы добавить блюдо в number экземпляров.");
                System.out.println("Напишите 'удалить name', чтобы удалить блюдо.");
                System.out.println("Напишите 'завершить', чтобы завершить создание заказа.");
                String query = in.next();
                String name;
                switch (query) {
                    case "добавить":
                        name = in.next();
                        int number = in.nextInt();
                        OrderDish orderDish = myDataBase.createOrderDishByNameAndNumber(name, number, order);
                        if (orderDish == null) {
                            orderDish = myDataBase.getOrderDishByOrderAndDish(order, myDataBase.getDishByName(name));
                            for (int i = 0; i < orderDishes.size(); ++i) {
                                if (orderDishes.get(i).getOrder().getId() == orderDish.getOrder().getId()
                                        && orderDishes.get(i).getDish().getId() == orderDish.getDish().getId()) {
                                    orderDishes.set(i, orderDish);
                                    break;
                                }
                            }
                        } else {
                            orderDishes.add(orderDish);
                            order.getOrderDishes().add(orderDish);
                        }
                        break;
                    case "удалить":
                        name = in.next();
                        myDataBase.deleteOrderDishByDishName(name);
                        orderDishes.removeIf(orderDish1 -> orderDish1.getDish().getName().equals(name));
                        break;
                    case "завершить":
                        myDataBase.setOrderStatus(order, Status.ACCEPTED);
                        orderStatusUpdater.updateOrderStatus(order);
                        return;
                    default:
                        break;
                }
            }
        } catch (SQLException e) {
            System.out.println("Произошла ошибка, связанная с SQL: " + e.getMessage());
            System.out.println("Нажмите ENTER, чтобы вернуться на предыдущую страницу");
            in.nextLine();
        }
    }

    public void changeOrder(long id) {
        try {
            Order order = myDataBase.getOrderByIdAndUser(id, myUser);
            if (order.getStatus() == Status.READY) {
                System.out.println("Заказ уже готов, можете его забрать");
                return;
            }
            List<OrderDish> orderDishes = order.getOrderDishes();
            while (true) {
                orderInfo(id);
                System.out.println();
                System.out.println("Напишите 'добавить name number', чтобы добавить блюдо в number экземпляров.");
                System.out.println("Напишите 'удалить name', чтобы удалить блюдо.");
                System.out.println("Напишите что-либо другое, чтобы завершить изменение заказа.");

                String query = in.next();
                String name;
                switch (query) {
                    case "добавить":
                        name = in.next();
                        int number = in.nextInt();
                        OrderDish orderDish = myDataBase.createOrderDishByNameAndNumber(name, number, order);
                        if (orderDish == null) {
                            orderDish = myDataBase.getOrderDishByOrderAndDish(order, myDataBase.getDishByName(name));
                            for (int i = 0; i < orderDishes.size(); ++i) {
                                if (orderDishes.get(i).getOrder().getId() == orderDish.getOrder().getId()
                                        && orderDishes.get(i).getDish().getId() == orderDish.getDish().getId()) {
                                    orderDishes.set(i, orderDish);
                                    break;
                                }
                            }
                        } else {
                            orderDishes.add(orderDish);
                        }
                        break;
                    case "удалить":
                        name = in.next();
                        myDataBase.deleteOrderDishByDishName(name);
                        orderDishes.removeIf(orderDish1 -> orderDish1.getDish().getName().equals(name));
                        break;
                    default:
                        return;
                }
            }
        } catch (SQLException e) {
            System.out.println("Произошла ошибка, связанная с SQL: " + e.getMessage());
            System.out.println("Нажмите ENTER, чтобы вернуться на предыдущую страницу");
            in.nextLine();
        }
    }

    public void payOrder(long id) {
        try {
            Order order = myDataBase.getOrderByIdAndUser(id, myUser);
            if (order.getStatus() != Status.READY) {
                System.out.println("Заказ не готов");
                return;
            }
            totalAmount += order.amount();
            myDataBase.deleteOrder(order);
            System.out.println("Оплата прошла успешно.");
        } catch (SQLException e) {
            System.out.println("Произошла ошибка, связанная с SQL: " + e.getMessage());
            System.out.println("Нажмите ENTER, чтобы вернуться на предыдущую страницу");
            in.nextLine();
        }
    }

    public void ordersUpdater() {
        while (true) {
            try {
                System.out.println("Ваши заказы:");
                List<Order> orders = myDataBase.getOrdersList(myUser);
                if (orders.size() == 0) {
                    System.out.println("Заказов нет");
                } else {
                    for (Order order : orders) {
                        System.out.println(order);
                    }
                }
                System.out.println();
                System.out.println("Напишите 'создать', чтобы создать новый заказ.");
                System.out.println("Напишите 'инфо id', чтобы вывести информацию о заказе с номером id");
                System.out.println("Напишите 'изменить id', чтобы внести изменения в заказ с номером id");
                System.out.println("Напишите 'оплатить id', чтобы оплатить и забрать заказ с номером id, если он " +
                        "уже готов");
                System.out.println("Напишите 'удалить id', чтобы отменить заказ с номером id");
                System.out.println("Напишите что-либо другое, чтобы вернуться на главную страницу");
                String queryName = in.next();
                long id;
                switch (queryName) {
                    case "инфо":
                        id = in.nextLong();
                        orderInfo(id);
                        break;
                    case "создать":
                        createOrder();
                        break;
                    case "изменить":
                        id = in.nextLong();
                        changeOrder(id);
                        break;
                    case "удалить":
                        id = in.nextLong();
                        myDataBase.deleteOrder(myDataBase.getOrderByIdAndUser(id, myUser));
                        break;
                    case "оплатить":
                        id = in.nextLong();
                        payOrder(id);
                        break;
                    default:
                        return;
                }
            } catch (SQLException e) {
                System.out.println("Произошла ошибка, связанная с SQL: " + e.getMessage());
                System.out.println("Нажмите ENTER, чтобы вернуться на главную страницу");
                in.nextLine();
            }
        }
    }

    public boolean mainPageNoName() {   // Возвращает false, если требуется завершить программу.
        System.out.println("Добро пожаловать в систему управления заказами!");
        System.out.println("Напишите 'войти', чтобы войти в систему.");
        System.out.println("Напишите 'зарегистрироваться', чтобы создать аккаунт в системе.");
        System.out.println("Напишите 'меню', чтобы вывести список блюд.");
        System.out.println("Напишите 'закрыть', чтобы завершить выполнение программы.");
        String query = in.nextLine();
        switch (query) {
            case "войти":
                authorization();
                break;
            case "зарегистрироваться":
                registration();
                break;
            case "меню":
                changeMenu();
                break;
            case "закрыть":
                return false;
            default:
                System.out.println("Вы ввели неправильный запрос. Нажмите ENTER, чтобы потом повторить попытку.");
                in.nextLine();
                break;
        }
        return true;
    }

    public boolean mainPageVisitor() {
        System.out.println(myUser.getUsername() + ", добро пожаловать в систему управления заказами!");
        System.out.println("Напишите 'меню', чтобы вывести список блюд.");
        System.out.println("Напишите 'заказы', чтобы вывести список заказов или сделать заказ.");
        System.out.println("Напишите 'выйти', чтобы войти в систему.");
        System.out.println("Напишите 'закрыть', чтобы завершить выполнение программы.");
        String query = in.nextLine();
        switch (query) {
            case "выйти":
                myUser = null;
                break;
            case "меню":
                changeMenu();
                break;
            case "закрыть":
                return false;
            case "заказы":
                ordersUpdater();
                break;
            default:
                System.out.println("Вы ввели неправильный запрос. Нажмите ENTER, чтобы потом повторить попытку.");
                in.nextLine();
                break;
        }
        return true;
    }

    public boolean mainPageAdmin() {
        System.out.println(myUser.getUsername() + ", добро пожаловать в систему управления заказами!");
        System.out.println("Напишите 'меню', чтобы вывести список блюд.");
        System.out.println("Напишите 'выручка', чтобы вывести выручку.");
        System.out.println("Напишите 'выйти', чтобы создать аккаунт в системе.");
        System.out.println("Напишите 'закрыть', чтобы завершить выполнение программы.");
        String query = in.nextLine();
        switch (query) {
            case "выйти":
                myUser = null;
                break;
            case "меню":
                changeMenu();
                break;
            case "закрыть":
                return false;
            case "выручка":
                System.out.println("Общая выручка составляет " + totalAmount + " руб.");
            default:
                System.out.println("Вы ввели неправильный запрос. Нажмите ENTER, чтобы потом повторить попытку.");
                in.nextLine();
                break;
        }
        return true;
    }

    public void mainPage() {
        while (true) {
            if (myUser == null) {
                if (!mainPageNoName()) {
                    break;
                }
            } else if (myUser.isAdmin()) {
                if (!mainPageAdmin()) {
                    break;
                }
            } else {
                if (!mainPageVisitor()) {
                    break;
                }
            }
        }
    }
}
