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

DROP TABLE CUSTOMER;

CREATE TABLE CUSTOMER (
   ID INT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY, 
   NAME VARCHAR(30),
   PHONE VARCHAR(10),
   ADDRESS VARCHAR(25),
   CITY VARCHAR(15),
   STATE VARCHAR(2),
   ZIP VARCHAR(5));

INSERT INTO CUSTOMER (NAME, PHONE, ADDRESS, CITY, STATE, ZIP ) VALUES('Joe Smith', '4081234567', '1440 N 1st ST', 'SAN JOSE', 'CA', '95134');
INSERT INTO CUSTOMER (NAME, PHONE, ADDRESS, CITY, STATE, ZIP ) VALUES('Erik Johnson', '4081230987', '1440 N 1st ST', 'SAN JOSE', 'CA', '95134');
INSERT INTO CUSTOMER (NAME, PHONE, ADDRESS, CITY, STATE, ZIP ) VALUES('Mark Pensacola', '4085679988', '1440 N 1st ST', 'SAN JOSE', 'CA', '95134');