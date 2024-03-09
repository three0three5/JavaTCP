CREATE TABLE vector
(
    login      VARCHAR,
    name       VARCHAR,
    x          DECIMAL NOT NULL,
    y          DECIMAL NOT NULL,
    z          DECIMAL NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (login, name)
);