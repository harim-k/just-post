package com.example.justpost.domain.store.post;

import com.example.justpost.domain.post.OrderColumnIndex;
import com.example.justpost.domain.post.Post;
import com.example.justpost.domain.utils.ExcelUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public abstract class PostConverter {

    abstract public String getProduct(String product,
                                      String option,
                                      String count);

    abstract OrderColumnIndex getOrderColumnIndex(String[][] orderSheet);

    abstract int getOrderSheetIndex();

    abstract int getHeaderRowIndex();


    public List<Post> convert(MultipartFile orderFile) throws Exception {
        List<Post> posts = new ArrayList<>();
        String[][] orderSheet = getOrderSheet(orderFile);
        OrderColumnIndex orderColumnIndex = getOrderColumnIndex(orderSheet);

        for (int rowIndex = getHeaderRowIndex() + 1; rowIndex < orderSheet.length; rowIndex++) {
            String[] orderRow = orderSheet[rowIndex];
            Post post = Post.create(orderRow, orderColumnIndex, this);
            posts.add(post);
        }

        return posts;
    }

    String[][] getOrderSheet(MultipartFile file) throws Exception {
        Workbook orderWorkbook = WorkbookFactory.create(file.getInputStream());
        String[][] orderSheet = ExcelUtil.workbookToArray(
                orderWorkbook,
                getOrderSheetIndex(),
                getHeaderRowIndex());

        // close workbook
        orderWorkbook.close();
        return orderSheet;
    }
}
