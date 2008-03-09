--
--  Licensed to the Apache Software Foundation (ASF) under one
--  or more contributor license agreements.  See the NOTICE file
--  distributed with this work for additional information
--  regarding copyright ownership.  The ASF licenses this file
--  to you under the Apache License, Version 2.0 (the
--  "License"); you may not use this file except in compliance
--  with the License.  You may obtain a copy of the License at
--
--  http://www.apache.org/licenses/LICENSE-2.0
--
--  Unless required by applicable law or agreed to in writing,
--  software distributed under the License is distributed on an
--  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
--  KIND, either express or implied.  See the License for the
--  specific language governing permissions and limitations
--  under the License.
--

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