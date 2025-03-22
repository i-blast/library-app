-- admin/admin
INSERT INTO users (id, username, password)
VALUES (1, 'admin', '$2a$10$ZtbKpi3xF.jZAMMgSkoTdON3oTKY0VxqnEbq9hF1etb7BVum.8z4y');

INSERT INTO user_roles (user_id, roles)
VALUES (1, 'ADMIN');
INSERT INTO user_roles (user_id, roles)
VALUES (1, 'USER');
