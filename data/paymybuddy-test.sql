CREATE SCHEMA test;

CREATE TABLE test.customer (
	id SERIAL PRIMARY KEY,
	username VARCHAR(32) UNIQUE NOT NULL,
	email VARCHAR(254) UNIQUE NOT NULL,
	password_hash CHAR(60), -- bcrypt
	signup TIMESTAMP NOT NULL
);
CREATE INDEX test.index_username ON test.customer(username); -- user search optimization

CREATE TABLE test.buddy (
	PRIMARY KEY (customer_id, buddy_id),
	customer_id INT REFERENCES test.customer(id) NOT NULL,
	buddy_id INT REFERENCES test.customer(id) NOT NULL,
	CONSTRAINT no_self_buddy CHECK (customer_id != buddy_id),
	CONSTRAINT unique_buddy UNIQUE (customer_id, buddy_id)
);

CREATE TABLE test.transfer (
	id SERIAL PRIMARY KEY,
	sender INT REFERENCES test.customer(id) NOT NULL,
	receiver INT REFERENCES test.customer(id) NOT NULL,
	description TEXT,
	amount NUMERIC(4,2) CHECK (amount > 0 AND amount <= 1000),
	timestamp TIMESTAMP NOT NULL
);
