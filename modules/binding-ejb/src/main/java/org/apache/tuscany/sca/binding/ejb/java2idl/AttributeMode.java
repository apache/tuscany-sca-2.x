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
package org.apache.tuscany.sca.binding.ejb.java2idl;

import java.io.ObjectStreamException;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

/**
 * Mode of an IDL attribute
 */
public final class AttributeMode implements IDLEntity {
    public static final int INT_NORMAL = 0;
    public static final int INT_READONLY = 1;
    public static final AttributeMode ATTR_NORMAL = new AttributeMode(INT_NORMAL);
    public static final AttributeMode ATTR_READONLY = new AttributeMode(INT_READONLY);
    private static final long serialVersionUID = 8549003678215309053L;

    private final int value;

    private AttributeMode(int value) {
        this.value = value;
    }

    public static AttributeMode valueOf(final int value) {
        switch (value) {
            case INT_NORMAL:
                return ATTR_NORMAL;
            case INT_READONLY:
                return ATTR_READONLY;
            default:
                throw new BAD_PARAM("Invalid Attribute Mode");
        }
    }

    public int getValue() {
        return value;
    }

    Object readResolve() throws ObjectStreamException {
        return valueOf(getValue());
    }
}
