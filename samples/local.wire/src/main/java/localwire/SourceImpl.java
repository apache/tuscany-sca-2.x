/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package localwire;

import org.osoa.sca.annotations.Scope;

/**
 * The component at the source end of the wire.
 *
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class SourceImpl implements Source {
    private Target target;

    public SourceImpl() {
    }

    /**
     * The reference to the other component. Default introspection will make this a reference.
     *
     * @param target the other component
     */
    public void setTarget(Target target) {
        this.target = target;
    }

    public String invoke(String msg) {
        return target.echo(msg);
    }
}
