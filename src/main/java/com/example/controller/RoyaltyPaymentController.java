package com.example.controller;

import com.example.model.Product;
import com.example.model.RoyaltyPayment;
import com.example.model.RoyaltyPayment.PayeeType;
import com.example.model.RoyaltyPayment.RoyaltyType;
import com.example.service.IProduct;
import com.example.service.IRoyaltyPaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/royalties")
public class RoyaltyPaymentController {

    @Autowired
    private IRoyaltyPaymentService royaltyPaymentService;

    @Autowired
    private IProduct productService;

    // 1. Create or update royalty payment with automatic amount calculation
    @PostMapping
    public RoyaltyPayment createOrUpdateRoyalty(@RequestBody RoyaltyPayment royaltyPayment) {

        Optional<Product> productOpt = productService.getProductById(royaltyPayment.getProduct().getProductId());

        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid product ID");
        }

        Product product = productOpt.get();
        BigDecimal price = product.getPrice();
        BigDecimal royaltyRate = BigDecimal.ZERO;

        // Determine the royalty rate based on payee type
        if (royaltyPayment.getPayeeType() == PayeeType.author) {
            royaltyRate = product.getRoyaltyAuthor();
        } else if (royaltyPayment.getPayeeType() == PayeeType.publisher) {
            royaltyRate = product.getRoyaltyPublisher();
        }

        BigDecimal calculatedAmount = price.multiply(royaltyRate != null ? royaltyRate : BigDecimal.ZERO);
        royaltyPayment.setAmount(calculatedAmount);

        return royaltyPaymentService.saveRoyaltyPayment(royaltyPayment);
    }

    // 2. Get all royalty payments
    @GetMapping
    public List<RoyaltyPayment> getAllRoyaltyPayments() {
        return royaltyPaymentService.getAllRoyaltyPayments();
    }

    // 3. Get royalty payment by ID
    @GetMapping("/{id}")
    public Optional<RoyaltyPayment> getRoyaltyPaymentById(@PathVariable int id) {
        return royaltyPaymentService.getRoyaltyPaymentById(id);
    }

    // 4. Get royalties by order ID
    @GetMapping("/order/{orderId}")
    public List<RoyaltyPayment> getByOrder(@PathVariable int orderId) {
        return royaltyPaymentService.getByOrderId(orderId);
    }

    // 5. Get royalties by product ID
    @GetMapping("/product/{productId}")
    public List<RoyaltyPayment> getByProduct(@PathVariable int productId) {
        return royaltyPaymentService.getByProductId(productId);
    }

    // 6. Get royalties by payee (author/publisher)
    @GetMapping("/payee")
    public List<RoyaltyPayment> getByPayee(@RequestParam PayeeType type,
                                           @RequestParam int id) {
        return royaltyPaymentService.getByPayee(type, id);
    }

    // 7. Get total royalty amount for a payee
    @GetMapping("/payee/total")
    public BigDecimal getTotalRoyaltyAmount(@RequestParam PayeeType type,
                                            @RequestParam int id) {
        return royaltyPaymentService.getTotalRoyaltyAmount(type, id);
    }

    // 8. Get royalties by royalty type (purchase/rental)
    @GetMapping("/type/{royaltyType}")
    public List<RoyaltyPayment> getByRoyaltyType(@PathVariable RoyaltyType royaltyType) {
        return royaltyPaymentService.getByRoyaltyType(royaltyType);
    }

    // 9. Delete royalty payment by ID
    @DeleteMapping("/{id}")
    public void deleteRoyaltyPayment(@PathVariable int id) {
        royaltyPaymentService.deleteRoyaltyPayment(id);
    }
}
