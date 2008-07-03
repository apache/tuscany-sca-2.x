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

package org.apache.tuscany.sca.itest.databindings.jaxb.impl;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;

/**
 * StandardTypesTransformer class that provide for transforming input provided to StandardTypesService methods.
 * 
 * @version $Rev$ $Date$
 */
public class StandardTypesTransformer {
    
    public static Object getNewObject(Object obj) {
        if(obj instanceof String) {
            return "Hello "+obj;
        } else if(obj instanceof Integer) {
            return new Integer(-((Integer)obj).intValue());
        } else if(obj instanceof Double) {
            return new Double(-((Double)obj).doubleValue());
        }
        
        return obj;
    }
    
    /**
     * Returns a copy of the source object if the input is DOMSource, SAXSource or StreamSource.
     * Returns the input object as is for other types.
     */
    public static Source getNewSource(Source src) {
        Source ret = null;
        if(src instanceof DOMSource) {
            DOMSource dsrc = (DOMSource)src;
            ret = new DOMSource(dsrc.getNode() != null ? dsrc.getNode().cloneNode(true) : null);
        } else if(src instanceof SAXSource) {
            SAXSource ssrc = (SAXSource)src;
            if(ssrc.getInputSource().getByteStream() != null) {
                InputStream inp = ssrc.getInputSource().getByteStream();
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                int b;
                try {
                    while((b = inp.read()) != -1) {
                        bout.write(b);
                    }
                } catch (IOException ignored) {
                }
                try { bout.close();} catch (IOException ignored) {}
                try { inp.reset();} catch (IOException ignored) {}
                ret = new SAXSource(new InputSource(new ByteArrayInputStream(bout.toByteArray())));
            } else if(ssrc.getInputSource().getCharacterStream() != null) {
                Reader rdr = ssrc.getInputSource().getCharacterStream();
                CharArrayWriter caw = new CharArrayWriter();
                try {
                    int c;
                    while((c = rdr.read()) != -1) {
                        caw.append((char)c);
                    }
                } catch (IOException ignored) {
                }
                caw.close();
                try{ rdr.reset();} catch(IOException ignored) {}
                ret = new SAXSource(new InputSource(new CharArrayReader(caw.toCharArray())));
            } else {
                ret = new SAXSource();
            }
        } else if(src instanceof StreamSource) {
            StreamSource ssrc = (StreamSource)src;
            if(ssrc.getInputStream() != null) {
                InputStream inp = ssrc.getInputStream();
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                int b;
                try {
                    while((b = inp.read()) != -1) {
                        bout.write(b);
                    }
                } catch (IOException ignored) {
                }
                try { bout.close();} catch (IOException ignored) {}
                try { inp.reset();} catch (IOException ignored) {}
                ret = new StreamSource(new ByteArrayInputStream(bout.toByteArray()));
            } else if(ssrc.getReader() != null) {
                Reader rdr = ssrc.getReader();
                CharArrayWriter caw = new CharArrayWriter();
                try {
                    int c;
                    while((c = rdr.read()) != -1) {
                        caw.append((char)c);
                    }
                } catch (IOException ignored) {
                }
                caw.close();
                try{ rdr.reset();} catch(IOException ignored) {}
                ret = new StreamSource(new CharArrayReader(caw.toCharArray()));
            } else {
                ret = new StreamSource();
            }
        }
        
        if(ret != null) {
            ret.setSystemId(src.getSystemId());
        } else {
            ret = src;
        }
        return ret;
    }
    
    public static Image getNewImage(Image arg) {
        arg.getGraphics().drawOval(2, 2, 7, 7);
        return arg;
    }
}
