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
 */
package org.springframework.sca;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author Andy Piper
 * @since 2.1
 */
public class ScaAdapterPostProcessor implements BeanPostProcessor {
    private ScaAdapter scaAdapter;

    public ScaAdapterPostProcessor(ScaAdapter adapter) {
        this.scaAdapter = adapter;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ScaAdapterAware) {
            if (this.scaAdapter == null) {
                throw new IllegalStateException("Cannot satisfy ScaAdapterAware for bean '"
                    + beanName + "' without ScaAdapater");
            }
            ((ScaAdapterAware) bean).setScaAdapter(scaAdapter);
        }
        return bean;
    }

    public Object postProcessAfterInitialization(Object object, String string) throws BeansException {
        return object;
    }

    public ScaAdapter getScaAdapter() {
        return scaAdapter;
    }

}
