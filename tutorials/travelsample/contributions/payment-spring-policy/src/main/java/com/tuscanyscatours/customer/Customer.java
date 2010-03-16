/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package com.tuscanyscatours.customer;

import com.tuscanyscatours.payment.creditcard.CreditCardDetailsType;

/**
 * Customer data
 */
public class Customer {
    private String id;
    private String email;
    private String name;

    private CreditCardDetailsType creditCard;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CreditCardDetailsType getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCardDetailsType creditCard) {
        this.creditCard = creditCard;
    }

    public String toString() {
        return "id: " + id + " name: " + name + " e-mail: " + email;
    }

}
