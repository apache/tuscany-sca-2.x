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
package src.test.java.org.apache.tuscany.sca.implementation.data;

import java.io.FileInputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import junit.framework.TestCase;
import org.apache.tuscany.sca.databinding.xml.XMLStreamReader2String;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.implementation.data.DATACollection;

public class DATACollectionTestCase extends TestCase {

    private SCADomain scaDomain;
    private DATACollection dataService;

    /**
     * @throws java.lang.Exception
     */
    @Override
    protected void setUp() throws Exception {
        scaDomain = SCADomain.newInstance("data.composite");
        dataService = scaDomain.getService(DATACollection.class, "DataComponent/COMPANY");
    }

    /**
     * @throws java.lang.Exception
     */
    @Override
    protected void tearDown() throws Exception {
        scaDomain.close();
    }

    public void testInsert() throws Exception {
        System.out.println(">testInsert");

        //Read and process the XML file
        FileInputStream fileInputStream = new FileInputStream("src/test/resources/insert.xml");
        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(fileInputStream);

        String result = dataService.post(null, reader);
        assertEquals(result, "2");

        System.out.println("Number of rows inserted: " + result);

        reader.close();
    }

    public void testGet() throws Exception {

        System.out.println(">testGet");

        XMLStreamReader reader = dataService.get(null);
        assertNotNull(reader);
        String xml = new XMLStreamReader2String().transform(reader, null);
        System.out.println(xml);
        reader.close();
    }

    public void testUpdate() throws Exception {

        System.out.println(">testUpdate");

        //Read and process the XML file
        FileInputStream fileInputStream = new FileInputStream("src/test/resources/update.xml");
        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(fileInputStream);

        dataService.put(null,reader);     

        reader.close();
    }

    public void testGetByID() throws Exception {
        System.out.println(">testGetByID");

        Integer companyID = new Integer(4);

        XMLStreamReader reader = dataService.get(companyID.toString());
        assertNotNull(reader);
        String xml = new XMLStreamReader2String().transform(reader, null);
        System.out.println(xml);
        reader.close();
    }

    public void testDeleteByID() throws Exception {
        System.out.println(">testDeleteByID");

        Integer companyID = new Integer(4);
        dataService.delete(companyID.toString());
    }

    public void testDelete() throws Exception {
        System.out.println(">testDelete");

        dataService.delete(null);

        System.out.println("recreating database...");
        //Helper.createDB();
        System.out.println("done!");
    }
}
