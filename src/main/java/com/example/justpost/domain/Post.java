package com.example.justpost.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Post {
    private final String name;
    private final String postcode;
    private final String invoiceNumber;

    @Override
    public String toString() {
        return String.join(" ", name, postcode, invoiceNumber);
    }
}
