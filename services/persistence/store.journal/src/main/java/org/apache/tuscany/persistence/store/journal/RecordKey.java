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

import java.util.UUID;

/**
 * Used by the store cache to retrieve record entries
 *
 * @version $Rev$ $Date$
 */
public class RecordKey {

    private UUID id;
    private String ownerName;

    public RecordKey(UUID id, String ownerName) {
        this.id = id;
        this.ownerName = ownerName;
    }

    public UUID getId() {
        return id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RecordKey recordKey = (RecordKey) o;
        if (id != null ? !id.equals(recordKey.id) : recordKey.id != null) {
            return false;
        }
        return !(ownerName != null ? !ownerName.equals(recordKey.ownerName) : recordKey.ownerName != null);
    }

    public int hashCode() {
        int result;
        result = id != null ? id.hashCode() : 0;
        result = 29 * result + (ownerName != null ? ownerName.hashCode() : 0);
        return result;
    }
}
