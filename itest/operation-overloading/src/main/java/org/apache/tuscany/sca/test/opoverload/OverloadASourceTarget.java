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

package org.apache.tuscany.sca.test.opoverload;

/**
 * This is an Interface which has methods declared but not implemented, These methods will be implemented in
 * OverloadATarget Class and OverloadASource This class has methods with overloaded parameters.
 */

//@AllowsPassByReference
public interface OverloadASourceTarget {
    final String opName = "operationA:";

    /**
     * Method with no parameters
     */
    String operationA();

    /**
     * Method with one integer parameter
     */
    String operationA(int parm1);

    /**
     * Method with one integer parameter and one string parameter
     */
    String operationA(int parm1, String parm2);

    /**
     * Method with one one string parameter and one integer parameter
     */
    String operationA(String parm1, int parm2);

    /**
     * Method with one string parameter
     */
    String operationA(String string);

    /**
     * Method which throws an illegal argument exception in case of any exceptions.
     */
    String[] operationAall();

}
