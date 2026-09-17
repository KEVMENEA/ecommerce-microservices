CREATE TABLE outbox_events (
                               id BIGSERIAL PRIMARY KEY,

                               event_id UUID NOT NULL UNIQUE,

                               aggregate_type VARCHAR(100) NOT NULL,

                               aggregate_id VARCHAR(100) NOT NULL,

                               event_type VARCHAR(100) NOT NULL,

                               topic VARCHAR(255) NOT NULL,

                               payload JSONB NOT NULL,

                               status VARCHAR(30) NOT NULL,

                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               published_at TIMESTAMP,

                               retry_count INTEGER NOT NULL DEFAULT 0,
                               last_error TEXT
);

CREATE INDEX idx_outbox_events_status_created_at
    ON outbox_events(status, created_at);