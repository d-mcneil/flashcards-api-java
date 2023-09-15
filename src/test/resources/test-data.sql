BEGIN TRANSACTION;

DELETE FROM deck_users;
DELETE FROM practice_settings;
DELETE FROM card;
DELETE FROM deck;
DELETE FROM login;
DELETE FROM users;

INSERT INTO users (user_id, username, first_name, last_name, email, joined_date, is_user_active)
VALUES (1001, 'USerNAme1', 'TestFirst1', 'TestLast1', 'testuser1@example.com', '9999/10/29 21:57:57', false);
-- This intentionally has different casing of the username compared to the representation data in the java objects... testing that citext works as expected

INSERT INTO users (user_id, username, first_name, last_name, email, joined_date)
VALUES (1002, 'USerNAme2', 'TestFirst2', 'TestLast2', 'testuser2@example.com', '8888/01/01 01:01:01');
-- This intentionally has different casing of the username compared to the representation data in the java objects... testing that citext works as expected

INSERT INTO login (user_id, hashed_password)
VALUES (1001, '111111111111111111111111111111111111111111111111111111111111');

INSERT INTO login (user_id, hashed_password)
VALUES (1002, '222222222222222222222222222222222222222222222222222222222222');

COMMIT TRANSACTION;