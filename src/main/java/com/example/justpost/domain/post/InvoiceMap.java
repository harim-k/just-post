package com.example.justpost.domain.post;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class InvoiceMap {
    private final Map<String, String> map = new HashMap<>();

    public String get(String postcode) {
        return map.getOrDefault(postcode, null);
    }

    public void put(String postcode, String invoiceNumber) {
        map.put(postcode, invoiceNumber);
    }
}
