BEGIN TRANSACTION;

DELETE FROM deck_users;
DELETE FROM practice_settings;
DELETE FROM card;
DELETE FROM deck;
DELETE FROM login;
DELETE FROM users;

INSERT INTO users (user_id, username, first_name, last_name, email, joined_date, is_user_active)
VALUES (1001, 'username1', 'TestFirst1', 'TestLast1', 'testuser1@example.com', '9999/10/29 21:57:57', false);

INSERT INTO users (user_id, username, first_name, last_name, email, joined_date)
VALUES (1002, 'username2', 'TestFirst2', 'TestLast2', 'testuser2@example.com', '8888/01/01 01:01:01');

INSERT INTO login (user_id, hashed_password)
VALUES (1001, '111111111111111111111111111111111111111111111111111111111111');

INSERT INTO login (user_id, hashed_password)
VALUES (1002, '222222222222222222222222222222222222222222222222222222222222');

COMMIT TRANSACTION;