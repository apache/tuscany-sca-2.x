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

package org.apache.tuscany.databinding.castor;

import org.apache.tuscany.databinding.extension.DataBindingExtension;
import org.exolab.castor.xml.XMLClassDescriptor;

public class CastorBinding extends DataBindingExtension {

    public CastorBinding() {
        super("org.exolab.castor.xml", Object.class, false);
    }

    @Override
    public boolean isSupported(Class javaClass) {
        try {
            Class descriptorClass = Class.forName(javaClass.getName() + "Descriptor", false, javaClass.getClassLoader());
            return XMLClassDescriptor.class.isAssignableFrom(descriptorClass);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
