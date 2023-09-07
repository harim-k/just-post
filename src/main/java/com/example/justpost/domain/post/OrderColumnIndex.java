package com.example.justpost.domain.post;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderColumnIndex {
    private final int nameColumnIndex;
    private final int postcodeColumnIndex;
    private final int addressColumnIndex;
    private final int contact1ColumnIndex;
    private final int contact2ColumnIndex;
    private final int optionColumnIndex;
    private final int productColumnIndex;
    private final int countColumnIndex;
    private final int messageColumnIndex;
}
