CREATE TABLE customer
(
	id            SERIAL PRIMARY KEY,
	username      VARCHAR(32) UNIQUE  NOT NULL,
	email         VARCHAR(254) UNIQUE NOT NULL,
	password_hash CHAR(60)            NOT NULL,
	created_at    TIMESTAMP           NOT NULL,
	last_login_at TIMESTAMP           NOT NULL
);

CREATE TABLE customer_buddy
(
	customer_id INTEGER NOT NULL REFERENCES customer (id),
	buddy_id    INTEGER NOT NULL REFERENCES customer (id),
	PRIMARY KEY (customer_id, buddy_id),
	CONSTRAINT check_no_self_buddy CHECK (customer_id != buddy_id)
);

CREATE TABLE transfer
(
	id          SERIAL PRIMARY KEY,
	sender      INTEGER   NOT NULL REFERENCES customer (id),
	receiver    INTEGER   NOT NULL REFERENCES customer (id),
	description VARCHAR(255),
	amount      NUMERIC(6, 2) CHECK (amount > 0 AND amount <= 1000),
	timestamp   TIMESTAMP NOT NULL
);