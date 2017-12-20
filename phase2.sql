DROP TABLE  Airline     CASCADE;
DROP TABLE  Flight      CASCADE;
DROP TABLE  Passenger   CASCADE;
DROP TABLE  Rating      CASCADE;
DROP TABLE  Books       CASCADE;

CREATE TABLE Airline (
    airId       CHAR(11)    NOT NULL,
    name        CHAR(11)    NOT NULL,
    founded     INTEGER    NOT NULL,
    country     CHAR(11)    NOT NULL,
    hub         CHAR(11)    NOT NULL,
	PRIMARY KEY(airId)
    check(founded > 1900)
);
--includes the offer relationship
 CREATE TABLE Flight (
    flightNum   CHAR(11)    NOT NULL,
    origin      CHAR(30)    NOT NULL,
    destination CHAR(30)    NOT NULL,
    plane       CHAR(30)    NOT NULL,
    seats       CHAR(30)    NOT NULL,
    duration    INTEGER    NOT NULL,
	PRIMARY KEY(flightNum),

    -- offers
	airId       CHAR(11)    NOT NULL,
    FOREIGN KEY(airId) REFERENCES Airline(airId) ON DELETE NO ACTION
    check(duration > 0),
    check(duration < 24),
);

 CREATE TABLE Passenger (
    pID         CHAR(11)    NOT NULL,
    passNum     CHAR(11)    NOT NULL,
    birthdate   CHAR(11)    NOT NULL,
    country     CHAR(30)    NOT NULL,
    name        CHAR(30)    NOT NULL,

	PRIMARY KEY(pID)
);

--inlcude rqtes relationship
--Include rated relationship
 CREATE TABLE Rating (
	rID         CHAR(11)    NOT NULL,
    comment     CHAR(30)    NOT NULL,
    score       INTEGER     NOT NULL,
	PRIMARY KEY(rID),
    check(score < 5),
    check(score > 0),

    -- rates
    pID         CHAR(11)    NOT NULL,
    FOREIGN KEY(pId) REFERENCES Passenger(pId) ON DELETE NO ACTION,

    -- rated
    flightNum   CHAR(11)    NOT NULL,
    FOREIGN KEY(flightNum) REFERENCES Flight(flightNum) ON DELETE NO ACTION
); 

CREATE TABLE Books (
    flightNum   CHAR(11)    NOT NULL,
    pID         CHAR(11)    NOT NULL,
    departure   CHAR(11)    NOT NULL,
    PRIMARY KEY(flightNum, pID),

    FOREIGN KEY(flightNum) REFERENCES Flight(flightNum),
    FOREIGN KEY(pId) REFERENCES Passenger(pId)
)

\d