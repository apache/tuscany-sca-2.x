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

package org.apache.tuscany.databinding.json.axiom;

import javax.xml.stream.XMLStreamException;

import org.codehaus.jettison.badgerfish.BadgerFishXMLStreamReader;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSONDataSource keeps the JSON String inside and consumes it when needed. This is to be kept in the
 * OMSourcedElementImpl and can be used either to expand the tree or get the JSON String directly without expanding.
 * This uses the "Badgerfish" JSON convention.
 * 
 * @version $Rev$ $Date$
 */

public class JSONBadgerfishDataSource extends JSONDataSource {

    public JSONBadgerfishDataSource(JSONObject json) {
        super(json);
    }

    /**
     * Gives the StAX reader using the "Badgerfish" formatted input JSON String.
     * 
     * @return The XMLStreamReader according to the JSON String.
     * @throws javax.xml.stream.XMLStreamException if there is an error while making the StAX reader.
     */
    public javax.xml.stream.XMLStreamReader getReader() throws XMLStreamException {
        try {
            return new BadgerFishXMLStreamReader(json);
        } catch (JSONException e) {
            throw new XMLStreamException(e);
        }

    }
}
