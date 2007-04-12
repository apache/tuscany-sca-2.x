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
package org.apache.tuscany.implementation.java.util;

/**
 * @version $Rev$ $Date$
 */
public class SuperBean {

    public static final int ALL_SUPER_FIELDS = 6;
    public static final int ALL_SUPER_PUBLIC_PROTECTED_FIELDS = 5;
    public static final int ALL_SUPER_METHODS = 4;
    public String superField2;

    protected String superField3;

    private String superField1;

    public void setSuperMethod1(String param) {
    }

    public void setSuperMethod1(int param) {
    }

    public void override(String param) throws Exception {
        throw new Exception("Override not handled");
    }

    public void noOverride() throws Exception {
    }

}
