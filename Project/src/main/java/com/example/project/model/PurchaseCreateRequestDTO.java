package com.example.project.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class PurchaseCreateRequestDTO {

    @NotNull
    @PositiveOrZero
    private Long priceCents;

    public PurchaseCreateRequestDTO() {
    }

    public Long getPriceCents() {
        return priceCents;
    }

    public void setPriceCents(Long priceCents) {
        this.priceCents = priceCents;
    }
}
