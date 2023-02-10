package com.example.justpost.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvoiceIndexInfo {
    private final int nameColumnIndex;
    private final int postcodeColumnIndex;
    private final int addressColumnIndex;
    private final int invoiceNumberColumnIndex;
}
