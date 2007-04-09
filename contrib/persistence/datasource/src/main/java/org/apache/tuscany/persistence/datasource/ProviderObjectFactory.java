/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tuscany.persistence.datasource;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.sql.DataSource;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;

/**
 * Responsible for instantiating a DataSource provider class and initializing the actual <code>DataSource</code> . It
 * must have a no-args constructor and may optionally implement {@link DataSourceProvider}. If it does not implement
 * <code>DataSourceProvider</code>, then the provider class must implement the <code>DataSource<code> interface. A
 * collection of {@link Injector}s is used to inject the provider instance with configuration parameters.
 *
 * @version $Rev$ $Date$
 */
public class ProviderObjectFactory implements ObjectFactory<DataSource> {
    private Class<?> providerClass;
    private List<Injector> injectors;

    public ProviderObjectFactory(Class<?> providerClass, List<Injector> injectors) {
        assert providerClass != null;
        assert injectors != null;
        this.providerClass = providerClass;
        this.injectors = injectors;
    }

    public DataSource getInstance() throws ObjectCreationException {
        try {
            Object instance = providerClass.newInstance();
            for (Injector injector : injectors) {
                injector.inject(instance);
            }
            if (instance instanceof DataSourceProvider) {
                DataSourceProvider dataSourceProvider = (DataSourceProvider) instance;
                try {
                    dataSourceProvider.init();
                    return dataSourceProvider.getDataSource();
                } catch (ProviderException e) {
                    throw new ObjectCreationException(e);
                }
            } else {
                return (DataSource) instance;
            }
        } catch (InstantiationException e) {
            throw new ObjectCreationException(e);
        } catch (IllegalAccessException e) {
            throw new ObjectCreationException(e);
        } catch (InvocationTargetException e) {
            throw new ObjectCreationException(e);
        }
    }

}
