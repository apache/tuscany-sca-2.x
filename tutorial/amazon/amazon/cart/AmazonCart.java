package amazon.cart;

import org.osoa.sca.annotations.Remotable;

import com.cart.amazon.*;

@Remotable
public interface AmazonCart {
	
	public CartCreateResponse CartCreate(CartCreate cartCreate);
	public CartAddResponse CartAdd(CartAdd cartAdd);
	public CartClearResponse CartClear(CartClear cartClear);
	public CartGetResponse CartGet(CartGet cartGet);
}
