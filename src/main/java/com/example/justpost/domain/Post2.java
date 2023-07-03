package com.example.justpost.domain;

import lombok.Builder;
import lombok.Data;
import org.thymeleaf.util.StringUtils;

@Data
@Builder
public class Post2 {
    private final String name;
    private final String invoiceNumber;
    private final String postcode;
    private final String address;
    private final String contact1;
    private final String contact2;
    private final String message;


    public boolean isSame(Post2 postReservation) {
        return StringUtils.equals(this.name, postReservation.name)
                && StringUtils.equals(this.address, postReservation.address);
    }
}
