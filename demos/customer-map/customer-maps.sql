CREATE TABLE CUSTOMER (ID INT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY ,
                       NAME VARCHAR(30),
                       ADDRESS VARCHAR(30),
                       CITY VARCHAR(20),
                       STATE VARCHAR(2),
                       LATITUDE VARCHAR(20),
                       LONGITUDE VARCHAR(30) )