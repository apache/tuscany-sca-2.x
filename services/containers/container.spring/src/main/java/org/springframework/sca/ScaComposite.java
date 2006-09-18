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
package org.springframework.sca;

import org.springframework.beans.factory.InitializingBean;

/**
 * Bean that represents an Sca composite component. An instance of this bean is created when the &lt;sca:composite
 * module-id="xxx"/&gt; element is declared.
 *
 * @author Adrian Colyer
 * @since 2.0
 */
public class ScaComposite implements InitializingBean {

    private String component;
    private ScaAdapter scaAdapter = new DefaultScaAdapter();

    public String getComponent() {
        return this.component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public void setScaAdapter(ScaAdapter scaAdapter) {
        this.scaAdapter = scaAdapter;
    }

    public void setScaAdapterClass(Class adapterClass) {
        if (!ScaAdapter.class.isAssignableFrom(adapterClass)) {
            throw new IllegalArgumentException(
                "Adapter class '" + adapterClass + "' specified for ScaComposite bean "
                    + "does not implement the ScaApapter interface"
            );
        }
        try {
            this.scaAdapter = (ScaAdapter) adapterClass.newInstance();
        } catch (Exception ex) {
            // many exceptions may be thrown by the above, we treat them all
            // the same
            throw new IllegalStateException("Unable to create instance of ScaAdapter class '"
                + adapterClass.getName() + "'", ex);
        }
    }

    public ScaAdapter getScaAdapter() {
        return this.scaAdapter;
    }

    /* (non-Javadoc)
      * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
      */
    public void afterPropertiesSet() throws Exception {
        if (this.component == null) {
            throw new IllegalArgumentException("Required property moduleId was not set");
        }
    }


}
