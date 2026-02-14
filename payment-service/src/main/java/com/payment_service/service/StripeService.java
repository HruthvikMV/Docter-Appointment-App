package com.payment_service.service;
import com.payment_service.dto.ProductRequest;
import com.payment_service.dto.StripeResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service
public class StripeService {

    @Value("${stripe.secretKey}")
    private String secretKey;

    //stripe -API
    //-> productName , amount , quantity , currency
    //-> return sessionId and url



    public StripeResponse checkoutProducts(ProductRequest productRequest) {
        // Set your secret key. Remember to switch to your live secret key in production!
        Stripe.apiKey = secretKey;

        // Create a PaymentIntent with the order amount and currency
        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(productRequest.getName())
                        .build();

        // Create new line item with the above product data and associated price
        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(productRequest.getCurrency() != null ? productRequest.getCurrency() : "USD")
                        .setUnitAmount(productRequest.getAmount())
                        .setProductData(productData)
                        .build();

        // Create new line item with the above price data
        SessionCreateParams.LineItem lineItem =
                SessionCreateParams
                        .LineItem.builder()
                        .setQuantity(productRequest.getQuantity())
                        .setPriceData(priceData)
                        .build();

        // Create new session with the line items
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:5555/product/v1/success?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl("http://localhost:5555/cancel")
                        .addLineItem(lineItem)
                        .build();

        // Create new session
        Session session = null;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            //log the error
        }
       
         StripeResponse sr = new StripeResponse();
         sr.setStatus("SUCCESS");
         sr.setMessage("Payment session created ");
         sr.setSessionId(session.getId());
         sr.setSessionUrl(session.getUrl());
         
         return sr;

    }

}
