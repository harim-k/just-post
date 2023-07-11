package com.example.justpost.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Product {
    private List<String> products = new ArrayList<>();

    public Product(String product) {
        this.products.add(product);
    }

    public void addAll(Product product) {
        products.addAll(product.getProducts());
    }

    public int size() {
        return products.size();
    }

    @Override
    public String toString() {
        return String.join(" ", getProducts());
    }
}
