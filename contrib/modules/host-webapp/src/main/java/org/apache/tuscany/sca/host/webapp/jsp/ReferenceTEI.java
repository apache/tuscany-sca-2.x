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

package org.apache.tuscany.sca.host.webapp.jsp;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * TagExtraInfo class for the SCA reference tags
 * <sca:reference name="service" type="test.MyService" scope="1" />
 *
 * @version $Rev$ $Date$
 */
public class ReferenceTEI extends TagExtraInfo {

    @Override
    public VariableInfo[] getVariableInfo(TagData data) {
        VariableInfo info1
           = new VariableInfo(
              data.getAttributeString("name"),
              data.getAttributeString("type"),
              true,
              VariableInfo.AT_END);
        VariableInfo[] info = { info1 } ;
        return info;
     }
}
