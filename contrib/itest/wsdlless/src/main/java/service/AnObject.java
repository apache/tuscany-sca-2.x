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
package service;

public class AnObject {

    private String someRetValue;
    private Integer someOtherRetValue;

    public AnObject() {
    }

    public AnObject(String someRetValue, Integer someOtherRetValue) {
        this.someRetValue = someRetValue;
        this.someOtherRetValue = someOtherRetValue;
    }

    /**
     * @return the someOtherRetValue
     */
    public Integer getSomeOtherRetValue() {
        return someOtherRetValue;
    }

    /**
     * @param someOtherRetValue the someOtherRetValue to set
     */
    public void setSomeOtherRetValue(Integer someOtherRetValue) {
        this.someOtherRetValue = someOtherRetValue;
    }

    /**
     * @return the someRetValue
     */
    public String getSomeRetValue() {
        return someRetValue;
    }

    /**
     * @param someRetValue the someRetValue to set
     */
    public void setSomeRetValue(String someRetValue) {
        this.someRetValue = someRetValue;
    }

}
