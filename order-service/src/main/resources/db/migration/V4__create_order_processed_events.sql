CREATE TABLE processed_events (
                                  id BIGSERIAL PRIMARY KEY,
                                  event_id UUID NOT NULL UNIQUE,
                                  processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);