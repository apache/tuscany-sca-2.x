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
package org.apache.tuscany.container.java.integration;

import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;
import org.apache.tuscany.core.sdo.helper.SDOHelper;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.DataFactory;

/**
 * @version $Rev$ $Date$
 */
@Service(HelloWorldService.class)
public class HelloWorldMCImpl implements HelloWorldService {

    @Property
    public String locale;

    public String getBar() {
        return bar;
    }

    @Property(name= "bar", required=true)
     public void setXBar(String bar) {
        this.bar = bar;
    }

    public String bar;

    @Reference(name="greetingProvider")
    public void setGreetingProvider(GreetingProvider greetingProvider) {
        this.greetingProvider2 = greetingProvider;
    }

    public GreetingProvider greetingProvider2;

    @Reference(required=false)
    public GreetingProvider foo;

    public String getGreetings(String name) {
        return greetingProvider2.getGreeting(name, locale);
    }


}
