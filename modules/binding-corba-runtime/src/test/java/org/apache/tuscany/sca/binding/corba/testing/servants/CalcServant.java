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

package org.apache.tuscany.sca.binding.corba.testing.servants;

import org.apache.tuscany.sca.binding.corba.testing.exceptions._CalcImplBase;
import org.apache.tuscany.sca.binding.corba.testing.exceptions.CalcPackage.Arguments;
import org.apache.tuscany.sca.binding.corba.testing.exceptions.CalcPackage.DivByZero;
import org.apache.tuscany.sca.binding.corba.testing.exceptions.CalcPackage.NotSupported;

public class CalcServant extends _CalcImplBase {

    private static final long serialVersionUID = 1L;

    public double div(double arg1, double arg2) throws DivByZero {
        if (arg2 == 0) {
            DivByZero exception = new DivByZero();
            exception.arguments = new Arguments(arg1, arg2);
            exception.info = "Error occured during div: div by zero";
            throw exception;
        } else {
            return arg1 / arg2;
        }
    }

    public double divForSmallArgs(double arg1, double arg2) throws DivByZero, NotSupported {
        if (arg1 > 100 || arg2 > 100) {
            NotSupported exception = new NotSupported();
            exception.info = "arg1: " + arg1 + ", arg2: " + arg2;
            throw exception;
        } else {
            return div(arg1, arg2);
        }
    }

}
