BEGIN TRANSACTION;

INSERT INTO users (username, first_name, last_name, email, joined_date, is_user_active)
VALUES ('username1', 'TestFirst1', 'TestLast1', 'testuser1@example.com', '9999/10/29 21:57:57', false);

INSERT INTO users (username, first_name, last_name, email, joined_date)
VALUES ('username2', 'TestFirst2', 'TestLast2', 'testuser2@example.com', '8888/01/01 01:01:01');

INSERT INTO login (user_id, hashed_password)
VALUES (1001, '111111111111111111111111111111111111111111111111111111111111');

INSERT INTO login (user_id, hashed_password)
VALUES (1002, '222222222222222222222222222222222222222222222222222222222222');

COMMIT TRANSACTION;