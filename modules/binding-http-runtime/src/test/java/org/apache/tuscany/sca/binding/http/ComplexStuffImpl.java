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

package org.apache.tuscany.sca.binding.http;

public class ComplexStuffImpl implements ComplexStuff {

    @Override
    public BeanA echoBeanA(BeanA bean) {
        return bean;
    }

    @Override
    public String multiParams(int x, String s, Boolean b) {
        return x + s + b;
    }

    @Override
    public String noArgs() {
        return "noArgs";
    }

    @Override
    public void voidReturn() {
    }

    @Override
    public String checkedException() throws SomeException {
        throw new SomeException("some msg");
    }

    @Override
    public String runtimeException() {
        throw new RuntimeException("bang");
    }

}
