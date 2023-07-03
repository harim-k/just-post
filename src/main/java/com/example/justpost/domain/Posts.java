package com.example.justpost.domain;

import com.example.justpost.domain.store.post.PostConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Posts {
    private final List<Post> posts;

    public Posts() {
        this.posts = new ArrayList<>();
    }

    public static Posts create(String[][] orderSheet,
                               OrderColumnIndex orderColumnIndex,
                               PostConverter postConverter) {
        Posts posts = new Posts();

        for (int rowIndex = postConverter.getHeaderRowIndex() + 1; rowIndex < orderSheet.length; rowIndex++) {
            String[] orderRow = orderSheet[rowIndex];
            Order order = Order.create(orderRow, orderColumnIndex);
            Post post = order.convertToPost(postConverter);

            // 같은 주소인 경우 하나로 합치기
            posts.addOrMerge(post);
        }

        return posts;
    }

    void addOrMerge(Post post) {
        Optional<Post> optionalSamePost = posts.stream()
                .filter(_post -> _post.isSame(post))
                .findFirst();

        if (optionalSamePost.isEmpty()) {
            posts.add(post);
        } else {
            optionalSamePost.get()
                    .getProducts()
                    .addAll(post.getProducts());
        }
    }

    public int size() {
        return posts.size();
    }


    public Post get(int i) {
        return posts.get(i);
    }
}
