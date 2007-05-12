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
package org.apache.tuscany.sca.databinding.xml;

import java.io.Reader;

import org.apache.tuscany.sca.databinding.PushTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

/**
 * Transform XML string to SAX
 */
public class Reader2SAX extends BaseTransformer<Reader, ContentHandler> implements
    PushTransformer<Reader, ContentHandler> {
    public void transform(Reader source, ContentHandler target, TransformationContext context) {
        try {
            new InputSource2SAX().transform(new InputSource(source), target, context);
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    public Class getSourceType() {
        return Reader.class;
    }

    public Class getTargetType() {
        return ContentHandler.class;
    }

    public int getWeight() {
        return 40;
    }

}
