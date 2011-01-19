package org.example.orderservice;

import javax.xml.ws.Holder;

import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Service;

@Service(OrderService.class)
public class OrderServiceForwardImpl implements OrderService {

	@Reference
	public OrderService ref;
	
	public String[] reviewOrder(Holder<Order> myData, Holder<Float> myOutParam) {
		String[] retVal = ref.reviewOrder(myData, myOutParam);
		return retVal;
	}

	@Override
	public String[] reviewOrderTwoInOuts(Holder<Order> myData,
			Holder<Float> myOutParam) {
		String[] retVal = ref.reviewOrderTwoInOuts(myData, myOutParam);
		return retVal;
	}

	@Override
	public String[] reviewOrderTwoOutHolders(Holder<Order> myData,
			Holder<Float> myOutParam) {
		String[] retVal = ref.reviewOrderTwoOutHolders(myData, myOutParam);
		return retVal;
	}

	@Override
	public String[] reviewOrderTwoInOutsThenIn(Holder<Order> myData,
			Holder<Float> myOutParam, Integer myCode) {
		String[] retVal = ref.reviewOrderTwoInOutsThenIn(myData, myOutParam, myCode);
		return retVal;
	}


}
