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

import org.osoa.sca.annotations.Remotable;

/**
 * It is an Interface which has method declarations. Methods which are to be accessed as a service are declared in this
 * interface. Implementation for these methods is provided in ABComponentImpl Class
 */
@Remotable
public interface ABComponent {

    /**
     * This is a method to get a string property value from the SCA runtime environment. It is implemented in the class
     * ABComponentImpl
     */

    String getA();

    /**
     * This is a method to get a string property value from the SCA runtime environment. It is implemented in the class
     * ABComponentImpl
     */
    String getB();

    /**
     * This is a method to get a string property value from the SCA runtime environment. It is implemented in the class
     * ABComponentImpl
     */
    String getZ();

    /**
     * This is a method to get an integer property value from the SCA runtime environment. It is implemented in the
     * class ABComponentImpl
     */
    int getIntValue();

    /**
     * This is a method to get a string property value from the SCA runtime environment. It is implemented in the class
     * ABComponentImpl
     */
    String getF();

    /**
     * This is a method to get collection of property values from the SCA runtime environment. It is implemented in the
     * class ABComponentImpl
     */
    Collection<String> getManyStringValues();

    /**
     * This is a method to get collection of property value from the SCA runtime environment. It is implemented in the
     * class ABComponentImpl
     */
    Collection<Integer> getManyIntegers();
}
