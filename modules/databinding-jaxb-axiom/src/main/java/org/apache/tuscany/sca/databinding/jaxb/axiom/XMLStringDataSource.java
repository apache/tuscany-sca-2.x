/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tuscany.sca.databinding.jaxb.axiom;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.ds.OMDataSourceExtBase;
import org.apache.axiom.om.util.StAXUtils;

/**
 * OMDataSource backed by a string containing xml data
 */
// FIXME: To be refectored into databinding-axiom
public class XMLStringDataSource extends OMDataSourceExtBase {
    private String data;

    public XMLStringDataSource(String data) {
        super();
        this.data = data;
    }

    public void close() {
    }

    public OMDataSourceExt copy() {
        return new XMLStringDataSource(data);
    }

    public Object getObject() {
        return data;
    }

    public XMLStreamReader getReader() throws XMLStreamException {
        StringReader reader = new StringReader(data);
        return StAXUtils.createXMLStreamReader(reader);
    }

    public void serialize(Writer writer, OMOutputFormat format) throws XMLStreamException {
        try {
            writer.write(data);
        } catch (UnsupportedEncodingException e) {
            throw new XMLStreamException(e);
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }


    public byte[] getXMLBytes(String encoding) throws UnsupportedEncodingException {
        return data.getBytes(encoding);
    }

    public boolean isDestructiveRead() {
        return false;
    }

    public boolean isDestructiveWrite() {
        return false;
    }

}
