--liquibase formatted sql
--changeset is6769:0001-create-initial-schema

create table if not exists tariffs(
    id                      BIGSERIAL           PRIMARY KEY,
    name                    VARCHAR(200)        NOT NULL,
    description             TEXT                NOT NULL,
    cycle_size              VARCHAR(100)        NOT NULL,
    is_active               BOOLEAN             NOT NULL,
    created_at              TIMESTAMP           DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP
);

create table if not exists service_packages(
    id                      BIGSERIAL           PRIMARY KEY,
    name                    VARCHAR(200)        NOT NULL,
    description             TEXT                NOT NULL,
    service_type            VARCHAR(200)        NOT NULL,
    created_at              TIMESTAMP           DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP
);

create table if not exists package_rules(
    id                      BIGSERIAL           PRIMARY KEY,
    service_package_id      BIGINT              NOT NULL REFERENCES service_packages(id),
    rule_type               VARCHAR(100)        NOT NULL,
    value                   NUMERIC             NOT NULL,
    unit                    VARCHAR(100)        NOT NULL,
--    period_type             VARCHAR(100)        NOT NULL,
    condition               JSONB               NOT NULL,
    created_at              TIMESTAMP           DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP
);

create table if not exists subscriber_tariff(
    id                      BIGSERIAL           PRIMARY KEY,
    subscriber_id           BIGINT              NOT NULL,
    tariff_id               BIGINT              NOT NULL REFERENCES tariffs(id),
    cycle_start             TIMESTAMP           NOT NULL,
    cycle_end               TIMESTAMP           NOT NULL,
    created_at              TIMESTAMP           DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP
);

create table if not exists subscriber_package_usage(
    id                      BIGSERIAL           PRIMARY KEY,
    subscriber_id           BIGINT              NOT NULL,
    service_package_id      BIGINT              NOT NULL REFERENCES service_packages(id),
    used_amount             INTEGER             NOT NULL,
    limit_amount            INTEGER             NOT NULL,
    unit                    VARCHAR(100)        NOT NULL,
    created_at              TIMESTAMP           DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP
);

create table if not exists tariff_packages(
    id                      BIGSERIAL           PRIMARY KEY,
    tariff_id               BIGINT              NOT NULL REFERENCES tariffs(id),
    service_package_id      BIGINT              NOT NULL REFERENCES service_packages(id),
    priority                INTEGER             NOT NULL,
    created_at              TIMESTAMP           DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP
)

