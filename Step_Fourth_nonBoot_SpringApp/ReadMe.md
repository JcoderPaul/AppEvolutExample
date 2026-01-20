### Маркетплейс - Step Fourth (nonBoot Spring and AOP)

Web приложение для управления каталогом товаров на базе nonBoot Spring. Необходимо обновить сервис согласно следующим 
требованиям и ограничениям.

---
#### Требования и ограничения:
- Java-конфигурация приложения - Кастомные конфигурационные файлы заменить на application.yml.
- Удалить сервлеты (предыдущий шаг), реализовать Rest-контроллеры (Spring MVC) - Swagger + Swagger UI.
- Аспекты переписать на Spring AOP.
- Внедрение зависимостей ТОЛЬКО через конструктор - Удалить всю логику создания сервисов, репозиториев и тд. Приложение должно полностью управляться Spring.
- Добавить тесты на контроллеры (WebMVC).

---
#### Вариант реализации:

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

- [aop](./src/main/java/me/oldboy/market/aop) - папка содержит аннотации и аспекты для реализации АОП (применены к слою контроллеров);
- [config](./src/main/java/me/oldboy/market/config) - папка содержит основные конфигурационные файлы приложения;
  - [AppDataSourceConfig](./src/main/java/me/oldboy/market/config/data_source/AppDataSourceConfig.java) - класс конфигуратор: связи с БД, запуска миграции БД, менеджера транзакций;
  - [jwt_config](./src/main/java/me/oldboy/market/config/jwt_config) - набор классов отвечающих за генерацию и обработку JWT токена;
  - [main_config](./src/main/java/me/oldboy/market/config/main_config) - основной конфигурационный файл приложения;
  - [security_config](./src/main/java/me/oldboy/market/config/security_config) - основной конфигурационный файл отвечающий за безопасность приложения;
  - [security_details](./src/main/java/me/oldboy/market/config/security_config) - набор классов позволяющих стыковать "наши правила" аутентификации со Spring;
  - [swagger](./src/main/java/me/oldboy/market/config/swagger) - конфигурация OpenApi (Swagger) документирования;
  - [ContextApp](./src/main/java/me/oldboy/market/config/context/AppContextBuilder.java) - класс инициализирующий и связывающий основные рабочие части приложения;
- [controllers](./src/main/java/me/oldboy/market/controllers) - набор классов отвечающих за обработку входящих HTTP запросов и их валидацию;
- [dto](./src/main/java/me/oldboy/market/dto) - кассы "межслойного" взаимодействия, используемый для передачи данных между различными подсистемами; 
- [entity](./src/main/java/me/oldboy/market/entity) - ключевые сущности проекта;
- [exceptions](./src/main/java/me/oldboy/market/exceptions) - набор исключений бросаемых приложением в процессе работы;
- [mapper](./src/main/java/me/oldboy/market/mapper) - набор интерфейсов описывающих преобразования "entity to dto" и обратно;
- [repository](./src/main/java/me/oldboy/market/repository) - классы и методы взаимодействия со слоем данных;
- [services](./src/main/java/me/oldboy/market/services) - классы основной бизнес логики;

---
- [docker-compose.yaml](docker-compose.yaml) - конфигурация сборки PastgreSQL контейнера;
- [.env](.env) - файл для хранения переменных окружения (в нашем случае доступ к БД);

---
- [Тесты](./src/test) - тесты (150 шт.), согласно расчетам IDE покрытие: Class - 86%, Method - 79%, Line - 84%; 
- Полное покрытие JavaDoc.

---
#### Запуск и тестирование приложение:

Для сборки и запуска необходимо сделать локальную копию приложения.

---
- **Первый вариант** (наиболее простой и надежный для целей изучения и тестов): Запуск и тестирование проекта в среде разработки - IntelliJ IDEA с подключенным TomCat контейнером сервлетов.

---
- **Второй вариант**: Консольный запуск приложения с развертыванием в TomCat контейнере сервлетов. 

Необходимо помнить, что данный проект взаимодействует с PostgreSQL БД развернутой в отдельном контейнере, 
см. [docker-compose.yaml](docker-compose.yaml). Контейнер должен быть запущен и работать перед запуском 
приложения и его тестами, т.к. для проверки работоспособности слоев [repository](./src/test/java/me/oldboy/market/integration/repository),
[services](./src/test/java/me/oldboy/market/integration/services) и [controllers](./src/test/java/me/oldboy/market/integration/controllers) 
используется функционал [Testcontainer-а](./src/test/java/me/oldboy/market/config/test_data_source/TestContainerInit.java).

**Шаг 1.** - (На машине должен быть установлен полнофункциональный Docker) В корне текущего проекта находится файл "инструкция"
сборки и запуска контейнера с БД - [docker-compose.yaml](docker-compose.yaml). Запускаем консоль и переходим в корень проекта,
запускаем команду:

    docker-compose up

Убеждаемся, что процесс развертки контейнера прошел нормально и контейнер БД запущен.

**Шаг 2.** - Запуск тестов. Из корня проекта в консоли передается команда (в зависимости от ОС могут быть проблемы с кодировкой):

    gradlew test

**Шаг 3.** - Сборка war архива. Из корня проекта в консоли передается команда:

    gradlew war

Если все прошло нормально, то в папке build/libs/ проекта появится файл market.war

**Шаг 4.** - Поместит полученный war архив в корне папки webapps вашего TomCat-a (либо вручную, простым копированием), 
либо используя уже запущенный контейнер по средствам его web-интерфейса (кнопка Manager App) произвести загрузку 
вышеописанного *.war архива.

**Особенности дальнейшего взаимодействия с приложением (если вы выбрали 2-й вариант запуска)**

Ниже, в следующем разделе, приведены все endpoint-ы для работы с приложением, однако в зависимости от способа 
развертывания его будут немного отличаться пути доступа. В случае создания market.war и применения его на внешнем 
TomCat-е к уже существующему пути будет прибавлен `префикс` генерируемый сервлет контейнером - сообразно имени 
war архива. 

Т.е. если у нас есть, например endpoint:

    GET server_path... /market/products/

То теперь, перед ним появится имя war архива market.war:

    GET server_path... /market/market/products/

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
### Доступ к Swagger (default config)

    http://localhost:8080/swagger-ui/index.html
    http://localhost:8080/v3/api-docs

**Помним** При развертывании приложения в контейнере сервлетов у Swagger-а может появиться `префикс` в стандартном пути,
например:

    http://localhost:8080/market/swagger-ui/index.html

---
#### Особенности

Техническое задание данного проекта не предусматривает возможность регистрации нового пользователя, ТОЛЬКО авторизация 
пользователя и все, судя по всему предполагается, что новые пользователи добавляются (и управляются) другим способом или
из другого сервиса приложения. Однако на слое репозиториев реализована и протестирована полная CRUD функциональность 
с управляемым просачиванием ее "на верхние слои приложения" не только для пользователей, но и для всех применяемых 
сущностей - "на перспективу". 

---
#### Работа над ошибками

- Метод update класса реализующего [ProductService](./src/main/java/me/oldboy/market/services/ProductServiceImpl.java) вместо boolean, теперь возвращает [ProductReadDto](./src/main/java/me/oldboy/market/dto/product/ProductReadDto.java);
- В более емких тестах (где на один метод приходится более 3-х тестов) логическое разделение блоков комментариями заменено на @Nested;
- Все тесты аннотированы и описаны в @DisplayName;
- В наиболее "широких утверждениях" применен SoftAssertions;