package com.payment_service.controller;

import com.payment_service.dto.ProductRequest;
import com.payment_service.dto.StripeResponse;
import com.payment_service.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.stripe.model.checkout.Session;

@RestController
@RequestMapping("/product/v1")
public class ProductCheckoutController {


    private StripeService stripeService;

    public ProductCheckoutController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> checkoutProducts(@RequestBody ProductRequest productRequest) {
        StripeResponse stripeResponse = stripeService.checkoutProducts(productRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stripeResponse);
    }

    @GetMapping("/success")
    public ResponseEntity<String> handleSuccess(@RequestParam("session_id") String sessionId) {
        // Replace with your actual secret key

        try {
            Session session = Session.retrieve(sessionId);
            String paymentStatus = session.getPaymentStatus();

            if ("paid".equalsIgnoreCase(paymentStatus)) {
                //Update Booking Details
                System.out.println("✅ Payment successful: true");
                return ResponseEntity.ok("Payment successful");
            } else {
                System.out.println("❌ Payment not completed: false");
                return ResponseEntity.status(400).body("Payment not completed");
            }

        } catch (StripeException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Stripe error occurred");
        }
    }


    @GetMapping("/cancel")
    public ResponseEntity<String> handleCancel() {
        System.out.println("❌ Payment cancelled: false");
        return ResponseEntity.ok("Payment cancelled");
    }
}


