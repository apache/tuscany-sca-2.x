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

package sample.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static sample.impl.TestUtil.here;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.DefaultContributionFactory;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test read/write Sample SCDL implementation elements.
 * 
 * @version $Rev$ $Date$
 */
@Ignore
public class ReadWriteTestCase {
    static XMLInputFactory xif;
    static XMLOutputFactory xof;
    static StAXArtifactProcessor<Object> xproc;
    static ProcessorContext ctx;

    @BeforeClass
    public static void setUp() throws Exception {
        final DefaultExtensionPointRegistry ep = new DefaultExtensionPointRegistry();
        final Contribution contrib = new DefaultContributionFactory().createContribution();
        contrib.setLocation(here());
        ctx = new ProcessorContext(contrib, null);
        xif = XMLInputFactory.newInstance();
        xof = XMLOutputFactory.newInstance();
        final StAXArtifactProcessorExtensionPoint xpep = new DefaultStAXArtifactProcessorExtensionPoint(ep);
        xproc = new ExtensibleStAXArtifactProcessor(xpep, xif, xof);
    }

    @Test
    public void testRead() throws Exception {
        final InputStream is = getClass().getClassLoader().getResourceAsStream("test.composite");
        final Composite c = (Composite)xproc.read(xif.createXMLStreamReader(is), ctx);
        assertNotNull(c);
        assertEquals("sample.ClientTest", ((SampleImplementation)c.getComponents().get(0).getImplementation()).name);
    }

    @Test
    public void testReadWrite() throws Exception {
        final InputStream is = getClass().getClassLoader().getResourceAsStream("test.composite");
        final Composite c = (Composite)xproc.read(xif.createXMLStreamReader(is), ctx);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        xproc.write(c, xof.createXMLStreamWriter(bos), ctx);
        assertTrue(bos.toString().contains("class=\"sample.WelloTest\""));
    }
}
