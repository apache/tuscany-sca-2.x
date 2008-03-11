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

package calculator.policy;

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.util.PolicyHandler;
import org.junit.Assert;

/**
 * @version $Rev$ $Date$
 */
public class TestImplPolicyHandler implements PolicyHandler {
    private PolicySet applicablePolicySet = null;

    public void afterInvoke(Object... arg0) {
    }

    public void beforeInvoke(Object... context) {
        for ( Object contextObj : context) {
            if ( contextObj instanceof Operation ) {
                Operation op = (Operation)contextObj;
                //System.out.println(" *TestImplPolicyHandler* " + op.getName() + " ** " + applicablePolicySet);
                if ( op.getName().equals("add") ) {
                    boolean match = applicablePolicySet.getName().getLocalPart().equals("TestPolicySet_1_implementation") ||
                        applicablePolicySet.getName().getLocalPart().equals("TestPolicySet_2_implementation") ||
                        applicablePolicySet.getName().getLocalPart().equals("TestPolicySet_1_Qualified_implementation") ||
                        applicablePolicySet.getName().getLocalPart().equals("TestPolicySet_3_implementation") ;
                    Assert.assertTrue(match); 
                } else if ( op.getName().equals("subtract") ) {
                    boolean match = applicablePolicySet.getName().getLocalPart().equals("TestPolicySet_1_Qualified_implementation") ||
                    applicablePolicySet.getName().getLocalPart().equals("TestPolicySet_1_implementation");
                    Assert.assertTrue(match); 
                } else if ( op.getName().equals("divide")) {
                    Assert.assertEquals(applicablePolicySet.getName().getLocalPart(), 
                                        "TestPolicySet_1_implementation");
                } else if ( op.getName().equals("multiply") ) {
                    boolean match = applicablePolicySet.getName().getLocalPart().equals("TestPolicySet_5_implementation") ||
                    applicablePolicySet.getName().getLocalPart().equals("TestPolicySet_1_implementation");
                    Assert.assertTrue(match);
                }    
                else {
                    Assert.fail();
                }
                //System.out.println(" *TestImplPolicyHandler* " + op.getName() + " ** " + applicablePolicySet);
            } else if ( contextObj instanceof Message ) {
                Message msg = (Message)contextObj;
                System.out.println(" *TestImplPolicyHandler* " + msg.getOperation().getName() + " ** " + applicablePolicySet);
            }
        }
    }

    public void cleanUp(Object... arg0) {
    }

    public PolicySet getApplicablePolicySet() {
        return applicablePolicySet;
    }

    public void setApplicablePolicySet(PolicySet arg0) {
        this.applicablePolicySet = arg0;
    }

    public void setUp(Object... arg0) {
    }
}
