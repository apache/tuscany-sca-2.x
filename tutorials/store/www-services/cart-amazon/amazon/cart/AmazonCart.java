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
package amazon.cart;

import org.apache.tuscany.sca.databinding.annotation.DataBinding;
import org.osoa.sca.annotations.Remotable;

import com.cart.amazon.CartAdd;
import com.cart.amazon.CartAddResponse;
import com.cart.amazon.CartClear;
import com.cart.amazon.CartClearResponse;
import com.cart.amazon.CartCreate;
import com.cart.amazon.CartCreateResponse;
import com.cart.amazon.CartGet;
import com.cart.amazon.CartGetResponse;

@Remotable
@DataBinding(value="commonj.sdo.DataObject", wrapped=true)
public interface AmazonCart {

    public CartCreateResponse CartCreate(CartCreate cartCreate);

    public CartAddResponse CartAdd(CartAdd cartAdd);

    public CartClearResponse CartClear(CartClear cartClear);

    public CartGetResponse CartGet(CartGet cartGet);
}
