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
package org.apache.tuscany.sca.itest.databindings.jaxb;

/**
 * @version $Rev$ $Date$
 */
public class Bean31 extends Bean2 {
    private String address;
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getAddress() {
        return address;
    }
    
    public boolean equals(Object that) {
        if(that == null) {
            return false;
        }
        
        if(this.getClass() != that.getClass()) {
            return false;
        }
        
        if(this == that) {
            return true;
        } else if(this.address != null) {
            return this.address.equals(((Bean31)that).address) && super.equals(that);
        } else {
            return ((Bean31)that).address == null && super.equals(that);
        }
    }

    public String toString() {
        return this.getClass().getSimpleName()+"[name = "+super.getName()+", address = "+address+"]";
    }
}
