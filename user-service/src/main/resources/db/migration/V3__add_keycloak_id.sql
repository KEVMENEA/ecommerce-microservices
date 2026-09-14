ALTER TABLE users
    ADD COLUMN keycloak_id VARCHAR(100);

CREATE UNIQUE INDEX uk_users_keycloak_id
    ON users(keycloak_id);