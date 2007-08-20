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
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.tuscany.sca.databinding.DataPipe;
import org.apache.tuscany.sca.databinding.DataPipeTransformer;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;

public class Writer2ReaderDataPipe extends BaseTransformer<Writer, Reader> implements DataPipeTransformer<Writer, Reader> {

    public DataPipe<Writer, Reader> newInstance() {
        return new Pipe();
    }

    @Override
    public Class getTargetType() {
        return Reader.class;
    }

    @Override
    public int getWeight() {
        return 50;
    }

    @Override
    public Class getSourceType() {
        return Writer.class;
    }

    private static class Pipe implements DataPipe<Writer, Reader> {
        private StringWriter writer = new StringWriter();

        public Reader getResult() {
            return new StringReader(writer.toString());
        }

        public Writer getSink() {
            return writer;
        }
    }

}
