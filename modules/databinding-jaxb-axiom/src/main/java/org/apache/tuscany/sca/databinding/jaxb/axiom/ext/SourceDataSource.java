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

package org.apache.tuscany.sca.databinding.jaxb.axiom.ext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.ds.OMDataSourceExtBase;
import org.apache.axiom.om.util.StAXUtils;

/**
 * OMDataSource backed by a source
 */
public class SourceDataSource extends OMDataSourceExtBase {
    private Source data;

    public SourceDataSource(Source data) {
        super();
        this.data = data;
    }

    public void close() {
    }

    public OMDataSourceExt copy() {
        return new SourceDataSource(data);
    }

    public Object getObject() {
        return data;
    }

    public XMLStreamReader getReader() throws XMLStreamException {

        try {
            String encoding = "UTF-8";
            InputStream is = new ByteArrayInputStream(getXMLBytes(encoding));
            return StAXUtils.createXMLStreamReader(is, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new XMLStreamException(e);
        }
    }

    public byte[] getXMLBytes(String encoding) throws UnsupportedEncodingException {
        byte[] bytes = null;
        try {
            bytes = (byte[])null;

            if (data instanceof StreamSource) {
                InputStream is = ((StreamSource)data).getInputStream();
                if (is != null) {
                    bytes = getBytesFromStream(is);
                }
            } else {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Result result = new StreamResult(out);
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.transform(data, result);
                bytes = out.toByteArray();
            }
        } catch (OMException e) {
            throw e;
        } catch (UnsupportedEncodingException e) {
            throw e;
        } catch (Throwable e) {
            throw new OMException(e);
        }

        return bytes;
    }

    public boolean isDestructiveRead() {
        return false;
    }

    public boolean isDestructiveWrite() {
        return false;
    }

    private static byte[] getBytesFromStream(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        while (true) {
            int size = is.read(buf);
            if (size < 0) {
                break;
            }
            bos.write(buf, 0, size);
        }
        return bos.toByteArray();
    }
}
