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
package org.apache.tuscany.core.deprecated.sdo.util.impl;

import commonj.sdo.DataObject;
import org.eclipse.emf.ecore.sdo.EDataObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.apache.tuscany.core.deprecated.sdo.util.CopyHelper;

/**
 * An implementation of the CopyHelper interface.
 */
public class CopyHelperImpl implements CopyHelper {

    /**
     * Constructor
     */
    public CopyHelperImpl() {
        super();
    }

    /**
     * @see org.apache.tuscany.core.deprecated.sdo.util.CopyHelper#copy(commonj.sdo.DataObject)
     */
    public DataObject copy(DataObject dataObject) {
        return (DataObject) EcoreUtil.copy((EDataObject) dataObject);
    }

}
