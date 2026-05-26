<div align="center">

<img src="https://img.shields.io/badge/-%F0%9F%92%96%20HeartSync-ff4d6d?style=for-the-badge&labelColor=1a1a2e&color=ff4d6d" alt="HeartSync" height="60"/>

# 💖 HeartSync

### *Desktop Dating Application — курсова робота*

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-2196F3?style=flat-square&logo=java&logoColor=white)](https://openjfx.io/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-Aiven%20Cloud-4479A1?style=flat-square&logo=mysql&logoColor=white)](https://aiven.io/)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=flat-square&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Academic-purple?style=flat-square)](LICENSE)

---

*Настільний додаток для знайомств з надійним захистом даних, хмарною базою даних та сучасним UI*

</div>

---

## 📖 Про проєкт

**HeartSync** — це десктопний застосунок для знайомств, розроблений на базі **JavaFX** та **Spring Framework**. Проєкт створений як курсова робота і реалізує повноцінну платформу для пошуку партнерів з акцентом на безпеку персональних даних та зручність використання.

Додаток використовує хмарну базу даних **Aiven (Europe)** для зберігання профілів, шифрування **AES-256** для захисту чутливої інформації та **BCrypt** для безпечного зберігання паролів.

---

## ✨ Функціональність

| Модуль | Опис |
|--------|------|
| 🔐 **Автентифікація** | Реєстрація та вхід з BCrypt-хешуванням паролів |
| 👤 **Профілі** | Створення та редагування особистих анкет |
| 💝 **Матчинг** | Алгоритм підбору сумісних партнерів |
| 💬 **Повідомлення** | Система особистого листування |
| 📊 **Експорт даних** | Генерація звітів у форматах Excel (.xlsx) та PDF |
| 🔒 **Шифрування** | AES-256 для захисту персональних даних |
| 📧 **Email-сервіс** | Відправка повідомлень через Spring Mail |
| 🛡️ **Безпека** | Spring Security Crypto + валідація даних |

---

## 🛠️ Технологічний стек

### Backend
```
Java 21              — основна мова розробки
Spring Boot 3.4.1    — фреймворк застосунку
Spring JDBC          — робота з базою даних
Spring Context       — IoC контейнер
Spring AOP           — аспектно-орієнтоване програмування
Spring Mail          — відправка email
Spring Security      — BCrypt хешування
```

### Frontend
```
JavaFX 21            — UI фреймворк
AtlantaFX 2.0.1      — сучасна тема оформлення
FXML                 — декларативна розмітка інтерфейсу
CSS                  — стилізація компонентів
```

### База даних
```
MySQL (Aiven Europe) — хмарна реляційна БД
Host: mysql-1daccc1b-nellyturkanuch-3551.c.aivencloud.com:12330
```

### Інструменти та бібліотеки
```
Apache POI 5.2.5     — генерація Excel-файлів
Apache PDFBox 3.0.1  — генерація PDF-документів
Lombok               — зменшення boilerplate-коду
BCrypt               — хешування паролів
AES-256              — шифрування даних
JUnit 5 + H2         — тестування
Maven                — збірка проєкту
```

---

## 🚀 Запуск проєкту

### Передумови

- ☕ **Java 21** або новіша
- 📦 **Apache Maven 3.8+**
- 🌐 Підключення до інтернету (для Aiven Cloud DB)

### Клонування репозиторію

```bash
git clone https://github.com/Nellly-xjp/HeartSync.git
cd HeartSync/HeartSync-main
```

### Налаштування підключення до БД

Файл `src/main/resources/application.properties` вже налаштований. Переконайся що `heartsync.properties` містить коректні дані підключення:

```properties
spring.datasource.url=jdbc:mysql://mysql-1daccc1b-nellyturkanuch-3551.c.aivencloud.com:12330/defaultdb?ssl-mode=REQUIRED
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

> ⚠️ **Увага:** Не додавай файл з паролями до Git репозиторію!

### Збірка та запуск

```bash
# Збірка проєкту
mvn clean install -DskipTests

# Запуск
mvn javafx:run
```

Або через **IntelliJ IDEA**: відкрий `HeartSyncApplication.java` → Run ▶️

---

## 📁 Структура проєкту

```
HeartSync/
├── HeartSync-main/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/tyrkanych/
│   │   │   │   ├── HeartSyncApplication.java   # Точка входу
│   │   │   │   ├── controller/                 # JavaFX контролери
│   │   │   │   ├── service/                    # Бізнес-логіка
│   │   │   │   ├── repository/                 # Робота з БД
│   │   │   │   └── model/                      # Моделі даних
│   │   │   └── resources/
│   │   │       ├── application.properties      # Конфігурація Spring
│   │   │       ├── fxml/                       # UI розмітка
│   │   │       └── css/                        # Стилі
│   │   └── test/                               # JUnit 5 тести
│   └── pom.xml
├── data/                                       # Локальні дані
└── pom.xml
```

---

## 🧪 Тестування

```bash
# Запуск всіх тестів
mvn test

# Тести використовують H2 in-memory БД (не потребують підключення до Aiven)
```

---

## 🔒 Безпека

- Паролі зберігаються виключно у вигляді **BCrypt-хешів**
- Чутливі дані профілів шифруються за допомогою **AES-256**
- Підключення до БД використовує **SSL/TLS**
- Файли з паролями виключені з `.gitignore`

---

## 📤 Експорт даних

Додаток підтримує експорт у два формати:

```
📊 Excel (.xlsx)  — за допомогою Apache POI 5.2.5
📄 PDF            — за допомогою Apache PDFBox 3.0.1
```

---

## 👩‍💻 Автор

**Тирканич** — студентка, розробник проєкту HeartSync

[![GitHub](https://img.shields.io/badge/GitHub-Nellly--xjp-181717?style=flat-square&logo=github)](https://github.com/Nellly-xjp)

---

<div align="center">

Made with 💖 using Java & JavaFX

*Курсова робота · 2025–2026*

</div>
