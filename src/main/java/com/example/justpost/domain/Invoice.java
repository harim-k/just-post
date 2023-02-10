package com.example.justpost.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class Invoice {
    private final String name;
    private final String postcode;
    private final String invoiceNumber;

}
