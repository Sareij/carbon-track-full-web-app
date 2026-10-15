-- V2: activity_logs, goals, badges, user_badges

CREATE TABLE activity_logs (
    id                  BIGINT         NOT NULL AUTO_INCREMENT,
    user_id             BIGINT         NOT NULL,
    category            VARCHAR(20)    NOT NULL,
    activity_type       VARCHAR(50)    NOT NULL,
    quantity            DECIMAL(12, 3) NOT NULL,
    unit                VARCHAR(20)    NOT NULL,
    co2e_kg             DECIMAL(14, 4) NOT NULL,   -- computed ONCE at insert time by the engine
    emission_factor_id  BIGINT         NOT NULL,   -- which factor was used (audit trail)
    log_date            DATE           NOT NULL,
    created_at          TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_log_user   FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_log_factor FOREIGN KEY (emission_factor_id) REFERENCES emission_factors (id),
    KEY idx_log_user_date     (user_id, log_date),
    KEY idx_log_user_category (user_id, category, log_date)
) ENGINE = InnoDB;

CREATE TABLE goals (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    user_id              BIGINT       NOT NULL,
    target_reduction_pct DECIMAL(5, 2) NOT NULL,
    period_days          INT          NOT NULL,
    start_date           DATE         NOT NULL,
    status               VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',   -- ACTIVE / ACHIEVED / MISSED
    PRIMARY KEY (id),
    CONSTRAINT fk_goal_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    KEY idx_goal_user_status (user_id, status)
) ENGINE = InnoDB;

CREATE TABLE badges (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    name          VARCHAR(80)  NOT NULL,
    description   VARCHAR(255) NOT NULL,
    trigger_type  VARCHAR(20)  NOT NULL,   -- STREAK / GOAL / REDUCTION
    threshold     INT          NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_badges_name (name)
) ENGINE = InnoDB;

CREATE TABLE user_badges (
    id          BIGINT    NOT NULL AUTO_INCREMENT,
    user_id     BIGINT    NOT NULL,
    badge_id    BIGINT    NOT NULL,
    awarded_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_ub_user  FOREIGN KEY (user_id)  REFERENCES users (id)  ON DELETE CASCADE,
    CONSTRAINT fk_ub_badge FOREIGN KEY (badge_id) REFERENCES badges (id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_badge (user_id, badge_id)   -- a badge can only be earned once
) ENGINE = InnoDB;
