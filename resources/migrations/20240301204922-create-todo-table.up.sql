CREATE TABLE todos (
    id SERIAL PRIMARY KEY,
    name VARCHAR(32) NOT NULL,
    description TEXT,
    completed BOOLEAN FALSE,
    user_id INT,
    CONSTRAINT fk_user
      FOREIGN KEY(user_id) 
        REFERENCES users(id)
        ON DELETE CASCADE
);
