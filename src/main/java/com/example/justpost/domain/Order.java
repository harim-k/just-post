package com.example.justpost.domain;

import com.example.justpost.domain.store.post.PostConverter;
import lombok.Builder;
import lombok.Data;

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


    public static Order create(String[] orderRow, OrderColumnIndex orderColumnIndex) {
        return Order.builder()
                .name(orderRow[orderColumnIndex.getNameColumnIndex()])
                .postcode(orderRow[orderColumnIndex.getPostcodeColumnIndex()])
                .address(orderRow[orderColumnIndex.getAddressColumnIndex()])
                .contact1(orderRow[orderColumnIndex.getContact1ColumnIndex()])
                .contact2(orderRow[orderColumnIndex.getContact2ColumnIndex()])
                .product(orderRow[orderColumnIndex.getProductColumnIndex()])
                .option(orderRow[orderColumnIndex.getOptionColumnIndex()])
                .count(orderRow[orderColumnIndex.getCountColumnIndex()])
                .message(orderRow[orderColumnIndex.getMessageColumnIndex()])
                .build();
    }

    public Post convertToPost(PostConverter postConverter) {
        final String product = postConverter.getProduct(
                getProduct(),
                getOption(),
                getCount());

        return Post.builder()
                .name(getName())
                .postcode(getPostcode())
                .address(getAddress())
                .contact1(getContact1())
                .contact2(getContact2())
                .products(new ArrayList<>(List.of(product)))
                .message(getMessage())
                .build();
    }


}
