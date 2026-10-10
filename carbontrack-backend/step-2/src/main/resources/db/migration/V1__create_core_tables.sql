-- V1: organisations, users, emission_factors
-- organisations.admin_user_id and users.org_id reference each other (circular),
-- so organisations is created first and its FK to users is added at the end.

CREATE TABLE organisations (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    name          VARCHAR(150) NOT NULL,
    admin_user_id BIGINT       NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_organisations_name (name)
) ENGINE = InnoDB;

CREATE TABLE users (
    id                    BIGINT       NOT NULL AUTO_INCREMENT,
    username              VARCHAR(50)  NOT NULL,
    email                 VARCHAR(150) NOT NULL,
    password_hash         VARCHAR(255) NULL,            -- NULL for Google (OAuth2) users
    role                  VARCHAR(20)  NOT NULL DEFAULT 'USER',      -- USER / ADMIN / ORG_ADMIN
    auth_provider         VARCHAR(20)  NOT NULL DEFAULT 'LOCAL',     -- LOCAL / GOOGLE
    preferred_unit_system VARCHAR(20)  NOT NULL DEFAULT 'METRIC',    -- METRIC / IMPERIAL
    goals_visible         BOOLEAN      NOT NULL DEFAULT TRUE,
    org_id                BIGINT       NULL,
    created_at            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_username (username),
    UNIQUE KEY uk_users_email (email),
    CONSTRAINT fk_users_org FOREIGN KEY (org_id) REFERENCES organisations (id)
) ENGINE = InnoDB;

ALTER TABLE organisations
    ADD CONSTRAINT fk_org_admin_user FOREIGN KEY (admin_user_id) REFERENCES users (id);

CREATE TABLE emission_factors (
    id                BIGINT         NOT NULL AUTO_INCREMENT,
    category          VARCHAR(20)    NOT NULL,   -- TRANSPORT / ELECTRICITY / FOOD / SHOPPING
    activity_type     VARCHAR(50)    NOT NULL,   -- e.g. CAR_PETROL
    unit              VARCHAR(20)    NOT NULL,   -- e.g. KM
    kg_co2e_per_unit  DECIMAL(12, 6) NOT NULL,
    source            VARCHAR(150)   NOT NULL,   -- e.g. 'EPA 2023'
    effective_date    DATE           NOT NULL,
    active            BOOLEAN        NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_factor_type_unit_date (activity_type, unit, effective_date),
    KEY idx_factor_lookup (activity_type, unit, active)
) ENGINE = InnoDB;
