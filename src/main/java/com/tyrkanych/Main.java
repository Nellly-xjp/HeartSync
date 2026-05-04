package com.tyrkanych;

import com.tyrkanych.config.ConnectionPool;
import com.tyrkanych.dao.impl.InterestDaoImpl;
import com.tyrkanych.dao.impl.LikeDaoImpl;
import com.tyrkanych.dao.impl.MessageDaoImpl;
import com.tyrkanych.dao.impl.SystemSettingDaoImpl;
import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.dao.impl.UserInterestDaoImpl;
import com.tyrkanych.entity.Interest;
import com.tyrkanych.entity.Like;
import com.tyrkanych.entity.Message;
import com.tyrkanych.entity.SystemSetting;
import com.tyrkanych.entity.User;
import com.tyrkanych.entity.UserInterest;
import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== HeartSync Application Started ===\n");

        try {
            // 1. Ініціалізація Connection Pool
            ConnectionPool.initialize();

            // 2. Створення таблиць
            DatabaseInitializer.createTables();
            System.out.println("✅ Таблиці створено успішно!\n");

            // ====================== ТЕСТУВАННЯ DAO ======================

            // --- UserDao ---
            UserDaoImpl userDao = new UserDaoImpl();
            System.out.println("=== Тест UserDao ===");

            User user = new User("test@example.com", "password123", "Тестовий Користувач",
                    "male", LocalDate.of(1995, 5, 15));
            user.setCity("Київ");
            user.setBio("Люблю програмувати і подорожувати");

            User savedUser = userDao.save(user);
            System.out.println(
                    "Збережено користувача: " + savedUser.getName() + " (ID: " + savedUser.getId()
                            + ")");

            userDao.findById(savedUser.getId()).ifPresent(u ->
                    System.out.println("Знайдено: " + u.getName() + " - " + u.getEmail()));

            // ✅ ВИПРАВЛЕННЯ: створюємо другого користувача, щоб лайки та повідомлення мали реальний to_user_id
            User user2 = new User("second@example.com", "pass456", "Другий Користувач",
                    "female", LocalDate.of(1998, 3, 20));
            user2.setCity("Львів");
            user2.setBio("Люблю музику і мандрівки");

            User savedUser2 = userDao.save(user2);
            System.out.println(
                    "Збережено другого користувача: " + savedUser2.getName() + " (ID: "
                            + savedUser2.getId()
                            + ")");

            List<User> users = userDao.findAll();
            System.out.println("Всього користувачів: " + users.size() + "\n");

            // --- InterestDao ---
            InterestDaoImpl interestDao = new InterestDaoImpl();
            System.out.println("=== Тест InterestDao ===");

            Interest music = new Interest("Музика");
            Interest sport = new Interest("Спорт");
            interestDao.save(music);
            interestDao.save(sport);

            interestDao.findByName("Музика").ifPresent(i ->
                    System.out.println("Знайдено інтерес: " + i.getName()));

            // --- UserInterestDao ---
            UserInterestDaoImpl userInterestDao = new UserInterestDaoImpl();
            System.out.println("\n=== Тест UserInterest ===");

            UserInterest ui1 = new UserInterest(savedUser.getId(), music.getId(), 5);
            UserInterest ui2 = new UserInterest(savedUser.getId(), sport.getId(), 4);

            userInterestDao.save(ui1);
            userInterestDao.save(ui2);

            List<UserInterest> userInterests = userInterestDao.findByUserId(savedUser.getId());
            System.out.println("Користувач має " + userInterests.size() + " інтересів");

            // --- LikeDao ---
            LikeDaoImpl likeDao = new LikeDaoImpl();
            System.out.println("\n=== Тест LikeDao ===");

            // ✅ ВИПРАВЛЕННЯ: використовуємо savedUser2.getId() замість хардкодного 2L
            Like like = new Like(savedUser.getId(), savedUser2.getId());
            likeDao.save(like);
            System.out.println("Лайк поставлено!");

            // --- MessageDao ---
            MessageDaoImpl messageDao = new MessageDaoImpl();
            System.out.println("\n=== Тест MessageDao ===");

            // ✅ ВИПРАВЛЕННЯ: використовуємо savedUser2.getId() замість хардкодного 2L
            Message msg = new Message(savedUser.getId(), savedUser2.getId(), "Привіт! Як справи?");
            messageDao.save(msg);
            System.out.println("Повідомлення відправлено!");

            List<Message> conversation = messageDao.findConversation(savedUser.getId(),
                    savedUser2.getId());
            System.out.println("Знайдено повідомлень у чаті: " + conversation.size());

            // --- System Settings ---
            SystemSettingDaoImpl settingDao = new SystemSettingDaoImpl();
            SystemSetting setting = new SystemSetting("max_likes_per_day", "50");
            settingDao.save(setting);
            System.out.println("\nСистемні налаштування збережено.");

            System.out.println("\n🎉 Всі тести пройдено успішно!");

        } catch (Exception e) {
            System.err.println("❌ Помилка під час виконання:");
            e.printStackTrace();
        } finally {
            ConnectionPool.shutdown();
        }
    }
}
