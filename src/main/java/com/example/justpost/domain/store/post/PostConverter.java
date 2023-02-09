package com.example.justpost.domain.store.post;

import com.example.justpost.domain.Order;
import com.example.justpost.domain.OrderIndexInfo;
import com.example.justpost.domain.Post;
import com.example.justpost.domain.utils.ExcelUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class PostConverter {

    public List<Post> convert(MultipartFile file) throws Exception {
        String[][] orderSheet = getOrderSheet(file);
        OrderIndexInfo orderIndexInfo = getOrderIndexInfo(orderSheet);
        return convertOrderToPost(orderSheet, orderIndexInfo);
    }

    abstract OrderIndexInfo getOrderIndexInfo(String[][] orderSheet);

    String[][] getOrderSheet(MultipartFile file) throws Exception {
        Workbook orderWorkbook = WorkbookFactory.create(file.getInputStream());
        String[][] orderSheet = ExcelUtil.workbookToArray(
                orderWorkbook,
                getSheetIndex(),
                getHeaderRowIndex());

        // close workbook
        orderWorkbook.close();
        return orderSheet;
    }

    abstract int getSheetIndex();

    abstract int getHeaderRowIndex();

    void addToPosts(List<Post> posts,
                    Post post) {
        Optional<Post> optionalSamePostInfo = posts.stream()
                .filter(_postInfo -> _postInfo.isSame(post))
                .findFirst();

        if (optionalSamePostInfo.isEmpty()) {
            posts.add(post);
        } else {
            optionalSamePostInfo.get()
                    .getProductInfos()
                    .addAll(post.getProductInfos());
        }
    }

    List<Post> convertOrderToPost(String[][] orderSheet,
                                  OrderIndexInfo orderIndexInfo) {
        List<Post> posts = new ArrayList<>();

        // copy second ~ last row from order sheet
        for (int rowIndex = getHeaderRowIndex() + 1; rowIndex < orderSheet.length; rowIndex++) {
            String[] orderRow = orderSheet[rowIndex];
            Order order = makeOrder(orderRow, orderIndexInfo);
            Post post = converOrderToPost(order);

            // 같은 주소인 경우 하나로 합치기
            addToPosts(posts, post);
        }

        return posts;
    }


    int getIndex(String[] orderHeaderRow,
                 String string) {
        for (int i = 0; i < orderHeaderRow.length; i++) {
            if (StringUtils.equals(orderHeaderRow[i], string)) {
                return i;
            }
        }

        return -1;
    }

    Order makeOrder(String[] orderRow, OrderIndexInfo orderIndexInfo) {
        return Order.builder()
                .name(orderRow[orderIndexInfo.getNameColumnIndex()])
                .postcode(orderRow[orderIndexInfo.getPostcodeColumnIndex()])
                .address(orderRow[orderIndexInfo.getAddressColumnIndex()])
                .contact1(orderRow[orderIndexInfo.getContact1ColumnIndex()])
                .contact2(orderRow[orderIndexInfo.getContact2ColumnIndex()])
                .product(orderRow[orderIndexInfo.getProductColumnIndex()])
                .option(orderRow[orderIndexInfo.getOptionColumnIndex()])
                .count(orderRow[orderIndexInfo.getCountColumnIndex()])
                .message(orderRow[orderIndexInfo.getMessageColumnIndex()])
                .build();
    }

    Post converOrderToPost(Order order) {
        final String productInfo = getProductInfo(
                order.getProduct(),
                order.getOption(),
                order.getCount());

        return Post.builder()
                .name(order.getName())
                .postcode(order.getPostcode())
                .address(order.getAddress())
                .contact1(order.getContact1())
                .contact2(order.getContact2())
                .productInfos(new ArrayList<>(List.of(productInfo)))
                .message(order.getMessage())
                .build();
    }

    abstract String getProductInfo(String product,
                                   String option,
                                   String count);

}
