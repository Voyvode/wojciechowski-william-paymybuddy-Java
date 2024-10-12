CREATE TABLE customer (
    id SERIAL PRIMARY KEY,
    username VARCHAR(32) UNIQUE NOT NULL,
    email VARCHAR(254) UNIQUE NOT NULL,
    password_hash CHAR(60) NOT NULL, -- bcrypt
	signup TIMESTAMP NOT NULL
);
CREATE INDEX index_username ON customer(username); -- user search optimization
CREATE INDEX index_email ON customer(email); -- user search optimization

CREATE TABLE buddy (
	PRIMARY KEY (customer_id, buddy_id),
	customer_id INT REFERENCES customer(id) NOT NULL,
	buddy_id INT REFERENCES customer(id) NOT NULL,
	CONSTRAINT no_self_buddy CHECK (customer_id != buddy_id),
	CONSTRAINT unique_buddy UNIQUE (customer_id, buddy_id)
);

CREATE TABLE transfer (
    id SERIAL PRIMARY KEY,
    sender INT REFERENCES customer(id) NOT NULL,
    receiver INT REFERENCES customer(id) NOT NULL,
    description TEXT,
    amount NUMERIC(4,2) CHECK (amount > 0 AND amount <= 1000),
	timestamp TIMESTAMP NOT NULL
);
