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
package org.apache.tuscany.core.mock.component;

import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

/**
 * @version $Rev$ $Date$
 */
public class BasicInterfaceImpl implements BasicInterface {

    @Property
    public String publicProperty;
    @Reference
    public BasicInterface publicReference;
    @Property
    protected String protectedProperty;
    @Reference
    protected BasicInterface protectedReference;
    private String privateProperty;
    private BasicInterface privateReference;

    @Property
    public void setPrivateProperty(String privateProperty) {
        this.privateProperty = privateProperty;
    }

    @Reference
    public void setPrivateReference(BasicInterface privateReference) {
        this.privateReference = privateReference;
    }

    public String returnsProperty() {
        return privateProperty;
    }

    public BasicInterface returnsReference() {
        return privateReference;
    }

    public int returnsInt() {
        return 0;
    }
}
