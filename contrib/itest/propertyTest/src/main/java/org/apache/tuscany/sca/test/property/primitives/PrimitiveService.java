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
package org.apache.tuscany.sca.test.property.primitives;

/**
 * Service interface for primitive property tests.
 * This defines the methods and the values typically returned from a test.
 *
 * @version $Rev$ $Date$
 */
public interface PrimitiveService {
    String getImplementationName();
    
    boolean BOOLEAN_VALUE = true;

    boolean isBooleanValue();

    byte BYTE_VALUE = 123;

    byte getByteValue();

    short SHORT_VALUE = 12345;

    short getShortValue();

    int INT_VALUE = 12345678;

    int getIntValue();

    long LONG_VALUE = 9876543210L;

    long getLongValue();

    float FLOAT_VALUE = 1.234e12f;

    float getFloatValue();

    double DOUBLE_VALUE = 1.23456789e87;

    double getDoubleValue();
}
