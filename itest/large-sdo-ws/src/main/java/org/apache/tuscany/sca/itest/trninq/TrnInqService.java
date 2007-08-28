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
package org.apache.tuscany.sca.itest.trninq;

import java.rmi.RemoteException;
import java.util.List;

import org.ifxforum.xsd._1.AdditionalStatus_Type;
import org.ifxforum.xsd._1.BankAcctTrnRec_Type;
import org.ifxforum.xsd._1.BillerId_Type;
import org.ifxforum.xsd._1.BillerPayee_Type;
import org.ifxforum.xsd._1.DepAcctTrnInqRq_Type;
import org.ifxforum.xsd._1.DepAcctTrnInqRs_Type;
import org.ifxforum.xsd._1.DepAcctTrnRec_Type;
import org.ifxforum.xsd._1._1Factory;

/**
 * @version $Rev$ $Date$
 */
public class TrnInqService implements TrninqInterface {

    public DepAcctTrnInqRs_Type DepAcctTrnInq(DepAcctTrnInqRq_Type req) throws RemoteException {

        DepAcctTrnInqRs_Type response = _1Factory.INSTANCE.createDepAcctTrnInqRs_Type();

        response.setStatus(_1Factory.INSTANCE.createStatus_Type());
        response.getStatus().setStatusCode(1);
        response.getStatus().setStatusDesc("the description associated with the code 1");
        AdditionalStatus_Type addStatus = _1Factory.INSTANCE.createAdditionalStatus_Type();
        addStatus.setServerStatusCode("STATUS_OK");
        addStatus.setSeverity("SEVERITY_WARNING");
        response.getStatus().getAdditionalStatus().add(addStatus);

        response.setDepAcctId(_1Factory.INSTANCE.createDepAcctId_Type());
        response.getDepAcctId().setBankInfo(_1Factory.INSTANCE.createBankInfo_Type());
        response.getDepAcctId().getBankInfo().setBranchId("Concord_1");
        response.getDepAcctId().getBankInfo().setBankId("Dufferin hill");
        response.getDepAcctId().getBankInfo().setBankIdType("Concord branch");
        response.getDepAcctId().getBankInfo().setBranchName("A Happy branch");
        response.getDepAcctId().getBankInfo().setCity("Toronto");

        response.getDepAcctId().getBankInfo().setCountry("Canada");
        response.getDepAcctId().getBankInfo().setName("Info for A Happy branch");
        response.getDepAcctId().getBankInfo().setPostalCode("L1LL1L");
        response.getDepAcctId().getBankInfo().setStateProv("ON");

        List records = response.getDepAcctTrnRec();
        // Record1
        DepAcctTrnRec_Type trnRec1 = null;
        BankAcctTrnRec_Type baTrnRec1 = null;
        long maxRec = req.getTrnCountLimit().getCount();
        for (int i = 0; i < maxRec; i++) {
            trnRec1 = _1Factory.INSTANCE.createDepAcctTrnRec_Type();
            baTrnRec1 = _1Factory.INSTANCE.createBankAcctTrnRec_Type();

            trnRec1.setBankAcctTrnRec(baTrnRec1);
            trnRec1.getBankAcctTrnRec().setCSPRefId("CSPRefId1");
            trnRec1.getBankAcctTrnRec().setPostedDt("1");
            trnRec1.getBankAcctTrnRec().setOrigDt("1");
            trnRec1.getBankAcctTrnRec().setBillRefInfo("aaaa");

            trnRec1.getBankAcctTrnRec().setTrnType("1");

            trnRec1.getBankAcctTrnRec().setCurAmt(_1Factory.INSTANCE.createCurrencyAmount());
            trnRec1.getBankAcctTrnRec().getCurAmt().setAmt(new java.math.BigDecimal(2000.35));
            trnRec1.getBankAcctTrnRec().getCurAmt().setCurCode("100");

            trnRec1.setStmtRunningBal(_1Factory.INSTANCE.createCurrencyAmount());
            trnRec1.getStmtRunningBal().setAmt(new java.math.BigDecimal("1345.55"));

            trnRec1.getBankAcctTrnRec().setCustPayeeInfo(_1Factory.INSTANCE.createCustPayeeInfo_Type());
            trnRec1.getBankAcctTrnRec().getCustPayeeInfo().setBillingAcct("000601832");
            trnRec1.getBankAcctTrnRec().getCustPayeeInfo().setAcctPayAcctId("12");
            trnRec1.getBankAcctTrnRec().getCustPayeeInfo().setName("John Doe");
            trnRec1.getBankAcctTrnRec().getCustPayeeInfo().setNickname("Jody");

            trnRec1.getBankAcctTrnRec().getCustPayeeInfo().setBillerPayee(_1Factory.INSTANCE.createBillerPayee_Type());
            BillerPayee_Type BillerP = trnRec1.getBankAcctTrnRec().getCustPayeeInfo().getBillerPayee();

            BillerP.setBillerId(_1Factory.INSTANCE.createBillerId_Type());
            BillerId_Type BId = BillerP.getBillerId();

            BId.setBillerNum("123");
            BId.setSPName("SomeName");

            trnRec1.setAvailDt("sss");
            trnRec1.setPmtId("SomeID");
            trnRec1.setXferId("SomeOtherID");

            records.add(trnRec1);
        }

        return response;
    }

}
