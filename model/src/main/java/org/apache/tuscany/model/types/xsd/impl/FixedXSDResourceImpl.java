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
package org.apache.tuscany.model.types.xsd.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xsd.util.XSDResourceImpl;

/**
 *         <p/>
 *         Extends the EMF XSDResourceImpl and works around a memory leak. The doLoad() method attaches a
 *         static SchemaLocator adapter to the resource and this is holding  the loaded resource and its containing
 *         resource set in memory. The fixes detaches the adapter from the resource after calling doLoad().
 */
public class FixedXSDResourceImpl extends XSDResourceImpl {

    /**
     * Constructor
     */
    public FixedXSDResourceImpl() {
        super();
    }

    /**
     * @param uri
     */
    public FixedXSDResourceImpl(URI uri) {
        super(uri);
    }

    /**
     * @see org.eclipse.xsd.util.XSDResourceImpl#doLoad(java.io.InputStream, java.util.Map)
     */
    protected void doLoad(InputStream inputStream, Map options) throws IOException {
        super.doLoad(inputStream, options);
        XSDResourceImpl.SCHEMA_LOCATOR.setTarget(null);
    }

}
