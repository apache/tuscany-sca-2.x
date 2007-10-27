DROP TABLE CATALOG;

CREATE TABLE CATALOG(
   id             NUMERIC(5 , 0) NOT NULL,
   product_name   VARCHAR(30),
   currency_code  CHAR(3),
   price          REAL,
   primary key (id)
);



INSERT INTO CATALOG
 VALUES(0,'Apple', 'USD', 2.99);
INSERT INTO CATALOG
 VALUES(1,'Orange', 'USD', 3.55);
INSERT INTO CATALOG
 VALUES(2,'Pear', 'USD', 1.55);