--liquibase formatted sql

--changeset sandbox:create-counters-table
CREATE TABLE counters (
    id UUID PRIMARY KEY,
    value INT NOT NULL CHECK (value >= 0),
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    finished_at TIMESTAMPTZ,
    version BIGINT NOT NULL
);
