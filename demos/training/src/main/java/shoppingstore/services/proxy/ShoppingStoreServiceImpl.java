package shoppingstore.services.proxy;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

import shoppingstore.services.cart.CartService;

import com.amazon.webservices.awsecommerceservice._2007_05_14.CartAddResponse;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartClearResponse;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartCreateResponse;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartGetResponse;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartModifyResponse;

@Scope("COMPOSITE")
public class ShoppingStoreServiceImpl implements ShoppingStoreService {

	private CartService cartService;
    
    @Reference
    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

	public CartAddResponse CartAdd(
			com.amazon.webservices.awsecommerceservice._2007_05_14.CartAdd cartAdd) {
		return cartService.CartAdd(cartAdd);
	}

	public CartClearResponse CartClear(
			com.amazon.webservices.awsecommerceservice._2007_05_14.CartClear cartClear) {
		return cartService.CartClear(cartClear);
	}

	public CartCreateResponse CartCreate(
			com.amazon.webservices.awsecommerceservice._2007_05_14.CartCreate cartCreate) {
		return cartService.CartCreate(cartCreate);
	}

	public CartGetResponse CartGet(
			com.amazon.webservices.awsecommerceservice._2007_05_14.CartGet cartGet) {
		return cartService.CartGet(cartGet);
	}

	public CartModifyResponse CartModify(
			com.amazon.webservices.awsecommerceservice._2007_05_14.CartModify cartModify) {
		return cartService.CartModify(cartModify);
	}

}
