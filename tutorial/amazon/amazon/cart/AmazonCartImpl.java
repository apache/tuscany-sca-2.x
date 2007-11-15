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

import org.osoa.sca.annotations.Remotable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cart.amazon.*;


@Remotable
public class AmazonCartImpl {

	protected Map<String, Cart> cartsHash = new HashMap<String, Cart>();
		
	public CartCreateResponse CartCreate(CartCreate cartCreate){
	
		System.out.println("CartServiceID: " + this.toString());
		System.out.println("Entering cartCreate...");
		String userId = cartCreate.getAWSAccessKeyId();

		Cart cart = getCart(userId);
		if(cart != null){
			System.out.println("User " + cartCreate.getAWSAccessKeyId() + " has already created a cart with ID: " + cart.getCartId());
			return null;
		}
		cart = new Cart();		
		cart.setCartItems(new CartItems());		
		addCart(userId, cart);		
		
		System.out.println("Exiting cartCreate...");
		return null;
			
	}
	
	public CartAddResponse CartAdd(CartAdd cartAdd){
		
		String userId = cartAdd.getAWSAccessKeyId();
		
		Cart cart = getCart(userId);
		if(cart == null){
			System.out.println("User has not associated Cart yet...");
			return null;
		}
		
		List cartAddRequestList = cartAdd.getRequest();
		CartAddRequest car = (CartAddRequest) cartAddRequestList.get(0);
		Items1 carItems = car.getItems();
		List itemList = carItems.getItem();
		Item1 item = (Item1) itemList.get(0); //Take only the first one, no iteration for now
		System.out.println("item.getASIN: " + item.getASIN());
		System.out.println("item.getQuantity(): " + item.getQuantity());
		CartItems cartItems = cart.getCartItems();
		List<CartItem> cartItemList = cartItems.getCartItem();
		cart.setCartItems(cartItems);
		return null;
	}
	
	public CartClearResponse CartClear(CartClear cartClear){
		String userId = cartClear.getAWSAccessKeyId();
		
		Cart cart = getCart(userId);
		if(cart == null){
			System.out.println("User has not associated Cart yet...");
			return null;
		}
		
		cart.setCartItems(new CartItems());
		return null;
	}
	
	public CartGetResponse CartGet(CartGet cartGet){
		return null;
	}

	
	private Cart getCart(String userId){
		Cart cart = null;
		cart = this.cartsHash.get(userId);
		return cart;
	}
	
	private void addCart(String userId, Cart cart){
		this.cartsHash.put(userId, cart);
	}
}
