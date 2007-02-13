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

package org.apache.tuscany.sca.test.property;

import org.osoa.sca.annotations.Property;

public class ABComponentImpl implements ABComponent {

    private String aProperty;
    private String bProperty;
    private int intValue;
 //   private Collection manyProp;
    private String zProperty;
    private String fProperty;

    @Property(name="xpath")
    public void setZProperty(final String value) {
        this.zProperty = value;
    }
    
//    @Property(name="foobar")
//    public void setCollectionProperty(final Collection value) {
//        this.manyProp = value;
//    }
    
    @Property
    public void setA(final String A) {
        this.aProperty = A;
    }

    @Property
    public void setB(final String B) {
        this.bProperty = B;
    }
    
    @Property 
    public void setF(final String F) {
        this.fProperty = F;
    }
    
    @Property
    public void setOne(final int value) {
        this.intValue = value;
    }
    
    public String getA() {
        return this.aProperty;
    }
    
    public String getB() {
        return this.bProperty;
    }
    
    public int getIntValue() {
        return this.intValue;
    }
    
    public String getZ() {
        return this.zProperty;
    }
    
    public String getF() {
        return this.fProperty;
    }
}
