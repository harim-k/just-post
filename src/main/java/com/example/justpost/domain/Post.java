package com.example.justpost.domain;

import lombok.Builder;
import lombok.Data;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@Data
@Builder
public class Post {
    private final String name;
    private final String postcode;
    private final String address;
    private final String contact1;
    private final String contact2;
    private final List<String> productInfos;
    private final String message;


    public boolean isSame(Post post) {
        return StringUtils.equals(this.name, post.name)
                && StringUtils.equals(this.address, post.address);
    }
}
