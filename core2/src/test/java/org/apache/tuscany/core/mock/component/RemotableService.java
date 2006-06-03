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
package org.apache.tuscany.core.mock.component;

import org.osoa.sca.annotations.Remotable;

/**
 * Used for testing basic remoting operations
 *
 * @version $Rev$ $Date$
 */

@Remotable
public interface RemotableService {

    public void syncOneWay(String msg);

    public String syncTwoWay(String msg);

    public DataObject syncTwoWayCustomType(DataObject val);

    public String getString();

    public void setString(String string);

}
