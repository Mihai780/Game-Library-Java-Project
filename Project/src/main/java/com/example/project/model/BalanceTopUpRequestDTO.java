package com.example.project.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BalanceTopUpRequestDTO {

    @NotNull
    @Positive
    private Long amountCents;

    public Long getAmountCents() {
        return amountCents;
    }

    public void setAmountCents(Long amountCents) {
        this.amountCents = amountCents;
    }
}
