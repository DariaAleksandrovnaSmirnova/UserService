CREATE TABLE users (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    name varchar(128) NOT NULL,
    surname varchar(128) NOT NULL,
    birth_date DATE NOT NULL,
    email varchar(64) UNIQUE NOT NULL
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_name_surname ON users(name, surname);

CREATE TABLE card_info (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number VARCHAR(16) UNIQUE NOT NULL CHECK (LENGTH(number) = 16),
    holder varchar(32),
    expiration_date DATE NOT NULL,
    user_id bigint NOT NULL,

    CONSTRAINT fk_card_info_user FOREIGN KEY (user_id) REFERENCES users(id)
);