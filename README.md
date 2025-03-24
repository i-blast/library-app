## Технологии: Java, Spring Boot, Hibernate, PostgreSQL, Git, Maven/Gradle, JUnit, Mockito, Swagger

## Пример приложения:

- Добавление, удаление и редактирование книг.
- Поиск книг по названию, автору или жанру.
- Регистрация и авторизация пользователей.
- Логирование действий пользователей.
- Документирование API с использованием Swagger

## 💻 Инструкция для запуска

- Requirements: JDK 21, Gradle 8.13, Docker Compose v2.33
- Запустить сервис БД с помощью docker-compose up
- Запустить приложение (powershell)
```$env:JWT_SECRET_KEY="secret_key"; java -jar library-app-0.0.1-SNAPSHOT.jar```
- OpenAPI документация доступна по адресу http://localhost:8085/swagger-ui.html
- 

## 💡TODO

- flyway migrations...
- 
