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
package org.apache.tuscany.tomcat;

import java.beans.PropertyChangeListener;

import org.apache.catalina.Container;
import org.apache.catalina.Loader;

/**
 * Implementation of a TomcatLoader that allows privileged servlets from the container
 * classloader to be loaded into an unprivileged application. This allows the Tuscany
 * integration code to add servlets to the application, for example, to handle web
 * services requests.
 *
 * @version $Rev$ $Date$
 */
public class ContainerLoader implements Loader {
    private static final String INFO = ContainerLoader.class.getName() + "/SNAPSHOT";
    private final ClassLoader cl;
    private Container container;

    /**
     * Constructor specifying the classloader to be used.
     *
     * @param cl the classloader this Loader wraps, typically the container classloader
     */
    public ContainerLoader(ClassLoader cl) {
        this.cl = cl;
    }

    public void backgroundProcess() {
    }

    public ClassLoader getClassLoader() {
        return cl;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public boolean getDelegate() {
        return false;
    }

    public void setDelegate(boolean delegate) {
        throw new UnsupportedOperationException();
    }

    public String getInfo() {
        return INFO;
    }

    public boolean getReloadable() {
        return false;
    }

    public void setReloadable(boolean reloadable) {
        throw new UnsupportedOperationException();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        throw new UnsupportedOperationException();
    }

    public void addRepository(String repository) {
        throw new UnsupportedOperationException();
    }

    public String[] findRepositories() {
        throw new UnsupportedOperationException();
    }

    public boolean modified() {
        throw new UnsupportedOperationException();
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        throw new UnsupportedOperationException();
    }
}
