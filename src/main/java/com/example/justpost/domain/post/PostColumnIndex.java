package com.example.justpost.domain.post;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostColumnIndex {
    private final int nameColumnIndex;
    private final int postcodeColumnIndex;
    private final int invoiceNumberColumnIndex;
}
