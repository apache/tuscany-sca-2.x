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
package bean;

import java.util.ArrayList;
import java.util.List;

public class TestBean {
    private String testString;
    // private String[] testStringArray; // Jackson cannot deserilize String[]
    private List<String> testStringArray = new ArrayList<String>();
    private int testInt;

    public String getTestString() {
        return testString;
    }

    public void setTestString(String testString) {
        this.testString = testString;
    }

    public List<String> getTestStringArray() {
        return testStringArray;
    }
    
    public void setStringArray(List<String> stringArray) {
        this.testStringArray = stringArray;
    }

    public int getTestInt() {
        return testInt;
    }

    public void setTestInt(int testInt) {
        this.testInt = testInt;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + testInt;
        result = prime * result + ((testString == null) ? 0 : testString.hashCode());
        result = prime * result + ((testStringArray == null) ? 0 : testStringArray.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TestBean other = (TestBean)obj;
        if (testInt != other.testInt) {
            return false;
        }
        if (testString == null) {
            if (other.testString != null) {
                return false;
            }
        } else if (!testString.equals(other.testString)) {
            return false;
        }
        if (testStringArray == null) {
            if (other.testStringArray != null) {
                return false;
            }
        } else if (!testStringArray.equals(other.testStringArray)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TestBean [testString=").append(testString).append(", testStringArray=").append(testStringArray)
            .append(", testInt=").append(testInt).append("]");
        return builder.toString();
    }

}
