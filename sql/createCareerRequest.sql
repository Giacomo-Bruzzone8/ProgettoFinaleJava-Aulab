CREATE TABLE career_request (
    id INT AUTO_INCREMENT PRIMARY KEY,
    body TEXT,
   user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    role_id BIGINT,
    FOREIGN KEY (role_id) REFERENCES roles(id),
    is_checked BOOLEAN
    );