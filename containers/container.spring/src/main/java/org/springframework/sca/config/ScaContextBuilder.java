/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created on 10-Apr-2006 by Adrian Colyer
 */
package org.springframework.sca.config;

import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.sca.ScaAdapter;
import org.springframework.sca.ScaComposite;

/**
 * @author Hal Hildebrand
 *         Date: Apr 11, 2006
 *         Time: 4:33:33 PM
 */
public class ScaContextBuilder {

    private String moduleId;

    private ScaAdapter scaAdapter;

    private static final String MODULE_ID = "moduleId";

    private static final String SCA_ADAPTER = "scaAdapter";

    private static final String SCA_COMPOSITE_BEAN_NAME = "scaComposite";

    public String getModuleId() {
        return this.moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public void setScaAdapter(ScaAdapter scaAdapter) {
        this.scaAdapter = scaAdapter;
    }

    public ScaAdapter getScaAdapter() {
        return this.scaAdapter;
    }

    public ApplicationContext construct() {
        GenericApplicationContext parent = new GenericApplicationContext();
        BeanDefinition bd = new RootBeanDefinition(ScaComposite.class, true);

        bd.getPropertyValues().addPropertyValue(new PropertyValue(MODULE_ID, moduleId));
        bd.getPropertyValues().addPropertyValue(new PropertyValue(SCA_ADAPTER, scaAdapter));
        parent.registerBeanDefinition(SCA_COMPOSITE_BEAN_NAME, bd);
        parent.refresh();
        return parent;
    }
}
