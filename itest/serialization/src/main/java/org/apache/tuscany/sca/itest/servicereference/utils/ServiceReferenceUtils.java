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
package org.apache.tuscany.sca.itest.servicereference.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.osoa.sca.CallableReference;
import org.osoa.sca.ServiceReference;

/**
 * Utility methods that are used by the ServiceReference Serialization tests
 * 
 * @version $Date $Revision$
 */
public final class ServiceReferenceUtils {

    /**
     * Constructor
     */
    private ServiceReferenceUtils() {
    }

    /**
     * Serializes the specified Object to a byte[]
     * 
     * @param obj The Object to Serialize
     * @return The Serialized Object as a byte[]
     * @throws IOException Failed to Serialize the Object
     */
    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream bos = null;
        try {
            ObjectOutputStream oos = null;
            bos = new ByteArrayOutputStream();
            try {
                oos = new ObjectOutputStream(bos);
                oos.writeObject(obj);
                oos.flush();
            } finally {
                if (oos != null) {
                    oos.close();
                }
            }
        } finally {
            if (bos != null) {
                bos.close();
            }
        }

        return bos.toByteArray();
    }

    /**
     * Deserializes the specified byte[] into a ServiceReference
     * 
     * @param serializedSR The Serialized ServiceReference to deserialize
     * @return The deserialized ServiceReference
     * @throws IOException Failed to deserialize the ServiceReference
     * @throws ClassNotFoundException Failed to deserialize the ServiceReference
     */
    public static ServiceReference<?> deserializeServiceReference(byte[] serializedSR)
            throws IOException, ClassNotFoundException {
        return (ServiceReference<?>) deserialize(serializedSR);
    }

    /**
     * Deserializes the specified byte[] into a CallableReference
     * 
     * @param callableRef The Serialized CallableReference to deserialize
     * @return The deserialized ServiceReference
     * @throws IOException Failed to deserialize the CallableReference
     * @throws ClassNotFoundException Failed to deserialize the CallableReference
     */
    public static CallableReference<?> deserializeCallableReference(byte[] callableRef)
            throws IOException, ClassNotFoundException {
        return (CallableReference<?>) deserialize(callableRef);
    }

    /**
     * Deserializes the specified byte[] into an Object
     * 
     * @param serializedObj The Serialized Object to deserialize
     * @return The deserialized Object
     * @throws IOException Failed to deserialize the Object
     * @throws ClassNotFoundException Failed to deserialize the Object
     */
    public static Object deserialize(byte[] serializedSR) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = null;
        try {
            ObjectInputStream ois = null;
            bis = new ByteArrayInputStream(serializedSR);
            try {
                ois = new ObjectInputStream(bis);
                Object obj = ois.readObject();
                return obj;
            } finally {
                if (ois != null) {
                    ois.close();
                }
            }
        } finally {
            if (bis != null) {
                bis.close();
            }
        }
    }
}
