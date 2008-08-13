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

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;

public class WrappingXMLStreamReader extends StreamReaderDelegate implements XMLFragmentStreamReader {

    private boolean done;
    private int level;

    public WrappingXMLStreamReader(XMLStreamReader realReader) throws XMLStreamException {
        super(realReader);
        if (realReader == null) {
            throw new UnsupportedOperationException("Reader cannot be null");
        }

        if (realReader instanceof XMLFragmentStreamReader) {
            ((XMLFragmentStreamReader)realReader).init();
        }

        if (realReader.getEventType() == START_DOCUMENT) {
            // Position to the 1st element
            realReader.nextTag();
        }
        if (realReader.getEventType() != START_ELEMENT) {
            throw new IllegalStateException("The reader is not positioned at START_DOCUMENT or START_ELEMENT");
        }
        this.done = false;
        this.level = 1;
    }

    @Override
    public boolean hasNext() throws XMLStreamException {
        return !done && super.hasNext();
    }

    @Override
    public int next() throws XMLStreamException {
        if (!hasNext()) {
            throw new IllegalStateException("No more events");
        }
        int event = super.next();
        if (!super.hasNext()) {
            done = true;
        }
        if (event == START_ELEMENT) {
            level++;
        } else if (event == END_ELEMENT) {
            level--;
            if (level == 0) {
                done = true;
            }
        }
        return event;
    }

    @Override
    public int nextTag() throws XMLStreamException {
        int event = 0;
        while (true) {
            event = next();
            if (event == START_ELEMENT || event == END_ELEMENT) {
                return event;
            }
        }
    }
    
    public void setParentNamespaceContext(NamespaceContext nsContext) {
        // nothing to do here
    }

    public void init() {
        // Nothing to do here
    }
    
    public boolean isDone() {
        return done;
    }

}
