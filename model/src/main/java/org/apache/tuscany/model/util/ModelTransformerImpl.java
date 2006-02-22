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
package org.apache.tuscany.model.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author jsdelfino
 *
 * Base implementation for a model transformer.
 * Invokes a model content handler to perform the actual transformation.
 */
public class ModelTransformerImpl implements ModelTransformer {

	/**
	 * Uses a ModelContentHandler to transform a model.
	 */
	public List transform(Iterator iterator, ModelContentHandler handler) {

		// Pass 1: visit the source model and create target model objects
		List linkers=new ArrayList();
		List contents=transformPass1(iterator, handler, linkers);

		// Pass 2: resolve the links between the target model objects
		transformPass2(linkers);

		// Return the target model contents
		return contents;
	}

	/**
	 * Uses a ModelContentHandler to transform a model.
	 */
	public List transformPass1(Iterator iterator, final ModelContentHandler handler, List deferredHandlers, Map targets, List contents) {

		// Initialize the handler
		handler.setTargets(targets);
		handler.setLinkers(deferredHandlers);
		handler.setContents(contents);

		// This runnable will invoke endModel and clean up the handler
		Runnable cleanup=new Runnable() {
			public void run() {

				// Cleanup
				handler.endModel();

				handler.setTargets(null);
				handler.setLinkers(null);
				handler.setContents(null);
			}
		};

		// Run the handler
		try {
			handler.startModel();

			// Pass 1: compile, visit the source model and create target model objects
			for (; iterator.hasNext(); ) {
				Object source=iterator.next();
				Object target=handler.doSwitch(source);

				// Record source to target associations
				targets.put(source,target);
			}

			// Add the cleanup runnable
			deferredHandlers.add(cleanup);

		} catch (Exception e) {

			// An exception occurred, run the cleanup now
			cleanup.run();
			throw new RuntimeException(e);
		}

		// return the target model contents
		// note that this list may actually be populated in pass2
		return contents;
	}

	/**
	 * Uses a ModelContentHandler to transform a model.
	 */
	public List transformPass1(Iterator iterator, final ModelContentHandler handler, List deferredHandlers) {
		Map targets=new HashMap();
		List contents=new ArrayList();
		return transformPass1(iterator,handler,deferredHandlers,targets,contents);
	}

	/**
	 * Uses a ModelContentHandler to transform a model.
	 */
	public void transformPass2(List deferredHandlers) {

		// Pass 2: link, resolve the links between the target model objects
		for (Iterator i=deferredHandlers.iterator(); i.hasNext(); ) {
			Runnable runnable=(Runnable)i.next();
			runnable.run();
		}

	}

}
