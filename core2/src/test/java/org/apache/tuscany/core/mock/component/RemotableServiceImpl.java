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

import junit.framework.Assert;
import org.osoa.sca.annotations.Property;


/**
 * @version $Rev: 368822 $ $Date: 2006-01-13 10:54:38 -0800 (Fri, 13 Jan 2006) $
 */
public class RemotableServiceImpl implements RemotableService {

    // ----------------------------------
    // Properties
    // ----------------------------------

    @Property(name = "string", required = true)
    private String mString;

    public String getString() {
        return mString;
    }

    public void setString(String string) {
        mString = string;
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public void syncOneWay(String msg) {
        Assert.assertEquals("hello", msg);
    }

    public String syncTwoWay(String msg) {
        return "response";
    }

    public DataObject syncTwoWayCustomType(DataObject val) {
        Assert.assertEquals("hello", val.getStringValue());
        DataObject dto = new DataObject();
        dto.setStringValue("return");
        return dto;
    }
}
