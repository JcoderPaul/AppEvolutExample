### Маркетплейс - Step Fifth (Spring Boot App)

Web приложение для управления каталогом товаров на базе SpringBoot Framework. Необходимо обновить сервис, 
доработанный на прошлом шаге согласно следующим требованиям и ограничениям:

---
#### Требования и ограничения:
- Spring Boot 3.2.0 с использованием необходимых стартеров;
- Обновить тесты;
- Аспекты аудита и логирования вынести в стартер, сделать отдельным модулем. Один стартер должен автоматически 
подключаться, второй через аннотацию @EnableXXX 
- Swagger -> SpringDoc;

---
### Вариант реализации:

Краткое пояснение к структуре проекта:

При реализации аудит-стартера пришел к выводу, что для правильного его функционирования необходимо выделить в отдельный 
модуль функционал взаимодействия с пользователем (таблицы связаны). При проведении unit тестирования выяснилось, что теперь 
необходимо вынести функционал управления продуктами тоже в отдельный модуль - сделано. Однако позже изменив логику 
аудирования выяснил, что вынос взаимодействие с продуктом в отдельный модуль можно было избежать, но менять полученную
структуру приложения не стал (обрела некую стройность, что ли). И того, два стартера, два модуля и обработка HTTP запросов
осталась в распоряжении основного приложения.

---
**Стек:**
- Java 17
- Gradle 8.10
- Spring Framework 6.2.8
- PostgreSQL 14
- Docker  (**Version: 28.5.1 - очень важно, версии выше могут быть не совместимы с текущим Testcontainer-ом, треб. доп. конф.**)
- Liquibase 4.32.0
- Jackson
- MapStruct
- Hibernate
- Hibernate Validator
- AspectJ
- JWT
- OpenAPI (Swagger) 2.8.6

**Тестирование:**
- Testcontainers 1.21.3
- JUnit 5
- Mockito
- AssertJ
- Postman

---
#### Структура проекта:

- [oldboy-market-auditor-starter](./oldboy-market-auditor-starter) - модуль-стартер содержит функционал аудит фиксации действий пользователя с продуктами (Product), а также аудит-запись аутентификации, в реализации опирается на АОП;
- [oldboy-market-logger-starter](./oldboy-market-logger-starter) - модуль-стартер содержит функционал логирования скорости выполнения методов аннотированных как @EnableLog, в реализации опирается на АОП;
- [oldboy-market-usermanager](./oldboy-market-usermanager) - модуль отвечающий за управление и взаимодействие с пользователями (аутентификация в системе);
- [oldboy-market-productmanager](./oldboy-market-productmanager) - модуль отвечающий за управление продуктами (Product): создание, удаление, обновление, просмотр (с фильтрацией);
- [config](./src/main/java/me/oldboy/market/config) - папка содержит основные конфигурационные файлы приложения;
  - [jwt_config](./src/main/java/me/oldboy/market/config/jwt_config) - набор классов отвечающих за генерацию и обработку JWT токена;
  - [liquibase](./src/main/java/me/oldboy/market/config/liquibase) - основной конфигурационный файл миграционного фреймворка Liquibase;
  - [security_config](./src/main/java/me/oldboy/market/config/security_config) - основной конфигурационный файл отвечающий за безопасность приложения;
  - [security_details](./src/main/java/me/oldboy/market/config/security_config) - набор классов позволяющих стыковать "наши правила" аутентификации со Spring;
  - [swagger](./src/main/java/me/oldboy/market/config/swagger) - конфигурация OpenApi (Swagger) документирования;
- [controllers](./src/main/java/me/oldboy/market/controllers) - набор классов отвечающих за обработку входящих HTTP запросов и их валидацию;
- [dto](./src/main/java/me/oldboy/market/dto) - кассы "межслойного" взаимодействия, используемый для передачи данных между различными подсистемами; 
- [exceptions](./src/main/java/me/oldboy/market/exceptions) - набор исключений бросаемых приложением в процессе работы;
- [MarketApp](./src/main/java/me/oldboy/market/MarketApp.java) - основной запускаемый файл;

