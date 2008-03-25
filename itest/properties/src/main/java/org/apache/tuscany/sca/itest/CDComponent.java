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

/**
 * It is an Interface which has method declarations. Methods which are to be accessed as a service are declared in this
 * interface. Implementation for these methods is provided in CDComponentImpl Class
 */
public interface CDComponent {

    /**
     * This is a method to get a property value from the SCA runtime environment. It is implemented in the class
     * CDComponentImpl Class
     */
    String getC();

    /**
     * This is a method to get a property value from the SCA runtime environment. It is implemented in the class
     * CDComponentImpl Class
     */
    String getC2();

    /**
     * This is a method to get a property value from the SCA runtime environment. It is implemented in the class
     * CDComponentImpl Class
     */
    String getD();

    /**
     * This is a method to get a property value from the SCA runtime environment. It is implemented in the class
     * CDComponentImpl Class
     */
    String getNoSource();

    /**
     * This is a method to get a property value from the SCA runtime environment. It is implemented in the class
     * CDComponentImpl Class
     */
    String getFileProperty();

    /**
     * This is a method to get a property value from the SCA runtime environment. It is implemented in the class
     * CDComponentImpl Class
     */
    Collection<String> getManyValuesFileProperty();

    /**
     * This is a method to get a property value from the SCA runtime environment. It is implemented in the class
     * CDComponentImpl Class
     */
    int getOverrideValue();
}
