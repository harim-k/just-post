package com.example.justpost.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.math3.util.Pair;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class InvoiceNumberMap {
    private final Map<String, String> map = new HashMap<>();

    public String get(String postcode) {
        return map.getOrDefault(postcode, null);
    }

    public void put(String postcode, String invoiceNumber) {
        map.put(postcode, invoiceNumber);
    }
}
