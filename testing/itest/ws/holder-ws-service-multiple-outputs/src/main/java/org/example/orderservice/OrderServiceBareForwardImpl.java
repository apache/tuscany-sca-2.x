package org.example.orderservice;

import javax.xml.ws.Holder;

import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Service;

@Service(OrderServiceBare.class)
public class OrderServiceBareForwardImpl implements OrderServiceBare {

    @Reference
    public OrderServiceBare ref;

    @Override
    public Order bareReviewOrder(Order myData) {
        Order retVal = ref.bareReviewOrder(myData);
        return retVal;
    }

    @Override
    public void bareReviewOrderInOutHolder(Holder<Order> myData) {
        ref.bareReviewOrderInOutHolder(myData);
    }

    @Override
    public void bareReviewOrderOutHolder(Holder<Order> myData) {
        ref.bareReviewOrderInOutHolder(myData);
    }
}
