package com.selimhorri.app.feature;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FeatureToggleService {

    @Value("${feature.product.count-enabled:false}")
    private boolean productCountEnabled;

    public boolean isProductCountEnabled() {
        return productCountEnabled;
    }

    // setter para tests (opcional)
    public void setProductCountEnabled(boolean enabled) {
        this.productCountEnabled = enabled;
    }
}