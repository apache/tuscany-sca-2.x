/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.core.deprecated.sdo.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import commonj.sdo.DataObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @deprecated replace with usage of SDO2 equivalent
 */
public interface XMLHelper {

    /**
     * Creates and returns a DataObject with the data loaded from the input file.
     *
     * @param uri input file name
     * @return the new root DataObject loaded
     */
    DataObject load(String uri) throws IOException;

    /**
     * Creates and returns a DataObject with the data read from the input stream.
     *
     * @param inputStream specifies the input stream to read from
     * @return the new root DataObject loaded
     */
    DataObject load(InputStream inputStream) throws IOException;

    /**
     * Creates and returns a DataObject with the data read from the input DOM node.
     *
     * @param node specifies the input node to read from
     * @return the new root DataObject loaded
     */
    DataObject load(Node node) throws IOException;

    /**
     * Serializes a DataObject into the specified file
     *
     * @param dataObject specifies DataObject to be saved
     * @param uri        specifies the URI to be used
     * @throws IOException
     */
    void save(DataObject dataObject, String uri) throws IOException;

    /**
     * Serializes a DataObject into the specified stream
     *
     * @param dataObject   specifies DataObject to be saved
     * @param outputStream specifies the output stream to write to
     * @throws IOException
     */
    void save(DataObject dataObject, OutputStream outputStream) throws IOException;

    /**
     * Serializes a DataObject into the specified DOM document
     *
     * @param dataObject   specifies DataObject to be saved
     * @param outputStream specifies the output document to write to
     * @throws IOException
     */
    void save(DataObject dataObject, Document document) throws IOException;

    /**
     * Prints a DataObject into the specified stream
     *
     * @param dataObject   The data object to be serialized
     * @param outputStream specifies the output stream to write to
     * @throws IOException
     */
    void print(DataObject dataObject, OutputStream outputStream);

    /**
     * A serializable object that wraps a DataObject and is able to serialize/deserialize it without requiring it
     * to be in a DataGraph. This should be removed when we port to SDO 2.0.
     */
    interface DataObjectSerializer extends Serializable {

        /**
         * Returns the wrapped DataObject
         *
         * @return
         */
        DataObject getDataObject();
    }

    /**
     * Creates a serializer for a DataObject.
     * @param dataObject
     */
    DataObjectSerializer createDataObjectSerializer(DataObject dataObject);
}