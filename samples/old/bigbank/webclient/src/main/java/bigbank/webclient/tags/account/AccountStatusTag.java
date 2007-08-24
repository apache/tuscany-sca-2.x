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
package bigbank.webclient.tags.account;

import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

import bigbank.webclient.services.profile.ProfileService;

import com.bigbank.account.AccountReport;
import com.bigbank.account.AccountService;

/**
 * Retrieves and iterates over account summary information for the current profile by accessing the remotable account service component
 */

public class AccountStatusTag extends TagSupport {

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public AccountStatusTag() {
        super();
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    private String mAccountService;

    public String getAccountService() {
        return mAccountService;
    }

    public void setAccountService(String pAccountService) {
        mAccountService = pAccountService;
    }

    private String mProfileService;

    public String getProfileService() {
        return mProfileService;
    }

    public void setProfileService(String pProfileService) {
        mProfileService = pProfileService;
    }

    private String mId;

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public void setId(String pId) {
        mId = pId;
    }

    private Iterator mIterator;

    @Override
    public int doStartTag() throws JspException {
        CompositeContext moduleContext = CurrentCompositeContext.getContext();

        ProfileService profile = moduleContext.locateService(ProfileService.class, mProfileService);

        if (profile == null) {
            throw new JspException("Profile [" + mProfileService + "] not found in current module context");
        }

        AccountService service = (AccountService) moduleContext.locateService(AccountService.class, mAccountService);

        if (service == null) {
            throw new JspException("Service [" + mAccountService + "] not found in current module context");
        }
        List summaries;
        try {
            AccountReport accountReport = service.getAccountReport(profile.getId());
            pageContext.setAttribute("StockSummaries", accountReport.getStockSummaries());
            summaries = accountReport.getAccountSummaries();
        } catch (Exception e) {
            throw new JspException(e);
        }
        mIterator = summaries.iterator();
        if (mIterator.hasNext()) {
            pageContext.setAttribute(mId, mIterator.next());
            return EVAL_BODY_INCLUDE;
        } else {
            return SKIP_BODY;
        }
    }

    @Override
    public int doAfterBody() {
        if (mIterator.hasNext()) {
            pageContext.setAttribute(mId, mIterator.next());
            return EVAL_BODY_AGAIN;
        } else {
            return SKIP_BODY;
        }
    }

    @Override
    public void release() {
        super.release();
        mId = null;
        mIterator = null;
    }
}