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

package datatypes.impl;

import commonj.sdo.DataObject;
import java.util.List;

import datatypes.GetDataServiceWithoutException;

public class GetDataServiceWithoutExceptionImpl implements GetDataServiceWithoutException {

    public byte[] getMessage(String paramString) {
        return null;
    }

    public List<byte[]> getMessageList(String paramString) {
        return null;
    }

    public DataObject getMessageSDO(String paramString) {
        return null;
    }
    
    public List<DataObject> getMessageListSDOList(String paramString) {
        return null;
    }

    public DataObject[] getMessageListSDOArr(String paramString) {
        return null;
    }

    public DataObject getMessageListSDOinSDO(String paramString) {
        return null;
    }
}

