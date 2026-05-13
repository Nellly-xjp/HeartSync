-- USERS
INSERT INTO users (email, password, name, gender, birth_date, city, bio) VALUES
('ivan@example.com', 'pass123', 'Іван', 'male', '1995-05-15', 'Київ', 'Люблю подорожі'),
('maria@example.com', 'pass456', 'Марія', 'female', '1997-08-20', 'Київ', 'Обожнюю музику'),
('olena@example.com', 'pass789', 'Олена', 'female', '1996-03-10', 'Львів', 'Шукаю нових друзів');

-- INTERESTS
INSERT INTO interests (name) VALUES
('Музика'),
('Спорт'),
('Кіно'),
('Подорожі'),
('Коддинг');

-- USER INTERESTS
INSERT INTO user_interests (user_id, interest_id, level) VALUES
(1, 4, 5),
(1, 5, 4),
(2, 1, 5),
(2, 4, 3),
(3, 3, 4);

-- SETTINGS
INSERT INTO user_settings (user_id, theme, language) VALUES
(1, 'dark', 'uk'),
(2, 'light', 'uk'),
(3, 'light', 'en');

-- PREFERENCES
INSERT INTO preferences (user_id, preferred_gender, min_age, max_age) VALUES
(1, 'female', 20, 30),
(2, 'male', 22, 35);

-- LIKES
INSERT INTO likes (from_user_id, to_user_id) VALUES
(1, 2),
(2, 1);

-- MATCHES
INSERT INTO matches (user1_id, user2_id, compatibility_score) VALUES
(1, 2, 85.50);

-- MESSAGES
INSERT INTO messages (sender_id, receiver_id, message_text) VALUES
(1, 2, 'Привіт! Як справи?');

-- ADMIN
INSERT INTO admins (name, email, password, role) VALUES
('Admin Root', 'admin@app.com', 'adminpass', 'superadmin');

-- SYSTEM SETTINGS
INSERT INTO system_settings (setting_key, setting_value) VALUES
('maintenance_mode', 'false'),
('max_likes_per_day', '50');