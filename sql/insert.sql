insert into users (username, email, password) values ('admin', 'admin@aulab.it', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG');

insert into roles (name) values ('ROLE_ADMIN');
insert into roles (name) values ('ROLE_REVISOR');
insert into roles (name) values ('ROLE_WRITER');
insert into roles (name) values ('ROLE_USER');

insert into users_roles (user_id, role_id) values (1, 1);

insert into categories (name) values ('politica'), ('economia'), ('food&drink'), ('sport'), ('intrattenimento'), ('tech');