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
package org.apache.tuscany.core.config;

/**
 * @version $Rev: 368822 $ $Date: 2006-01-13 10:54:38 -0800 (Fri, 13 Jan 2006) $
 */
public class SuperBean {

    public static final int ALL_SUPER_FIELDS = 6;
    public static final int ALL_SUPER_PUBLIC_PROTECTED_FIELDS = 5;

    public static final int ALL_SUPER_METHODS = 4;

    private String superField1;

    public String superField2;

    protected String superField3;

    public void setSuperMethod1(String param) {
    }

    public void setSuperMethod1(int param) {
    }

    public void override(String param) throws Exception {
        throw new Exception("Override not handled");
    }

    public void noOverride() throws Exception {
    }

}
