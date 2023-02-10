package com.example.justpost.domain;

import lombok.Builder;
import lombok.Data;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@Data
@Builder
public class PostReservation {
    private final String name;
    private final String postcode;
    private final String address;
    private final String contact1;
    private final String contact2;
    private final List<String> products;
    private final String message;


    public boolean isSame(PostReservation postReservation) {
        return StringUtils.equals(this.name, postReservation.name)
                && StringUtils.equals(this.address, postReservation.address);
    }
}
