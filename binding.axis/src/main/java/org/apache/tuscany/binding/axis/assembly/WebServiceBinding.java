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
package org.apache.tuscany.binding.axis.assembly;

import org.apache.tuscany.model.assembly.Binding;

/**
 * A representation of the model object '<em><b>Web Service Binding</b></em>'.
 */
public interface WebServiceBinding extends Binding {
    /**
     * Returns the value of the '<em><b>Port</b></em>' attribute.
     */
    String getPort();

    /**
     * Sets the value of the '{@link org.osoa.sca.model.WebServiceBinding#getPort <em>Port</em>}' attribute.
     */
    void setPort(String value);

} // TWebServiceBinding
