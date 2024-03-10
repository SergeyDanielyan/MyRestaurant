# MyRestaurant

## Описание проекта

Приложение реализует систему обработки заказов в ресторане, которая позволяет гостям и администраторам удобно обрабатывать заказы.

## Работа с приложением

Для работы с приложением достаточно следовать инструкциям, которые выводятся в консоли приложения. 

## Шаблоны

В 3 классах используется шаблон Одиночка, так как там гарантированно по одному экземпляру.

## Подключение базы данных

Для подключения базы данных создайте базу данных с названием restaurant в PostgreSQL. Задайте переменные USER и PASS в 17 и 18 строках значениями ващего логина и пароля соответственно.
Вот запросы создания таблиц:

CREATE TABLE IF NOT EXISTS public.dishes
(
    id bigint NOT NULL DEFAULT nextval('dish_seq'::regclass),
    name character varying(100) COLLATE pg_catalog."default" NOT NULL,
    price integer NOT NULL,
    complexity integer NOT NULL,
    "number" integer NOT NULL,
    CONSTRAINT dishes_pkey PRIMARY KEY (id)
)

CREATE TABLE IF NOT EXISTS public.users
(
    id bigint NOT NULL DEFAULT nextval('users_seq'::regclass),
    username character varying(25) COLLATE pg_catalog."default" NOT NULL,
    password character varying(25) COLLATE pg_catalog."default" NOT NULL,
    is_admin boolean NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id)
)

CREATE TABLE IF NOT EXISTS public.orders
(
    id bigint NOT NULL DEFAULT nextval('order_seq'::regclass),
    status character varying(15) COLLATE pg_catalog."default" NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT orders_pkey PRIMARY KEY (id),
    CONSTRAINT orders_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES public.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

CREATE TABLE IF NOT EXISTS public.order_dish
(
    id bigint NOT NULL DEFAULT nextval('order_dish_seq'::regclass),
    "number" integer NOT NULL,
    dish_id bigint NOT NULL,
    order_id bigint NOT NULL,
    CONSTRAINT order_dish_pkey PRIMARY KEY (id),
    CONSTRAINT order_dish_dish_id_fkey FOREIGN KEY (dish_id)
        REFERENCES public.dishes (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT order_dish_order_id_fkey FOREIGN KEY (order_id)
        REFERENCES public.orders (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

