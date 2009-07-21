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

package org.apache.tuscany.sca.itest.policies;

import java.io.Serializable;

/**
 * Credit Card 
 */
public class CreditCard implements Serializable {
    private static final long serialVersionUID = -6107293191546007197L;
    private String type;
    private String number;
    private String owner;
    private int expMonth;
    private int expYear;
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public int getExpMonth() {
        return expMonth;
    }
    public void setExpMonth(int expMonth) {
        this.expMonth = expMonth;
    }
    public int getExpYear() {
        return expYear;
    }
    public void setExpYear(int expYear) {
        this.expYear = expYear;
    }
}
