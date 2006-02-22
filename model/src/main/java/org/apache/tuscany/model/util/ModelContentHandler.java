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

import java.util.List;
import java.util.Map;

/**
 * A model content handler. This mimics a SAX content handler for models and
 * receives notification of the content of a model. A model content handler is
 * responsible for handling model content notifications. As part of the handling
 * the handler can produce new contents.
 */
public interface ModelContentHandler {

	/**
	 * Starts handling of a model.
	 */
	public void startModel();

	/**
	 * Ends handling of a model.
	 */
	public void endModel();

	/**
	 * Sets the contents list, where the content handler should store
	 * the contents that it produces.
	 * @param contents The contents to set
	 */
	public void setContents(List contents);

	/**
	 * Sets the linkers list. A content handler can add linker objects to this
	 * list. Linker objects must implement java.lang.Runnable. They are
	 * run as part of the endModel notification processing.
	 * Typically linker objects are used to resolve model forward references
	 * or establish model relationships after the model content has been
	 * handled.
	 * @param linkers The linkers to set
	 */
	public void setLinkers(List linkers);

	/**
	 * Sets the targets map. This map keeps track of the objects returned by
	 * the content handler for each object passed to its doSwitch method.
	 * @param targets The targets to set
	 */
	public void setTargets(Map targets);

	/**
	 * This method dispatches handling of the given object (XYZ for example) to a
	  * corresponding "caseXYZ()" method on the content handler.
	 */
	public Object doSwitch(Object object);

}
