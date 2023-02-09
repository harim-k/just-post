package com.example.justpost.domain.store.post;

import com.example.justpost.domain.PostInfo;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.Optional;

public abstract class PostConverter {

    public abstract List<PostInfo> convert(MultipartFile file) throws Exception;

    void addToPost(List<List<String>> postValues,
                   List<String> postRowValues,
                   String 수량, String 품목) {
        int 배송요청사항Index = 6;
        int sameOrderRowIndex = getSameOrderRowIndex(postValues, postRowValues);

        if (sameOrderRowIndex != -1) {
            List<String> sameOrderRowValues = postValues.get(sameOrderRowIndex);
            sameOrderRowValues.set(
                    배송요청사항Index,
                    String.join(" ", 품목, 수량, sameOrderRowValues.get(배송요청사항Index)));
        } else {
            postValues.add(postRowValues);
        }
    }

    void addToPostInfo(List<PostInfo> postInfos,
                       PostInfo postInfo) {
        Optional<PostInfo> optionalSamePostInfo = postInfos.stream()
                .filter(_postInfo -> _postInfo.isSame(postInfo))
                .findFirst();

        if (optionalSamePostInfo.isEmpty()) {
            postInfos.add(postInfo);
        } else {
            optionalSamePostInfo.get()
                    .getProductInfos()
                    .addAll(postInfo.getProductInfos());
        }
    }


    private int getSameOrderRowIndex(List<List<String>> postExcelValues, List<String> rowValues) {
        for (int i = 0; i < postExcelValues.size(); i++) {
            if (isSameAddress(postExcelValues.get(i), rowValues)) {
                return i;
            }
        }

        return -1;
    }

    private boolean hasSameAddress(List<List<String>> postExcelValues, List<String> rowValues) {
        for (List<String> postRowValues : postExcelValues) {
            if (isSameAddress(postRowValues, rowValues)) {
                return true;
            }
        }

        return false;
    }

    private boolean isSameAddress(List<String> rowValue1, List<String> rowValue2) {
        return hasSameValueAt(rowValue1, rowValue2, 0)
                && hasSameValueAt(rowValue1, rowValue2, 1)
                && hasSameValueAt(rowValue1, rowValue2, 2)
                && hasSameValueAt(rowValue1, rowValue2, 3)
                && hasSameValueAt(rowValue1, rowValue2, 4);
    }

    private boolean hasSameValueAt(List<String> list1, List<String> list2, int index) {
        return StringUtils.equals(list1.get(index), list2.get(index));
    }

}
