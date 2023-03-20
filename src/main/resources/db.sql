create table cab (
	id               BIGINT UNSIGNED   NOT NULL     PRIMARY KEY AUTO_INCREMENT,
	cab_number       VARCHAR(20)       NOT NULL     UNIQUE,
	in_service       BOOLEAN           NOT NULL     DEFAULT TRUE
);

create table driver (
	id       BIGINT UNSIGNED   NOT NULL     PRIMARY KEY AUTO_INCREMENT,
	name     VARCHAR(50)       NOT NULL     UNIQUE,
	license  VARCHAR(20)       NOT NULL     UNIQUE,
	cab_id   BIGINT UNSIGNED,
	CONSTRAINT driver_fk1 FOREIGN KEY (cab_id) REFERENCES cab (id),
	CONSTRAINT driver_uk1 UNIQUE (cab_id)
);

create table booking (
	id                  BIGINT UNSIGNED   NOT NULL     PRIMARY KEY AUTO_INCREMENT,
	customer_name       VARCHAR(50)       NOT NULL,
	customer_phone      VARCHAR(20)       NOT NULL,
	customer_email      VARCHAR(100),
    pickup_location     VARCHAR(255)      NOT NULL,
    call_time           TIMESTAMP         NOT NULL,
	pickup_time         TIMESTAMP,
	dropoff_time        TIMESTAMP,
	passengers          INT UNSIGNED,
	miles               DECIMAL(6,2),
	wait_minutes        DECIMAL(6,2),
	fare                DECIMAL(6,2),
	driver_id           BIGINT UNSIGNED,
	CONSTRAINT booking_fk1 FOREIGN KEY (driver_id) REFERENCES driver (id)
);

create table users (
	id                  BIGINT UNSIGNED   NOT NULL     PRIMARY KEY AUTO_INCREMENT,
	username            VARCHAR(20)       NOT NULL     UNIQUE,
	email               VARCHAR(255)      NOT NULL     UNIQUE,
	full_name           VARCHAR(255)      NOT NULL,
	password            VARCHAR(255)      NOT NULL,
    accountExpired      BOOLEAN           NOT NULL     DEFAULT FALSE,
    accountLocked       BOOLEAN           NOT NULL     DEFAULT FALSE,
    passwordExpired     BOOLEAN           NOT NULL     DEFAULT FALSE,
    enabled             BOOLEAN           NOT NULL     DEFAULT TRUE,
    role                VARCHAR(20)       NOT NULL,
    CONSTRAINT users_ck1 CHECK (role IN ('REGISTERED','USER','ADMIN'))
);
