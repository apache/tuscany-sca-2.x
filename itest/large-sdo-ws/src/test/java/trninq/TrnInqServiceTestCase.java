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
package trninq;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.itest.trninq.TrninqInterface;
import org.ifxforum.xsd._1.DepAcctTrnInqRq_Type;
import org.ifxforum.xsd._1.DepAcctTrnInqRs_Type;
import org.ifxforum.xsd._1.TrnCountLimit_Type;
import org.ifxforum.xsd._1._1Factory;
import org.junit.Assert;

/**
 * This test is created to cover https://issues.apache.org/jira/browse/TUSCANY-1541
 * 
 * @version $Rev$ $Date$
 */
public class TrnInqServiceTestCase extends TestCase {

    private SCADomain scaDomain;
    private TrninqInterface trnInq;

    @Override
    protected void setUp() throws Exception {
        scaDomain = SCADomain.newInstance("trnInq.composite");
        trnInq = scaDomain.getService(TrninqInterface.class, "TransactionInquiryClientComponent");
    }

    @Override
    protected void tearDown() throws Exception {
        scaDomain.close();
    }

    public void testDepAcctTrnInq() throws IOException {
        DepAcctTrnInqRq_Type depAcctTrnInqRequest = _1Factory.INSTANCE.createDepAcctTrnInqRq_Type();
        TrnCountLimit_Type trnCountLimit = _1Factory.INSTANCE.createTrnCountLimit_Type();
        depAcctTrnInqRequest.setTrnCountLimit(trnCountLimit);
        depAcctTrnInqRequest.getTrnCountLimit().setCount(2);

        try {
            DepAcctTrnInqRs_Type depAcctTrnInqResponse = trnInq.DepAcctTrnInq(depAcctTrnInqRequest);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            Assert.fail("Should pass with MaxRec 2! \n" + sw);
            sw.close();
            pw.close();
        }

        depAcctTrnInqRequest.getTrnCountLimit().setCount(10);
        try {
            DepAcctTrnInqRs_Type depAcctTrnInqResponse = trnInq.DepAcctTrnInq(depAcctTrnInqRequest);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            Assert.fail("Should pass with MaxRec 10! \n" + sw);
            sw.close();
            pw.close();
        }
    }
}
