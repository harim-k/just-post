package com.example.justpost.domain;

import lombok.Builder;
import lombok.Data;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Order {
    private final String name;
    private final String postcode;
    private final String address;
    private final String contact1;
    private final String contact2;
    private final String product;
    private final String option;
    private final String count;
    private final String message;
}
