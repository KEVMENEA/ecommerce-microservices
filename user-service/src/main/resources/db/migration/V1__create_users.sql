CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       email VARCHAR(320) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,
                       status VARCHAR(30) NOT NULL,
                       created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                       updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE user_roles (
                            user_id UUID NOT NULL,
                            role VARCHAR(30) NOT NULL,

                            PRIMARY KEY(user_id, role),

                            CONSTRAINT fk_user_roles_user
                                FOREIGN KEY(user_id)
                                    REFERENCES users(id)
);