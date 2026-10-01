--liquibase formatted sql

--changeset sandbox:drop-counters-state-table
DROP TABLE counters;

--changeset sandbox:create-counter-events-table
CREATE TABLE counter_events (
    counter_id UUID NOT NULL,
    sequence INT NOT NULL,
    type VARCHAR(16) NOT NULL,
    value INT NOT NULL,
    pod VARCHAR(64) NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (counter_id, sequence)
);

--changeset sandbox:create-counter-timeline-table
CREATE TABLE counter_timeline (
    event_id UUID PRIMARY KEY,
    counter_id UUID NOT NULL,
    service VARCHAR(16) NOT NULL,
    type VARCHAR(16) NOT NULL,
    previous_value INT NOT NULL,
    value INT NOT NULL,
    pod VARCHAR(64) NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_counter_timeline_counter_id_occurred_at ON counter_timeline (counter_id, occurred_at);
