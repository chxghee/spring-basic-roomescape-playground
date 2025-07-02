INSERT INTO member (name, email, password, role)
VALUES ('어드민', 'admin@email.com', 'password', 'ADMIN'),
       ('브라운', 'brown@email.com', 'password', 'USER');

INSERT INTO theme (name, description, deleted)
VALUES ('테마1', '테마1입니다.', false),
       ('테마2', '테마2입니다.', false);

INSERT INTO time (time_value, deleted)
VALUES ('10:00', false),
       ('12:00', false);
