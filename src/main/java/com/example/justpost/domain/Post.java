package com.example.justpost.domain;

import com.example.justpost.domain.store.post.PostConverter;
import lombok.Builder;
import lombok.Data;
import org.thymeleaf.util.StringUtils;

@Data
@Builder
public class Post {
    private final String name;
    private final String postcode;
    private final String address;
    private final String contact1;
    private final String contact2;
    private final Product product;
    private final String message;

    public static Post create(String[] orderRow,
                              OrderColumnIndex orderColumnIndex,
                              PostConverter postConverter) {
        return Post.builder()
                .name(orderRow[orderColumnIndex.getNameColumnIndex()])
                .postcode(orderRow[orderColumnIndex.getPostcodeColumnIndex()])
                .address(orderRow[orderColumnIndex.getAddressColumnIndex()])
                .contact1(orderRow[orderColumnIndex.getContact1ColumnIndex()])
                .contact2(orderRow[orderColumnIndex.getContact2ColumnIndex()])
                .product(new Product(postConverter.getProduct(
                        orderRow[orderColumnIndex.getProductColumnIndex()],
                        orderRow[orderColumnIndex.getOptionColumnIndex()],
                        orderRow[orderColumnIndex.getCountColumnIndex()])))
                .message(orderRow[orderColumnIndex.getMessageColumnIndex()])
                .build();
    }

    public boolean isSame(Post post) {
        return StringUtils.equals(this.name, post.name)
                && StringUtils.equals(this.address, post.address);
    }


}
