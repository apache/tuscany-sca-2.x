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

package org.apache.tuscany.contribution.services;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.contribution.Contribution;
import org.apache.tuscany.contribution.service.impl.ContributionMetadataLoaderImpl;
import org.apache.tuscany.contribution.service.impl.InvalidValueException;

/**
 * @version $Rev$ $Date$
 */
public class ContributionMetadataLoaderTestCase extends TestCase {

    private static final String VALID_XML =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<contribution xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:ns=\"http://ns\">"
            + "<deployable composite=\"ns:Composite1\"/>"
            + "<deployable composite=\"ns:Composite2\"/>"
            + "<import namespace=\"http://ns2\" location=\"sca://contributions/002/\"/>"
            + "<export namespace=\"http://ns1\"/>"
            + "</contribution>";

    private static final String INVALID_XML =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<contribution xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:ns=\"http://ns\">"
            + "<deployable composite=\"ns:Composite1\"/>"
            + "<deployable composite=\"ns3:Composite1\"/>"
            + "<import namespace=\"http://ns2\" location=\"sca://contributions/002/\"/>"
            + "<export namespace=\"http://ns1\"/>"
            + "</contribution>";

    private XMLInputFactory factory;

    protected void setUp() throws Exception {
        super.setUp();
        factory = XMLInputFactory.newInstance();
    }

    public void testLoad() throws Exception {
        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(VALID_XML));
        ContributionMetadataLoaderImpl loader = new ContributionMetadataLoaderImpl();
        Contribution contribution = loader.load(reader);
        assertNotNull(contribution);
        assertEquals(1, contribution.getImports().size());
        assertEquals(1, contribution.getExports().size());
        assertEquals(2, contribution.getDeployables().size());
    }

    public void testLoadInvalid() throws Exception {
        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(INVALID_XML));
        ContributionMetadataLoaderImpl loader = new ContributionMetadataLoaderImpl();
        try {
            loader.load(reader);
            fail("InvalidException should have been thrown");
        } catch (InvalidValueException e) {
            assertTrue(true);
        }
    }    
}
