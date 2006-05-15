/**
 *
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.tuscany.databinding.sdo;

import commonj.sdo.DataObject;
import commonj.sdo.helper.CopyHelper;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.injection.ObjectCreationException;

/**
 * Creates new instances of an SDO
 *
 * @version $Rev$ $Date$
 */
public class SDOObjectFactory implements ObjectFactory<DataObject> {

    private DataObject dataObject;

    public SDOObjectFactory(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    public DataObject getInstance() throws ObjectCreationException {
        return CopyHelper.INSTANCE.copy(dataObject);
    }

    public void releaseInstance(DataObject instance) {
    }

}

