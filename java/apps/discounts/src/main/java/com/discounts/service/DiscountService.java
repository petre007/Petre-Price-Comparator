package com.discounts.service;

import com.discounts.dto.ApplyDiscountDTO;
import com.discounts.dto.DiscountDTO;
import com.discounts.model.DiscountProduct;
import com.discounts.model.KauflandDiscount;
import com.discounts.model.LidlDiscount;
import com.discounts.model.ProfiDiscount;
import com.discounts.repository.KauflandDiscountRepository;
import com.discounts.repository.LidlDiscountRepository;
import com.discounts.repository.ProfiDiscountRepository;
import com.pc_client.impl.PcClientImpl;
import com.pc_client.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscountService {

    private final LidlDiscountRepository lidlDiscountRepository;
    private final KauflandDiscountRepository kauflandDiscountRepository;
    private final ProfiDiscountRepository profiDiscountRepository;
    private final PcClientImpl pcClient;
    private final LambdaService lambdaService;

    @Transactional
    public void createDiscount(DiscountDTO discountDTO)
            throws Exception {

        Product product = this.pcClient.getProductById(discountDTO.productId()).block();

        if (product == null) {
            log.error("No product with id {} in product_catalog", discountDTO.productId());
            return;
        }

        DiscountProduct response = null;

        switch (discountDTO.shop()) {
            case KAUFLAND -> response = this.kauflandDiscountRepository.save(KauflandDiscount.builder()
                    .productId(product.getProductId())
                    .brand(product.getBrand())
                    .packageQuantity(product.getPackageQuantity())
                    .packageUnit(product.getPackageUnit())
                    .productCategory(product.getProductCategory())
                    .toDate(LocalDate.parse(discountDTO.toDate()).atStartOfDay())
                    .fromDate(LocalDate.parse(discountDTO.fromDate()).atStartOfDay())
                    .discountPercentage(discountDTO.percentageOfDiscount())
                    .build());
            case PROFI -> response = this.profiDiscountRepository.save(ProfiDiscount.builder()
                    .productId(product.getProductId())
                    .brand(product.getBrand())
                    .packageQuantity(product.getPackageQuantity())
                    .packageUnit(product.getPackageUnit())
                    .productCategory(product.getProductCategory())
                    .toDate(LocalDate.parse(discountDTO.toDate()).atStartOfDay())
                    .fromDate(LocalDate.parse(discountDTO.fromDate()).atStartOfDay())
                    .discountPercentage(discountDTO.percentageOfDiscount())
                    .build());
            case LIDL -> response = this.lidlDiscountRepository.save(LidlDiscount.builder()
                    .productId(product.getProductId())
                    .brand(product.getBrand())
                    .packageQuantity(product.getPackageQuantity())
                    .packageUnit(product.getPackageUnit())
                    .productCategory(product.getProductCategory())
                    .toDate(LocalDate.parse(discountDTO.toDate()).atStartOfDay())
                    .fromDate(LocalDate.parse(discountDTO.fromDate()).atStartOfDay())
                    .discountPercentage(discountDTO.percentageOfDiscount())
                    .build());
        }

        log.info("Product stored in {} with product_id {}", discountDTO.shop(), response.getProductId());

        String lambdaResponse = this.lambdaService.invokeApplyDiscountLambda(ApplyDiscountDTO.builder()
                .productId(response.getProductId())
                .shop(discountDTO.shop())
                .discountPercentage(response.getDiscountPercentage())
                .expiryDate(response.getToDate().toString())
                .applyDate(response.getFromDate().toString())
                .build());

        log.info("Lambda response: {}", lambdaResponse);
    }

}
