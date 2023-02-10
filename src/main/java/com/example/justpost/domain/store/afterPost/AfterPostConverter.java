package com.example.justpost.domain.store.afterPost;

import com.example.justpost.domain.InvoiceNumberMap;
import com.example.justpost.domain.Post;
import org.apache.commons.math3.util.Pair;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AfterPostConverter {

    public abstract List<List<String>> convertAndSave(MultipartFile file,
                                                      InvoiceNumberMap invoiceNumberMap) throws Exception;

    public abstract String getAfterPostFilePath();

    public Map<Pair<String, String>, String> getPostMap(List<Post> posts) {
        Map<Pair<String, String>, String> postMap = new HashMap<>();

        for (Post post : posts) {
            postMap.put(
                    new Pair<>(post.getName(), post.getPostcode()),
                    post.getInvoiceNumber());
        }

        return postMap;
    }

}
