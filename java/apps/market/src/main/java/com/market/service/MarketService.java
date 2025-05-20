package com.market.service;

import com.market.dto.ShopDTO;
import com.market.model.KauflandShop;
import com.market.model.LidlShop;
import com.market.model.ProfiShop;
import com.market.model.ShopProduct;
import com.market.repository.KauflandShopRepository;
import com.market.repository.LidlShopRepository;
import com.market.repository.ProfiShopRepository;
import com.pc_client.impl.PcClientImpl;
import com.pc_client.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarketService {

    private final PcClientImpl pcClient;
    private final LidlShopRepository lidlShopRepository;
    private final KauflandShopRepository kauflandShopRepository;
    private final ProfiShopRepository profiShopRepository;

    @Transactional
    public void addProductToShop(ShopDTO shopDTO) {

        var product = this.pcClient.getProductById(shopDTO.product_id()).block();

        if (product == null) {
            log.error("No product with id {} in product_catalog", shopDTO.product_id());
            return;
        }

        ShopProduct response = null;
        switch (shopDTO.shops()) {
            case LIDL -> response = this.lidlShopRepository.save(LidlShop.builder()
                        .productId(product.getProductId())
                        .productCategory(product.getProductCategory())
                        .productName(product.getProductName())
                        .packageQuantity(product.getPackageQuantity())
                        .brand(product.getBrand())
                        .packageUnit(product.getPackageUnit())
                        .price(shopDTO.price())
                        .currency(shopDTO.currency())
                        .build());
            case PROFI -> response = this.profiShopRepository.save(ProfiShop.builder()
                    .productId(product.getProductId())
                    .productCategory(product.getProductCategory())
                    .productName(product.getProductName())
                    .packageQuantity(product.getPackageQuantity())
                    .brand(product.getBrand())
                    .packageUnit(product.getPackageUnit())
                    .price(shopDTO.price())
                    .currency(shopDTO.currency())
                    .build());
            case KAUFLAND -> response = this.kauflandShopRepository.save(KauflandShop.builder()
                    .productId(product.getProductId())
                    .productCategory(product.getProductCategory())
                    .productName(product.getProductName())
                    .packageQuantity(product.getPackageQuantity())
                    .brand(product.getBrand())
                    .packageUnit(product.getPackageUnit())
                    .price(shopDTO.price())
                    .currency(shopDTO.currency())
                    .build());
        }

        log.info("Product stored in {} with product_id {}", shopDTO.shops(), response.getProductId());
    }

}
