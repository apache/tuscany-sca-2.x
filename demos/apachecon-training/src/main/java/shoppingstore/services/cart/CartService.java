package shoppingstore.services.cart;

import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.annotations.Service;

import com.amazon.webservices.awsecommerceservice._2007_05_14.CartAdd;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartAddResponse;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartClear;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartClearResponse;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartCreate;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartCreateResponse;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartGet;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartGetResponse;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartModify;
import com.amazon.webservices.awsecommerceservice._2007_05_14.CartModifyResponse;

@Remotable
@Service
public interface CartService {
	
	public CartCreateResponse CartCreate(CartCreate cartCreate);
	
	public CartAddResponse CartAdd(CartAdd cartAdd);
	
	public CartModifyResponse CartModify(CartModify cartModify);
	
	public CartClearResponse CartClear(CartClear cartClear);

	public CartGetResponse CartGet(CartGet cartGet);
	
	//@Init
	//public void start();
	
	//@Destroy
	//public void stop();

}
