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
package org.apache.tuscany.spi;

/**
 * An evaluated name consisting of a part/port pair. In the runtime, a part generally 'contains' or 'provides'
 * ports such as a component/service point or a component/reference pair.
 *
 * @version $Rev$ $Date$
 */
public class QualifiedName {

    private String qName;

    private String partName;

    private String portName;

    public static final String NAME_SEPARATOR = "/";

    /**
     * Constructs a new qualified name in the form of part/port where part is the parent context and port
     * represents a child, which is either a service in the case of an atomic context or a contained context
     * in the case of a composite.
     *
     * @throws InvalidNameException if the name is in an invalid format
     */
    public QualifiedName(String qualifiedName) throws InvalidNameException {
        if (qualifiedName == null) {
            return;
        }
        int pos = qualifiedName.indexOf(QualifiedName.NAME_SEPARATOR);
        switch (pos) {
            case -1:
                partName = qualifiedName;
                break;
            case 0:
                throw new InvalidNameException(qualifiedName);
            default:
                partName = qualifiedName.substring(0, pos);
                portName = qualifiedName.substring(pos + 1);
                break;
        }
        qName = qualifiedName;
    }

    /**
     * Returns the parsed part name
     */
    public String getPartName() {
        return partName;
    }

    /**
     * Returns the parsed port name if the original is of the compound for part/port
     */
    public String getPortName() {
        return portName;
    }

    /**
     * Returns the full part/port name pair
     */
    public String getQualifiedName() {
        return qName;
    }

    public String toString() {
        return qName;
    }
}
