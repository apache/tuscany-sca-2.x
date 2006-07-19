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

package org.apache.tuscany.databinding.xml;

import java.io.InputStream;
import java.io.Reader;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.tuscany.databinding.DataBinding;

public class StreamBinding implements DataBinding {
    public static final String NAME = "STREAM";

    public static enum Type {
        STREAM, READER, STRING
    }

    public Result createResult(Class resultType) {
        return new StreamResult();
    }

    public Source createSource(Object source, Class sourceType) {
        if (source instanceof InputStream)
            return new StreamSource((InputStream) source);
        else if (source instanceof Reader)
            return new StreamSource((Reader) source);
        return null;
    }

    public boolean isSink() {
        return false;
    }

    public String getName() {
        return NAME;
    }

}
