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
package org.apache.tuscany.model.assembly.impl;

import java.util.Iterator;

import org.eclipse.emf.ecore.sdo.EDataObject;

import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;

/**
 */
public class AssemblyModelVisitorHelperImpl {

    public static boolean accept(AssemblyModelObject modelObject, AssemblyModelVisitor visitor) {
        if (!visitor.visit(modelObject))
            return false;
        for (Iterator i = ((EDataObject) modelObject).eContents().iterator(); i.hasNext();) {
            Object child = i.next();
            if (child instanceof AssemblyModelObject) {
                if (!((AssemblyModelObject) child).accept(visitor))
                    return false;
            }
        }
        return true;
    }

}
