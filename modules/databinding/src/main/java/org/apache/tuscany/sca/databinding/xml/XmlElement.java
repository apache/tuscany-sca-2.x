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

package org.apache.tuscany.sca.databinding.xml;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * @version $Rev$ $Date$
 */
public interface XmlElement {
    /**
     * Returns the child <code>XmlTreeElement</code> at index 
     * <code>childIndex</code>.
     */
    // XmlElement getChildAt(int childIndex);

    /**
     * Returns the number of children <code>XmlTreeElement</code>s the receiver
     * contains.
     */
    // int getChildCount();

    /**
     * Returns the parent <code>XmlTreeElement</code> of the receiver.
     */
    // XmlElement getParent();

    /**
     * Returns the index of <code>node</code> in the receivers children.
     * If the receiver does not contain <code>node</code>, -1 will be
     * returned.
     */
    // int getIndex(XmlElement node);

    /**
     * Returns true if the receiver is a leaf.
     */
    boolean isLeaf();

    /**
     * Returns the children of the receiver as an <code>Iterator</code>.
     */
    Iterator<XmlElement> children();

    /**
     * Returns the attributes of the element as an <code>Iterator</code>
     * @return
     */
    List<XmlAttribute> attributes();
    
    List<QName> namespaces();

    /**
     * Return the QName of the element
     * @return
     */
    QName getName();

    /**
     * Return the text value of the leaf element
     * @return
     */
    String getValue();
}
