-- Загружаем таблицу users
INSERT INTO my_market.users (user_id, email, user_pass, role)
VALUES (1, 'admin@admin.ru', '1234', 'ADMIN'),
       (2, 'user@user.ru', '4321', 'USER'),
       (3, 'manager@manager.ru', '1111', 'MANAGER');

SELECT SETVAL('my_market.users_user_id_seq', (SELECT MAX(user_id) FROM my_market.users));

-- Загружаем таблицу брэндов
INSERT INTO my_market.brands (brand_id, brand_name)
VALUES (1, 'Puma'),
       (2, 'PolarBear'),
       (3, 'Marten'),
       (4, 'Bask');

SELECT SETVAL('my_market.brands_brand_id_seq', (SELECT MAX(brand_id) FROM my_market.brands));

-- Загружаем таблицу категорий товаров
INSERT INTO my_market.categories (category_id, category_name)
VALUES (1, 'Обувь'),
       (2, 'Куртки'),
       (3, 'Снаряжение');

SELECT SETVAL('my_market.categories_category_id_seq', (SELECT MAX(category_id) FROM my_market.categories));

-- Загружаем таблицу аудит записей (если нужно, например для тестов)
INSERT INTO my_market.audits (id, created_at, created_by, action, is_success, auditable_record)
VALUES (1, '2024-11-17T16:28:29', 'admin@admin.ru', 'LOGIN', 'SUCCESS', null),
       (2, '2024-11-17T17:18:29', 'admin@admin.ru', 'LOGOUT', 'SUCCESS', null),
       (3, '2024-12-17T16:28:29', 'user@user.ru', 'LOGIN', 'SUCCESS', null);

SELECT SETVAL('my_market.audits_id_seq', (SELECT MAX(id) FROM my_market.audits));

-- Загрузка таблицы товаров (продуктов)
INSERT INTO my_market.products (id, product_name, price, category_id, brand_id, description, stock_quantity, creation_at, modified_at)
VALUES (1, 'Парка', 2200.0, 2, 4, 'Очень теплая', 3, '2024-11-17T16:28:29', '2024-12-17T16:28:29'),
       (2, 'Уги', 800.0, 1, 1, 'Уги-вуги', 4, '2024-08-17T16:28:29', '2024-08-19T16:28:29'),
       (3, 'Самахват', 400.0, 3, 3, 'Выше, быстрее, сильнее', 2, '2024-10-17T16:28:29', '2024-10-19T16:28:29');

SELECT SETVAL('my_market.products_id_seq', (SELECT MAX(id) FROM my_market.products));