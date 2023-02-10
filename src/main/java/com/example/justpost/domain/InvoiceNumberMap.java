package com.example.justpost.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.math3.util.Pair;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class InvoiceNumberMap {
    private final Map<Pair<String, String>, String> map = new HashMap<>();

    public String get(String name, String postcode) {
        return map.getOrDefault(new Pair<>(name, postcode), null);
    }

    public void put(String name, String postcode, String invoiceNumber) {
        map.put(new Pair<>(name, postcode), invoiceNumber);
    }
}
