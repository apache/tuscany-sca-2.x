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
package org.apache.tuscany.common.resource.loader;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

/**
 * This interface represents a resource loader.
 */
public interface ResourceLoader {

    /**
     * Returns the parent resource loaders.
     * @return
     */
    List getParents();

    /**
     * Loads a class.
     * @param name
     * @return
     * @throws ClassNotFoundException
     */
    Class loadClass(String name) throws ClassNotFoundException;

    /**
     * Find resources with the given name.
     * @param name
     * @return
     * @throws IOException
     */
    Iterator getResources(String name) throws IOException;

    /**
     * Finds all the resources with the given name.
     * @param name
     * @return
     * @throws IOException
     */
    Iterator getAllResources(String name) throws IOException;

    /**
     * Finds the first resource with the given name.
     * @param name
     * @return
     * @throws IOException
     */
    URL getResource(String name) throws IOException;
	
}
