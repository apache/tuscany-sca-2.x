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

import org.osoa.sca.annotations.Property;

/**
 * Implementation where all primitive properties are passed in the constructor.
 * 
 * @version $Rev$ $Date$
 */
public class ConstructorPrimitives implements PrimitiveService {
    private boolean booleanValue;

    private byte byteValue;

    private short shortValue;

    private int intValue;

    private long longValue;

    private float floatValue;

    private double doubleValue;

    public ConstructorPrimitives(@Property(name = "booleanValue") boolean booleanValue,
                                 @Property(name = "byteValue") byte byteValue,
                                 @Property(name = "shortValue") short shortValue,
                                 @Property(name = "intValue") int intValue,
                                 @Property(name = "longValue") long longValue,
                                 @Property(name = "floatValue") float floatValue,
                                 @Property(name = "doubleValue") double doubleValue) {
        this.booleanValue = booleanValue;
        this.byteValue = byteValue;
        this.shortValue = shortValue;
        this.intValue = intValue;
        this.longValue = longValue;
        this.floatValue = floatValue;
        this.doubleValue = doubleValue;
    }

    public boolean isBooleanValue() {
        return booleanValue;
    }

    public byte getByteValue() {
        return byteValue;
    }

    public short getShortValue() {
        return shortValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public String getImplementationName() {
        return getClass().getSimpleName();
    }
}
