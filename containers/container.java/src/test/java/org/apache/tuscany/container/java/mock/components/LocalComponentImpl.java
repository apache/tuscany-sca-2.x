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
package org.apache.tuscany.container.java.mock.components;

import org.osoa.sca.ModuleContext;
import org.osoa.sca.annotations.ComponentName;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

/**
 * A test local service
 *
 * @version $Rev$ $Date$
 */

public class LocalComponentImpl {

    @ComponentName
    protected String name;

    public String getName() {
        return name;
    }

    @Context
    protected ModuleContext moduleCtx;

    public ModuleContext getModuleContext() {
        return moduleCtx;
    }

    @Property(name = "fieldSetter", required = true)
    private String mfieldSetter;

    public String getfieldSetter() {
        return mfieldSetter;
    }

    public void setfieldSetter(String pfieldSetter) throws Exception {
        throw new Exception("Set method instead of field");
    }

    private String mMethodSetter;
    boolean mSetByMethod;

    public String getMethodSetter() throws Exception {
        if (mSetByMethod) {
            return mMethodSetter;
        }
        throw new Exception("Property method setter failed");

    }

    @Property(required = true)
    public void setMethodSetter(String pMethodSetter) {
        mSetByMethod = true;
        mMethodSetter = pMethodSetter;
    }

    @Reference(name = "requiredDataObject", required = true)
    private DataObject mRequiredDataObject;

    public DataObject getRequiredDataObject() {
        return mRequiredDataObject;
    }

    public void setRequiredDataObject(DataObject pRequiredDataObject) {
        mRequiredDataObject = pRequiredDataObject;
    }

    @Reference(name = "optionalDataObject")
    private DataObject mOptionalDataObject;

    public DataObject getOptionalDataObject() {
        return mOptionalDataObject;
    }

    public void setOptionalDataObject(DataObject pOptionalDataObject) {
        mOptionalDataObject = pOptionalDataObject;
    }

}
