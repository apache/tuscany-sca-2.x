/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.databinding.util;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.tuscany.databinding.DataPipe;

public class Writer2ReaderDataPipe implements DataPipe<Writer, Reader> {

    private StringWriter writer = new StringWriter();

    public Reader getResult() {
        return new StringReader(writer.toString());
    }

    public Class<Reader> getTargetType() {
        return Reader.class;
    }

    public int getWeight() {
        return 50;
    }

    public Writer getSink() {
        return writer;
    }

    public Class<Writer> getSourceType() {
        return Writer.class;
    }

}
