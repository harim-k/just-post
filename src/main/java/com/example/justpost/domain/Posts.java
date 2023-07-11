package com.example.justpost.domain;

import com.example.justpost.domain.store.post.PostConverter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Posts {
    private final List<Post> posts;

    public Posts(List<Post> posts) {
        this.posts = new ArrayList<>();

        for (Post post : posts) {
            addOrMerge(post);
        }
    }

    public static Posts create(MultipartFile orderFile,
                               PostConverter postConverter) throws Exception {
        List<Post> posts = postConverter.convert(orderFile);
        return new Posts(posts);
    }

    void addOrMerge(Post post) {
        Optional<Post> optionalSamePost = posts.stream()
                .filter(_post -> _post.isSame(post))
                .findFirst();

        if (optionalSamePost.isEmpty()) {
            posts.add(post);
        } else {
            optionalSamePost.get()
                    .getProduct()
                    .addAll(post.getProduct());
        }
    }

    public int size() {
        return posts.size();
    }


    public Post get(int i) {
        return posts.get(i);
    }
}
