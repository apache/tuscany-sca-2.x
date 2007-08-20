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

package org.apache.tuscany.sca.contribution.services;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
//FIXME Why is this test case all commented out? 
public class ContributionMetadataDocumentProcessorTestCaseFIXME extends TestCase {

//    private static final String VALID_XML =
//        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
//            + "<contribution xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:ns=\"http://ns\">"
//            + "<deployable composite=\"ns:Composite1\"/>"
//            + "<deployable composite=\"ns:Composite2\"/>"
//            + "<import namespace=\"http://ns2\" location=\"sca://contributions/002/\"/>"
//            + "<export namespace=\"http://ns1\"/>"
//            + "</contribution>";
//
//    private static final String INVALID_XML =
//        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
//            + "<contribution xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:ns=\"http://ns\">"
//            + "<deployable composite=\"ns:Composite1\"/>"
//            + "<deployable composite=\"ns3:Composite1\"/>"
//            + "<import namespace=\"http://ns2\" location=\"sca://contributions/002/\"/>"
//            + "<export namespace=\"http://ns1\"/>"
//            + "</contribution>";

//    private XMLInputFactory xmlFactory;

    @Override
    protected void setUp() throws Exception {
//        xmlFactory = XMLInputFactory.newInstance();
    }

    public void testLoad() throws Exception {
//        XMLStreamReader reader = xmlFactory.createXMLStreamReader(new StringReader(VALID_XML));
//
//        ContributionFactory factory = new ContributionFactoryImpl();
//        ContributionMetadataLoaderImpl loader = 
//            new ContributionMetadataLoaderImpl(new DefaultAssemblyFactory(), factory);
//        Contribution contribution = factory.createContribution();
//        contribution.setModelResolver(new ModelResolverImpl(getClass().getClassLoader()));
//        loader.load(contribution, reader);
//        assertNotNull(contribution);
//        assertEquals(1, contribution.getImports().size());
//        assertEquals(1, contribution.getExports().size());
//        assertEquals(2, contribution.getDeployables().size());
    }

    public void testLoadInvalid() throws Exception {
//        XMLStreamReader reader = xmlFactory.createXMLStreamReader(new StringReader(INVALID_XML));
//        ContributionFactory factory = new ContributionFactoryImpl();
//        ContributionMetadataLoaderImpl loader = 
//            new ContributionMetadataLoaderImpl(new DefaultAssemblyFactory(), factory);
//        Contribution contribution = factory.createContribution();
//        contribution.setModelResolver(new ModelResolverImpl(getClass().getClassLoader()));
//        try {
//            loader.load(contribution, reader);
//            fail("InvalidException should have been thrown");
//        } catch (InvalidValueException e) {
//            assertTrue(true);
//        }
    }    
}