---
- [docker-compose.yaml](docker-compose.yaml) - конфигурация сборки PastgreSQL контейнера;
- [.env](.env) - файл для хранения переменных окружения (в нашем случае доступ к БД);

---
- [Тесты](./src/test) - тесты (154 шт.), согласно расчетам IDE покрытие: Class - 93%, Method - 79%, Line - 82%; 
- Полное покрытие JavaDoc.

---
#### Запуск и тестирование приложение:

Для сборки и запуска необходимо сделать локальную копию приложения.
- **Первый вариант** (наиболее простой и надежный для целей изучения и тестов): Запуск и тестирование проекта в среде разработки - IntelliJ IDEA.

- **Второй вариант**: Консольный запуск приложения. 

Необходимо помнить, что данный проект взаимодействует с PostgreSQL БД развернутой в отдельном контейнере, 
см. [docker-compose.yaml](docker-compose.yaml). Контейнер должен быть запущен и работать перед запуском 
приложения и его тестами, т.к. при первом запуске приложение "накатывает" стартовые миграции (создает таблицы БД и 
заполняет их тестовыми данными). Естественно для проверки работоспособности слоев [repository](./src/test/java/me/oldboy/market/integration/repository),
[services](./src/test/java/me/oldboy/market/integration/services) и [controllers](./src/test/java/me/oldboy/market/integration/controllers) 
используется функционал [Testcontainer-а](./src/test/java/me/oldboy/market/config/test_data_source/TestContainerInit.java),
который тоже требует запущенный Docker demon.

**Шаг 1.** - (На машине должен быть установлен полнофункциональный Docker) В корне текущего проекта находится файл "инструкция"
сборки и запуска контейнера с БД - [docker-compose.yaml](docker-compose.yaml). Запускаем консоль и переходим в корень проекта,
запускаем команду:

    docker-compose up

Убеждаемся, что процесс развертки контейнера прошел нормально и контейнер БД запущен.

**Шаг 2.** - Запуск тестов. Из корня проекта в консоли передается команда (в зависимости от ОС могут быть проблемы с кодировкой):

    gradlew test

**Шаг 3.** - Сборка jar архива. Из корня проекта в консоли передается команда:

    gradlew bootJar

Если все прошло нормально, то в папке build/libs/ проекта появится файл market.jar. Фактически, у вас на руках 
полноценное web-приложение. Для его запуска необходима команда (из корня папки где лежит *.jar файл, либо указать точный 
путь):

    java -jar build/libs/market.jar

Если из папки с файлом (т.е. вы перешли в нее):

    java -jar market.jar

---
#### Взаимодействие с приложением после запуска

Приложение полностью web. Взаимодействие с ним возможно при помощи любого HTTP клиента способного отправлять весь спектр 
запросов (GET, PUT, POST, DELETE и т.д.), например Postman, Insomnia. 

Приложение обладает защитой и открывает доступ к своим endpoint-ам с передачей JWT токена. Для его получения 
необходимо залогиниться в системе см. ниже:  

**Авторизация:**
- Логины: `admin@admin.ru` / `user@user.ru` / `manager@manager.ru`;
- Пароли: `1234` / `4321` / `1111`;

**API Endpoints:**

1. Авторизация ("вход в систему"), любой из 3-х предложенных выше (в теле запроса).
- `POST /market/users/login`: 

      {
        "email": "admin@admin.ru",
        "password": "1234"
      }

После удачной авторизации пользователь получает ответ вида:

      {
        "id": 1,
        "email": "admin@admin.ru",
        "role": "ADMIN",
        "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBhZG1pbi5ydSIsImlkIjoxLCJyb2xlIjoiQURNSU4iLCJleHAiOjE3NjM4Nzk0ODJ9.ulxy6t9jYVC-8d-agVpRKe6GF9dANh9OlFJyZjVtlmw"
      }

Полученный токен доступа необходимо использовать при любом запросе к интересующему endpoint-у, вставляя в соответствующее 
поле вашего HTTP клиента, в Postman это Authorization, тип аутентификации Bearer Token. 

