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

import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * @version $Rev$ $Date$
 */
@XmlSeeAlso({Bean3.class, Bean3[].class, Bean31.class, Bean31[].class})
public class Bean2 {
    private String name;
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
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
        } else if(this.name != null) {
            return this.name.equals(((Bean2)that).name);
        } else {
            return ((Bean2)that).name == null;
        }
    }
    
    public String toString() {
        return this.getClass().getSimpleName()+"[name = "+name+"]";
    }
}
