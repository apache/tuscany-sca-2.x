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

package org.apache.tuscany.sca.assembly.xml;

import static junit.framework.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.junit.Before;
import org.junit.Test;

/**
 * Test writing SCA XML assemblies.
 * 
 * TUSCANY-2662
 * 
 * @version $Rev$ $Date$
 */
public class MultiplicityReadWriteTestCase {
    private XMLInputFactory inputFactory;
    private XMLOutputFactory outputFactory;
    private ExtensibleStAXArtifactProcessor staxProcessor;


    @Before
    public void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        inputFactory = XMLInputFactory.newInstance();
        outputFactory = XMLOutputFactory.newInstance();
        StAXArtifactProcessorExtensionPoint staxProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, outputFactory, null);
    }


    @Test
    public void testReadWriteComposite() throws Exception {
        InputStream is = getClass().getResourceAsStream("Multiplicity.composite");
        Composite composite = staxProcessor.read(is, Composite.class);
        
        verifyComposite(composite);
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        staxProcessor.write(composite, bos);
        bos.close();
        
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        composite = staxProcessor.read(bis, Composite.class);
        
        verifyComposite(composite);
        
    }

    
    private void verifyComposite(Composite composite) {
        assertEquals(composite.getComponents().get(0).getReferences().get(0).getMultiplicity(), Multiplicity.ZERO_N);
        assertEquals(composite.getReferences().get(0).getMultiplicity(), Multiplicity.ONE_N);
    }

}
