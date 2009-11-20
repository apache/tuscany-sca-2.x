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

/**
 * It is an Interface which has method declarations. Methods which are to be accessed as a service are declared in this
 * interface. Implementation for these methods is provided in ABCDComponentImpl Class
 */
public interface ABCDComponent {

    /**
     * This is a method to get a string property value from the SCA runtime environment. It is implemented in the class
     * ABCDComponentImpl
     */
    String getA();

    /**
     * This is a method to get a string property value from the SCA runtime environment. It is implemented in the class
     * ABCDComponentImpl
     */
    String getB();

    /**
     * This is a method to get a string property value from the SCA runtime environment. It is implemented in the class
     * ABCDComponentImpl
     */
    String getC();

    /**
     * This is a method to get a string property value from the SCA runtime environment. It is implemented in the class
     * ABCDComponentImpl
     */
    String getD();
}
