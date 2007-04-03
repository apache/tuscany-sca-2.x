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

package org.apache.tuscany.sca.test.exceptions;

public interface ExceptionThrower {
    public static final String SO_THEY_SAY = "All is good that ends good.";

    public String theGood() throws org.apache.tuscany.sca.test.exceptions.Checked;

    public String theBad() throws org.apache.tuscany.sca.test.exceptions.Checked;

    public String theUgly() throws org.apache.tuscany.sca.test.exceptions.Checked;

}
