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
@XmlSeeAlso({Bean10.class, Bean11.class, Bean10[].class, Bean11[].class})
public class Bean1<T> {
    private T item;
    
    public Bean1() {
    }

    public Bean1(T item) {
        this.item = item;
    }

    public void setItem(T item) {
        this.item = item;
    }
    
    public T getItem() {
        return item;
    }
    
    public boolean equals(Object that) {
        if(that == null) {
            return false;
        }
        if(that.getClass() != this.getClass()) {
            return false;
        }

        Bean1<?> that1 = (Bean1<?>)that;
        if(this == that1) {
            return true;
        } else if(this.item != null) {
            return this.item.equals(that1.item);
        } else {
            return that1.item == null;
        }
    }
    
    public String toString() {
        return this.getClass().getSimpleName()+"[item = "+item+"]";
    }
}
