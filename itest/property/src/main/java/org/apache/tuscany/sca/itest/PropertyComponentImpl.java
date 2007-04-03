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

package org.apache.tuscany.sca.itest;

import java.util.Collection;

import org.osoa.sca.annotations.Property;

public class PropertyComponentImpl implements PropertyComponent {
    @Property
    protected ComplexPropertyBean complexPropertyOne;
    
    @Property
    protected ComplexPropertyBean complexPropertyTwo;
    
    @Property
    protected ComplexPropertyBean complexPropertyThree;
    
    @Property
    protected Collection<ComplexPropertyBean> complexPropertyFour;
    
    @Property(name = "location")
    protected String location = "RTP";

    @Property(name = "year")
    protected String year = "2006";
    
    public String getLocation(){
        return location;
    }
    
    public String getYear(){
        return year;
    }

    public ComplexPropertyBean getComplexPropertyOne() {
        //System.out.println(complexPropertyOne);
        return complexPropertyOne;
    }

    public ComplexPropertyBean getComplexPropertyTwo() {
        //System.out.println(complexPropertyTwo);
        return complexPropertyTwo;
    }
    
    public ComplexPropertyBean getComplexPropertyThree() {
        //System.out.println(complexPropertyThree);
        return complexPropertyThree;
    }
    
    public Collection<ComplexPropertyBean> getComplexPropertyFour() {
        //System.out.println(complexPropertyThree);
        return complexPropertyFour;
    }
}
