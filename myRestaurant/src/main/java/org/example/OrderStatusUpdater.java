package org.example;

import org.example.models.Order;
import org.example.models.Status;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class OrderStatusUpdater {
    MyDataBase myDataBase = MyDataBase.getInstance();
    private ExecutorService executor = Executors.newFixedThreadPool(10);

    private static class OrderStatusUpdaterHolder {
        public static final OrderStatusUpdater HOLDER_INSTANCE = new OrderStatusUpdater();
    }

    public static OrderStatusUpdater getInstance() {
        return OrderStatusUpdaterHolder.HOLDER_INSTANCE;
    }

    public void updateOrderStatus(Order order) {
        executor.submit(() -> {
            while (order.getStatus() != Status.READY) {
                try {
                    Order myOrder;
                    switch (order.getStatus()) {
                        case ACCEPTED:
                            TimeUnit.SECONDS.sleep(4);
                            myDataBase.setOrderStatus(order, Status.PREPARING);
                            break;
                        case PREPARING:
                            int seconds = 0;
                            do {
                                TimeUnit.SECONDS.sleep(order.totalComplexity() - seconds);
                                seconds = order.totalComplexity();
                                myOrder = myDataBase.getOrderByIdAndUser(order.getId(), order.getUser());
                            }
                            while (myOrder.totalComplexity() > seconds);
                            myDataBase.setOrderStatus(order, Status.READY);
                            break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    System.out.println("Произошла ошибка, связанная с SQL: " + e.getMessage());
                }
            }
        });
    }
}