2. Управление товаром:
- `POST /market/products/` : Создание товара (пример json object);

       {
         "name": "Примус",
         "price": 210.0,
         "categoryId": 1,
         "brandId": 4,
         "description": "5 л.ч.",
         "stockQuantity": 5
       }

- `PUT /market/products/` : Обновление существующего товара (пример json object):

       {
         "id": "8",
         "name": "Грабли",
         "price": 540.0,
         "description": "Гребут, окучивают",
         "stockQuantity": 2
       }

- `DELETE /market/products/{id}` : Удаление существующего товара по ID; 
- `GET /market/products/{id}` : Получение товара по ID;
- `GET /market/products` : Получение всех доступных товаров (список);
- `GET /market/products/categories/{categoryId}` : Получить все товары из категории с заданным ID;
- `GET /market/products/brands/{brandId}` : Получить все товары брэнда с заданным ID;
- `GET /market/products/{productId}/categories/{categoryId}` - Получение (продукта) товара по его уникальному идентификатору и идентификатору категории
- `GET /market/products/{productId}/brands/{brandId}` - Получение (продукта) товара его по уникальному идентификатору и идентификатору брэнда
- `GET /market/products/brands/{brandId}/categories/{categoryId}` - Получение списка (продуктов) товаров по брэнду и категории
- `GET /market/products/brands/{brandId}/?productName=Валенки` - Получение (продукта) товара по брэнду и названию товара

3. Просмотр категорий:
- `GET /market/categories/` : Получение всех доступных категорий товаров;
- `GET /market/categories/1` : Получение одной категории по ее ID;
- `GET /market/categories/?categoryName=Missiles` : Получение одной категории по ее названию;

4. Просмотр брэндов:
- `GET /market/brands/` : Получение всех доступных брэндов товаров;
- `GET /market/brands/1` : Получение одного брэнда по его ID;
- `GET /market/brands/?brandName=LockheedMartin` : Получение одного брэнда по его названию;

5. Просмотр аудит записей:
- `GET /market/audits` : Получить все аудит записи;
- `GET /market/audits/1` : Получить аудит запись с заданным ID;
- `GET /market/audits/?userEmail=user@user.ru` : Получить список всех аудит записей для конкретного пользователя по его email;

---
### Доступ к SpringDoc OpenAPI (Swagger) (default config)

    http://localhost:8080/swagger-ui/index.html
    http://localhost:8080/v3/api-docs

И так, вы можете исследовать работу приложения, как при помощи Postman-а, так и встроенными средствами SpringDoc OpenAPI 
(Swagger), не забудьте аутентифицироваться, получить JWT Token доступа и использовать его для доступа к endpoint-ам. 
Интерфейс Swagger имеет соответствующую кнопку для ввода token-a доступа.  

---
#### Особенности

Техническое задание данного проекта не предусматривает возможность регистрации нового пользователя, ТОЛЬКО авторизация 
пользователя и все, судя по всему предполагается, что новые пользователи добавляются (и управляются) другим способом или
из другого сервиса приложения. Однако на слое репозиториев реализована и протестирована полная CRUD функциональность 
с управляемым просачиванием ее "на верхние слои приложения" не только для пользователей, но и для всех применяемых 
сущностей - "на перспективу". 

---
#### Работа над ошибками

- Убраны все комментарии - только JavaDoc;
- Убраны * в импорте зависимостей на ключевых сущностях;
- Добавлены [утилитные классы](./src/test/java/me/oldboy/market/test_utils) для тестов;
- Метод update класса реализующего [ProductService](./oldboy-market-productmanager/src/main/java/me/oldboy/market/productmanager/core/services/ProductServiceImpl.java) вместо boolean, теперь возвращает [ProductReadDto](./oldboy-market-productmanager/src/main/java/me/oldboy/market/productmanager/core/dto/product/ProductReadDto.java);
- В более емких тестах (где на один метод приходится более 3-х тестов) логическое разделение блоков комментариями заменено на @Nested;
- Все тесты аннотированы и описаны в @DisplayName;
- В наиболее "широких утверждениях" применен SoftAssertions;