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
package org.apache.tuscany.persistence.store.journal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.component.SCAExternalizable;
import org.apache.tuscany.spi.component.WorkContext;

/**
 * Utility methods for processing journal records
 *
 * @version $Rev$ $Date$
 */
public final class SerializationHelper {

    private SerializationHelper() {
    }

    /**
     * Serializes and object
     *
     * @param serializable the object to serialize
     * @throws IOException
     */
    public static byte[] serialize(Serializable serializable) throws IOException {
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bas);
        out.writeObject(serializable);
        return bas.toByteArray();
    }

    /**
     * Deserializes an object using the TCCL
     *
     * @param bytes       the serialized object byte array
     * @param workContext the current work context
     */
    public static Object deserialize(byte[] bytes, WorkContext workContext) throws IOException, ClassNotFoundException {
        Object o = new TCCLObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
        if (o instanceof SCAExternalizable) {
            SCAExternalizable externalizable = (SCAExternalizable) o;
            externalizable.setWorkContext(workContext);
            externalizable.reactivate();
        }
        return o;
    }

    /**
     * Breaks a byte array into a series of blocks of the given size
     *
     * @param bytes the byte array to partition
     * @param size  the partition size
     */
    public static List<byte[]> partition(byte[] bytes, int size) {
        assert size > 0;
        List<byte[]> list = new ArrayList<byte[]>();
        int pos = 0;
        while (pos < bytes.length) {
            if (pos + size > bytes.length) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                for (int i = pos; i < bytes.length; i++) {
                    stream.write(bytes[i]);
                }
                byte[] partition = stream.toByteArray();
                list.add(partition);
                pos = pos + partition.length;
            } else {
                byte[] partition = new byte[size];
                for (int i = 0; i < size; i++) {
                    partition[i] = bytes[pos + i];
                }
                list.add(partition);
                pos = pos + size;
            }
        }
        return list;
    }

    /**
     * Creates a serialized header entry that may be written to a log
     *
     * @param operation  the operation type, i.e. {@link Header#INSERT}, {@link Header#UPDATE}, or {@link
     *                   JournalStore#DELETE}
     * @param numRecords the number of blocks that the record will be written two excluding the header block
     * @param ownerId    the id of the owner of the record
     * @param id         the id of the record unique to the owner
     * @param expiration the record expirtation time in milliseconds
     * @return a byte array containing the serialized header
     * @throws IOException
     */
    public static byte[] serializeHeader(short operation, int numRecords, String ownerId, String id, long expiration)
        throws IOException {
        ByteArrayOutputStream stream = null;
        ObjectOutputStream ostream = null;
        try {
            stream = new ByteArrayOutputStream();
            ostream = new ObjectOutputStream(stream);
            ostream.writeShort(operation);
            ostream.writeInt(numRecords);
            ostream.writeObject(ownerId);
            ostream.writeObject(id);
            ostream.writeLong(expiration);
            ostream.flush();
            return stream.toByteArray();
        } finally {
            if (ostream != null) {
                try {
                    ostream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    /**
     * Serializes a unique record id consisting of the owner id  and the record's UUID
     *
     * @param ownerId the id of the owner, typically an SCAObject canonical name
     * @param id      the id associated with the record
     * @return the serialized record byte array
     * @throws IOException
     */
    public static byte[] serializeRecordId(String ownerId, String id)
        throws IOException {
        ByteArrayOutputStream stream = null;
        ObjectOutputStream ostream = null;
        try {
            stream = new ByteArrayOutputStream();
            ostream = new ObjectOutputStream(stream);
            ostream.writeObject(ownerId);
            ostream.writeObject(id);
            ostream.flush();
            return stream.toByteArray();
        } finally {
            if (ostream != null) {
                try {
                    ostream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    /**
     * Deserializes the given header. The first element from {@link Header#getFields()} is assumed to contain the header
     * byte array to deserialize from
     *
     * @param header the header to deserialize
     * @return the deserialized header
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Header deserializeHeader(Header header) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bas = null;
        ObjectInputStream stream = null;
        try {
            bas = new ByteArrayInputStream(header.getFields()[0]);
            stream = new TCCLObjectInputStream(bas);
            header.setOperation(stream.readShort());
            header.setNumBlocks(stream.readInt());
            header.setOwnerId((String) stream.readObject());
            header.setId((String) stream.readObject());
            header.setExpiration(stream.readLong());
            return header;
        } finally {
            if (bas != null) {
                try {
                    bas.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

    }

}
