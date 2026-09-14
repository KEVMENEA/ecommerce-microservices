
CREATE SEQUENCE IF NOT EXISTS revinfo_seq
    START WITH 1
    INCREMENT BY 50;

CREATE TABLE revinfo
(
    rev      BIGINT NOT NULL,
    revtstmp BIGINT,
    CONSTRAINT pk_revinfo PRIMARY KEY (rev)
);

CREATE TABLE revchanges
(
    rev        BIGINT NOT NULL,
    entityname VARCHAR(255),

    CONSTRAINT fk_revchanges_revinfo
        FOREIGN KEY (rev)
            REFERENCES revinfo (rev)
);