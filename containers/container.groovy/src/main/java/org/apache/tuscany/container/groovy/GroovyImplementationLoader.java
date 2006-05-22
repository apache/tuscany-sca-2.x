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
package org.apache.tuscany.container.groovy;

import org.osoa.sca.annotations.Scope;

/**
 * Groovy implementation loader.
 *
 */
@Scope("MODULE")
public class GroovyImplementationLoader{// extends AbstractImplementationLoader<GroovyImplementation> {
//
//	// Qualified name for the Groovy implementation.
//	public static final QName NAME = new QName("http://org.apache.tuscany/xmlns/groovy/0.9", "implementation.groovy");
//
//    /**
//     * Lifecycle method registers the implementation loader.
//     *
//     */
//    @Init(eager = true)
//    public void start() {
//        registry.registerLoader(NAME, this);
//    }
//
//    /**
//     * Lifecycle method deregisters the implementation loader.
//     *
//     */
//    @Destroy
//    public void stop() {
//        registry.unregisterLoader(NAME, this);
//    }
//
//	/**
//	 * Required to be implemented by the concrete classes.
//	 * @return Implementation object.
//	 */
//	protected GroovyImplementation getAssemblyObject(XMLStreamReader reader, LoaderContext loaderContext) {
//
//		GroovyImplementation groovyImplementation = new GroovyImplementation();
//		String script = reader.getAttributeValue(null, "script");
//
//		groovyImplementation.setScript(script);
//		groovyImplementation.setResourceLoader(loaderContext.getResourceLoader());
//
//		return groovyImplementation;
//
//	}
//
//    /**
//     * Gets the side file.
//     *
//     * @param reader Reader for the module file.
//     * @param loaderContext Loader context.
//     * @return Side file Url.
//     * @throws MissingResourceException
//     */
//	protected URL getSideFile(XMLStreamReader reader, LoaderContext loaderContext)
//	throws MissingResourceException {
//
//		String script = reader.getAttributeValue(null, "script");
//		String sidefile = script.substring(0, script.lastIndexOf('.')) + ".componentType";
//        URL componentTypeFile = loaderContext.getResourceLoader().getResource(sidefile);
//        if (componentTypeFile == null) {
//            throw new MissingResourceException(sidefile);
//        }
//		return componentTypeFile;
//
//	}

}
