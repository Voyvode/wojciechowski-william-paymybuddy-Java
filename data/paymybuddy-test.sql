CREATE SCHEMA test;

CREATE TABLE test.customer (
	id SERIAL PRIMARY KEY,
	username VARCHAR(32) UNIQUE NOT NULL,
	email VARCHAR(254) UNIQUE NOT NULL,
	password_hash CHAR(40)
);

CREATE TABLE test.buddy (
	PRIMARY KEY (customer_id, buddy_id),
	customer_id INT REFERENCES customer(id),
	buddy_id INT REFERENCES customer(id),
	CONSTRAINT no_self_buddy CHECK (customer_id != buddy_id),
	CONSTRAINT unique_buddy UNIQUE (customer_id, buddy_id)
);

CREATE TABLE test.transfer (
	id SERIAL PRIMARY KEY,
	sender INT REFERENCES customer(id),
	receiver INT REFERENCES customer(id),
	description TEXT,
	amount NUMERIC(7,5)
);
