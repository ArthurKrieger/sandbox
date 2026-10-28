--liquibase formatted sql

--changeset sandbox:create-counters-table-again
-- The producer keeps only what it decides: that a counter exists, and where and when it was asked for. The
-- consumer counts it and keeps its history.
CREATE TABLE counters (
    id UUID PRIMARY KEY,
    created_by VARCHAR(64) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);
INSERT INTO counters (id, created_by, created_at)
SELECT counter_id, pod, occurred_at
FROM counter_timeline
WHERE service = 'producer' AND type = 'CREATED';

--changeset sandbox:drop-counter-events-and-timeline
DROP TABLE counter_events;
DROP TABLE counter_timeline;
