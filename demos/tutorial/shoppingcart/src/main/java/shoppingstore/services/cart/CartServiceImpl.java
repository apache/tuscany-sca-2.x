package shoppingstore.services.cart;

import java.util.HashMap;

import org.osoa.sca.annotations.Scope;

import com.amazon.webservices.awsecommerceservice._2007_05_14.Cart;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartAdd;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartAddRequest;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartAddResponse;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartClear;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartClearResponse;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartCreate;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartCreateResponse;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartGet;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartGetResponse;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartItem;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartItems;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartModify;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartModifyResponse;

@Scope("COMPOSITE")
public class CartServiceImpl implements CartService {
	
	private static long ID = 0;
	
	private HashMap<String, Cart> cartsHash = new HashMap<String, Cart>();
	
	public CartAddResponse CartAdd(CartAdd cartAdd) {
		System.out.println("Entering cartAdd...");
		System.out.println("CartServiceID: " + this.toString());
		CartAddResponse cartAddResponse = new CartAddResponse();
		
		CartAddRequest cartAddRequest = cartAdd.getRequest().get(0);
		
		//Cart cart = getCart(cartAddRequest.getCartId());
		Cart cart = getCart(cartAdd.getAWSAccessKeyId());
		if(cart == null){
			cartAddResponse.getCart().add(new Cart());
			return cartAddResponse;
		}
		
		CartItem cartItem = new CartItem();
		cartItem.setASIN(cartAddRequest.getItems().getItem().get(0).getASIN());
		cartItem.setQuantity(cartAddRequest.getItems().getItem().get(0).getQuantity().toString());
		cart.getCartItems().getCartItem().add(cartItem);
		cartAddResponse.getCart().add(cart);
		System.out.println("Exiting cartAdd...");
		return cartAddResponse;
	}

	public CartClearResponse CartClear(CartClear cartClear) {
		System.out.println("CartServiceID: " + this.toString());
		System.out.println("Entering cartClear...");
		CartClearResponse cartClearResponse = new CartClearResponse();
		
		//CartClearRequest cartClearRequest = cartClear.getRequest().get(0);
		
		//Cart cart = getCart(cartClearRequest.getCartId());
		Cart cart = getCart(cartClear.getAWSAccessKeyId());
		if(cart == null) {
			cartClearResponse.getCart().add(new Cart());
			return cartClearResponse;
		}
		
		cart.getCartItems().getCartItem().clear();
				
		cartClearResponse.getCart().add(cart);
		System.out.println("Exiting cartClear...");
		return cartClearResponse;
	}

	public CartCreateResponse CartCreate(CartCreate cartCreate) {
		System.out.println("CartServiceID: " + this.toString());
		System.out.println("Entering cartCreate...");
		CartCreateResponse cartCreateResponse = new CartCreateResponse();
		
		Cart cart = getCart(cartCreate.getAWSAccessKeyId());
		if(cart != null){
			cartCreateResponse.getCart().add(cart);
			System.out.println("User " + cartCreate.getAWSAccessKeyId() + " has already created a cart with ID: " + cart.getCartId());
			return cartCreateResponse;
		}

		cart = new Cart();		
		cart.setCartId(this.generateID());
		cart.setCartItems(new CartItems());		
		addCart(cartCreate.getAWSAccessKeyId(), cart);
		
		cartCreateResponse.getCart().add(cart);
		System.out.println("Exiting cartCreate...");
		return cartCreateResponse;
	}

	public CartGetResponse CartGet(CartGet cartGet) {
		System.out.println("CartServiceID: " + this.toString());
		System.out.println("Entering cartGet...");
		CartGetResponse cartGetResponse = new CartGetResponse();
		
		//CartGetRequest cartGetRequest = cartGet.getRequest().get(0);
		
		//Cart cart = getCart(cartGetRequest.getCartId());
		Cart cart = getCart(cartGet.getAWSAccessKeyId());
		if(cart == null){
			cartGetResponse.getCart().add(new Cart());
			return cartGetResponse;
		}
		
		cartGetResponse.getCart().add(cart);
		System.out.println("Exiting cartGet...");
		return cartGetResponse;
	}

	public CartModifyResponse CartModify(CartModify cartModify) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private synchronized String generateID(){
		ID++;
		return String.valueOf(ID);
	}
	
	
	private Cart getCart(String cartId){
		Cart cart = null;
		System.out.println(this.cartsHash.toString());
		cart = this.cartsHash.get(cartId);
		return cart;
	}
	
	private void addCart(String cartId, Cart cart){
		this.cartsHash.put(cartId, cart);
	}
/*
	public void start() {
		System.out.println("Start CartService...");
	}

	public void stop() {
		System.out.println("Stop CartService...");
	}
*/	
}
