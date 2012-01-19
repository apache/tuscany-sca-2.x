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

package datatypes;

import java.util.List;
import javax.jws.WebParam;
import javax.jws.soap.SOAPBinding;

import org.oasisopen.sca.ServiceReference;
import org.oasisopen.sca.annotation.Remotable;

import commonj.sdo.DataObject;
import other.OtherPojo;

@Remotable
public interface DataTypes {

    void testSimpleInt(int simple);

    void testSimpleArrayInt(int[] simple);

    void testSimpleMultiArrayInt(int[][] simple);

    void testSimpleMulti3ArrayInt(int[][][] simple);

    void testList(List any);

    void testSimpleListString(List<String> simple);

    List<String> testReturnSimpleListString();

    void testListByteArray(List<byte[]> byteArrayList);

    void testListWildcard(List<?> wild);

    void testComplex(ComplexNumber complex);

    void testOtherPackage(OtherPojo pojo);

    void testByteArray(byte[] byteArray);

    void testBaseExtension(ExtClass ext);

    void testServiceReference(ServiceReference ref);

    void testException() throws Exception;

    DataObject testDynamicSDO();

    void testWebParamSDO(@WebParam(name="foo") DataObject myObject);

    void testWebParamSDOArray(@WebParam(name="foo") DataObject[] myArray);
/*

    @SOAPBinding(parameterStyle=SOAPBinding.ParameterStyle.BARE)
    void testWebParamBare(@WebParam(name="simpleInt") int simple);

    @SOAPBinding(parameterStyle=SOAPBinding.ParameterStyle.BARE)
    void testWebParamBareArray(@WebParam(name="arrayInt") int[] array);
*/
}
