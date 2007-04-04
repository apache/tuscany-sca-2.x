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

package org.apache.tuscany.databinding.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.apache.tuscany.databinding.impl.GroupDataBinding;
import org.apache.tuscany.idl.util.XMLType;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

/**
 * A Group DataBinding
 * 
 * @version $Rev$ $Date$
 */
public class XMLGroupDataBinding extends GroupDataBinding {

    public XMLGroupDataBinding() {
        super(new Class[] {InputStream.class, OutputStream.class, Reader.class, Writer.class, Source.class,
                           Result.class, InputSource.class, ContentHandler.class, XMLStreamReader.class,
                           XMLStreamWriter.class, XMLEventReader.class, XMLEventWriter.class});
    }

    @Override
    protected Object getLogical(Class<?> markerType, Annotation[] annotations) {
        return XMLType.UNKNOWN;
    }

}
