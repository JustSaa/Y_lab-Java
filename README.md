# Personal Finance Tracker

Учебный проект по Java Core с использованием SOLID-принципов, и unit-тестирования (JUnit5, Mockito, AssertJ).

## Структура проекта

| Папка      | Описание                                            | Ссылка                       |
|------------|-----------------------------------------------------|------------------------------|
| homework_1 | Реализация консольного приложения                   | [Перейти →](homework_1/)     |
| homework_2 | Реализация приложения в БД (PostgresSql and Docker) | [Перейти →](tree/homework_2) |

---
Описание проекта

Финансовый трекер — это консольное приложение для управления финансами. Пользователь может вести учет доходов и расходов, устанавливать бюджет, создавать финансовые цели и анализировать свои расходы.

Приложение реализовано с использованием JDBC для работы с базой данных PostgreSQL, а для миграций используется Liquibase. Для тестирования применяется TestContainers.

Технологии
•	Язык: Java 17
•	База данных: PostgreSQL
•	Библиотеки:
•	JDBC (работа с БД)
•	Liquibase (миграции)
•	TestContainers (интеграционные тесты)
•	Инструменты:
•	Docker + Docker Compose (развертывание PostgreSQL)
•	JUnit 5, Mockito, AssertJ (юнит-тестирование)
•	Maven (сборка и управление зависимостями)

Структура проекта

📂 Y_lab_java
├── 📂 src
│   ├── 📂 main
│   │   ├── 📂 java/homework_1
│   │   │   ├── 📂 config (конфигурация подключения к БД и Liquibase)
│   │   │   ├── 📂 domain (сущности: User, Transaction, Budget, Goal)
│   │   │   ├── 📂 repositories (репозитории для работы с БД)
│   │   │   ├── 📂 services (логика приложения)
│   │   │   ├── 📂 ui (консольный интерфейс)
│   │   │   ├── ApplicationDB.java (основной класс для запуска)
│   ├── 📂 test
│   │   ├── 📂 java/homework_1
│   │   │   ├── 📂 repositories (интеграционные тесты с TestContainers)
│   │   │   ├── 📂 services (юнит-тесты)
│   │   │   ├── BaseTest.java (настройка тестовой БД)
├── 📂 db
│   ├── 📂 changelog (Liquibase-миграции)
│   │   ├── db.changelog-master.xml (основной changelog)
│   │   ├── db.changelog-00-create-schema.xml (создание схемы)
│   │   ├── db.changelog-01-create-tables.xml (создание таблиц)
│   │   ├── db.changelog-02-initial-data.xml (начальные данные)
├── 📄 docker-compose.yml (развертывание БД)
├── 📄 pom.xml (Maven-зависимости)
├── 📄 README.md (инструкция)

Установка и запуск

1. Развертывание базы данных с Docker

docker-compose up -d

Это создаст и запустит контейнер с PostgreSQL.

2. Запуск миграций Liquibase

mvn liquibase:update

Миграции создадут все необходимые таблицы и заполнят базу начальными данными.

3. Запуск приложения

mvn clean package
java -jar target/Y_lab_java.jar

Тестирование

Для запуска всех тестов:

mvn test

Тесты используют TestContainers, автоматически создавая временную базу данных в контейнере PostgreSQL.

⸻