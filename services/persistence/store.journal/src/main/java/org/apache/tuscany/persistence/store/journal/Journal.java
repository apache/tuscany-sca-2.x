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

import java.io.IOException;

import org.apache.tuscany.spi.services.store.StoreWriteException;

import org.objectweb.howl.log.Configuration;
import org.objectweb.howl.log.LogClosedException;
import org.objectweb.howl.log.LogFileOverflowException;
import org.objectweb.howl.log.LogRecordSizeException;
import org.objectweb.howl.log.Logger;

/**
 * Extends the HOWL logger implementation adding convenience methods for processing records
 *
 * @version $Rev$ $Date$
 */
public class Journal extends Logger {
    public static final short HEADER = 0;
    public static final short RECORD = 1;

    public Journal() throws IOException {
    }

    public Journal(Configuration config) throws IOException {
        super(config);
    }

    /**
     * Writes a header record to the log. The format of the header is defined by {@link
     * SerializationHelper#createHeader(short, int, String, String, long)}
     *
     * @param bytes the record as a byte array
     * @param force true if the disk write should be forced
     * @return the log entry key
     * @throws StoreWriteException
     */
    public long writeHeader(byte[] bytes, boolean force) throws StoreWriteException {
        try {
            return put(HEADER, new byte[][]{bytes}, force);
        } catch (LogRecordSizeException e) {
            throw new StoreWriteException(e);
        } catch (LogFileOverflowException e) {
            throw new StoreWriteException(e);
        } catch (IOException e) {
            throw new StoreWriteException(e);
        } catch (LogClosedException e) {
            throw new StoreWriteException(e);
        } catch (InterruptedException e) {
            throw new StoreWriteException(e);
        }
    }

    /**
     * Writes a record block to the log. The block may be the complete number of bytes or a chunked part of thereof if
     * it does not fit within HOWL's block size limit.
     *
     * @param data  the data to write
     * @param id    the unique record id consisting of the owner id and the UUID
     * @param force true if the disk write should be force. For records that are written in multiple blocks, only the
     *              last write should be forced.
     * @return the log entry key
     * @throws StoreWriteException
     */
    public long writeBlock(byte[] data, byte[] id, boolean force) throws StoreWriteException {
        try {
            return put(RECORD, new byte[][]{id, data}, force);
        } catch (LogRecordSizeException e) {
            throw new StoreWriteException(e);
        } catch (LogFileOverflowException e) {
            throw new StoreWriteException(e);
        } catch (IOException e) {
            throw new StoreWriteException(e);
        } catch (LogClosedException e) {
            throw new StoreWriteException(e);
        } catch (InterruptedException e) {
            throw new StoreWriteException(e);
        }
    }

}
