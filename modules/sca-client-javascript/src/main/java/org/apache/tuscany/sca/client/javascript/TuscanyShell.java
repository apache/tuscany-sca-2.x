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

package org.apache.tuscany.sca.client.javascript;

/**
 * 
 * This class provides a scripting shell that can be used as a Tuscany client. This shell
 * extends from the Rhino JavaScript Engine's shell and provides capablities for working 
 * with the Tuscany Runtime.
 * 
 * <p> This shell initializt the Tuscany Engine, using the input parameters that point to 
 * an SCA jar or war or a directory with an sca.module file. The Shell will load that module
 * and then all the components and external services in the sca.module become available in the 
 * Shell, so that you can invoke them dynamically
 *
 */
public class TuscanyShell {

	/**
     * Main entry point into the shell. This method invokes the 
     * <code>ScriptEngine</code>.
     *
     */
    public static void main(String args[]) {
    	ScriptEngine engine = new ScriptEngine(args);
    	new Thread(engine).start();
    }

}
