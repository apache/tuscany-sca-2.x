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

/**
 * Represents a record read from the log
 *
 * @version $Rev$ $Date$
 */
public class JournalRecord {

    private Header header;
    private byte[] data;

    public JournalRecord(byte[] data) {
        this.data = data;
    }

    /**
     * Returns the record header
     *
     * @return the record header
     */
    public Header getHeader() {
        return header;
    }

    /**
     * Sets  the record header
     */
    public void setHeader(Header header) {
        this.header = header;
    }

    /**
     * Returns the serialized data portion of the record which may be assembled from multiple blocks
     *
     * @return the serialized data portion of the record which may be assembled from multiple blocks
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Sets the serialized data portion of the record which may be written across multiple blocks
     */
    public void setData(byte[] data) {
        this.data = data;
    }

}
