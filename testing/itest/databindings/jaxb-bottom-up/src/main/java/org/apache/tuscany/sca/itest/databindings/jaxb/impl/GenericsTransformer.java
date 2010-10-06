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

package org.apache.tuscany.sca.itest.databindings.jaxb.impl;

import org.apache.tuscany.sca.itest.databindings.jaxb.Bean1;
import org.apache.tuscany.sca.itest.databindings.jaxb.Bean10;
import org.apache.tuscany.sca.itest.databindings.jaxb.Bean11;
import org.apache.tuscany.sca.itest.databindings.jaxb.Bean2;
import org.apache.tuscany.sca.itest.databindings.jaxb.Bean3;
import org.apache.tuscany.sca.itest.databindings.jaxb.Bean31;


/**
 * GenericsTransformer class that provide for transforming input provided to GenericsService methods.
 * 
 * @version $Rev$ $Date$
 */
public class GenericsTransformer {
    
    public static Bean1<String> getTypeExplicit(Bean1<String> arg) {
        return new Bean1<String>(arg.getItem() == null ? null : arg.getItem()+" AA");
    }
    
    public static <T> Bean1<T> getTypeUnbound(T[] arg) {
        if(arg instanceof String[]) {
            return new Bean1<T>(arg[0]);
        } else if(arg instanceof Integer[]){
            return new Bean1<T>(arg[1]);
        } else {
            return new Bean1<T>(arg[2]);
        }
    }

    public static <T extends Bean2> Bean1<T> getTypeExtends(T[] arg) {
        if(arg instanceof Bean3[]) {
            return new Bean1<T>(arg[0]);
        } else if(arg instanceof Bean31[]) {
            return new Bean1<T>(arg[1]);
        } else {
            return new Bean1<T>(arg[2]);
        }
    }

    public static <T extends Bean1<String>> Bean1<T> getRecursiveTypeBound(T[] arg) {
        if(arg instanceof Bean10[]) {
            return new Bean1<T>(arg[0]);
        } else if(arg instanceof Bean11[]) {
            return new Bean1<T>(arg[1]);
        } else {
            return new Bean1<T>(arg[2]);
        }
    }
    
    public static Bean1<?> getWildcardUnbound(Bean1<?> arg) {
        if(arg.getItem() instanceof String) {
            Bean1<String> temp = new Bean1<String>();
            temp.setItem("Hello "+arg.getItem());
            return temp;
        } else if(arg.getItem() instanceof Integer) {
            Bean1<Integer> temp = new Bean1<Integer>();
            temp.setItem(10+(Integer)arg.getItem());
            return temp;
        } else {
            return new Bean1<String>(arg.toString());
        }
    }
    
    public static Bean1<? super Bean3> getWildcardSuper(Bean1<? super Bean3> arg) {
        Object item = arg.getItem();
        if(item instanceof Bean3) {
            Bean3 temp = new Bean3();
            temp.setName("Hello " + ((Bean3)item).getName());
            temp.setAddress("New "+((Bean3)item).getAddress());
            return new Bean1<Bean3>(temp);
        } else if(item instanceof Bean2) {
            Bean2 temp = new Bean2();
            temp.setName("Hello " + ((Bean3)item).getName());
            return new Bean1<Bean2>(temp);
        } else {
            Bean2 temp = new Bean2();
            temp.setName(item.toString());
            return new Bean1<Bean2>(temp);
        }
    }

    public static Bean1<? extends Bean2> getWildcardExtends(Bean1<? extends Bean2> arg) {
        Bean2 item = arg.getItem();
        if(item instanceof Bean3) {
            Bean3 temp = new Bean3();
            temp.setName("Hello "+item.getName());
            temp.setAddress("New "+((Bean3)item).getAddress());
            return new Bean1<Bean3>(temp);
        } else if(item instanceof Bean31) {
            Bean31 temp = new Bean31();
            temp.setName("Hello "+item.getName());
            temp.setAddress("New "+((Bean31)item).getAddress());
            return new Bean1<Bean31>(temp);
        } else {
            Bean2 temp = new Bean2();
            temp.setName("Hello "+item.getName());
            return new Bean1<Bean2>(temp);
        }
    }
    
    public static Bean2 getPolymorphic(Bean2 arg) {
        return arg;
    }
}
