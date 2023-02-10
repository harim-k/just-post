package com.example.justpost.domain.store.post;

import com.example.justpost.domain.Order;
import com.example.justpost.domain.OrderColumnIndex;
import com.example.justpost.domain.PostReservation;
import com.example.justpost.domain.utils.ExcelUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class PostConverter {

    public List<PostReservation> convert(MultipartFile orderFile) throws Exception {
        String[][] orderSheet = getOrderSheet(orderFile);
        OrderColumnIndex orderColumnIndex = getOrderColumnIndex(orderSheet);
        return convert(orderSheet, orderColumnIndex);
    }


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

    void addOrMerge(List<PostReservation> postReservations,
                    PostReservation postReservation) {
        Optional<PostReservation> optionalSamePostReservation = postReservations.stream()
                .filter(_postInfo -> _postInfo.isSame(postReservation))
                .findFirst();

        if (optionalSamePostReservation.isEmpty()) {
            postReservations.add(postReservation);
        } else {
            optionalSamePostReservation.get()
                    .getProducts()
                    .addAll(postReservation.getProducts());
        }
    }

    List<PostReservation> convert(String[][] orderSheet,
                                  OrderColumnIndex orderColumnIndex) {
        List<PostReservation> postReservations = new ArrayList<>();

        for (int rowIndex = getHeaderRowIndex() + 1; rowIndex < orderSheet.length; rowIndex++) {
            String[] orderRow = orderSheet[rowIndex];
            Order order = getOrder(orderRow, orderColumnIndex);
            PostReservation postReservation = convert(order);

            // 같은 주소인 경우 하나로 합치기
            addOrMerge(postReservations, postReservation);
        }

        return postReservations;
    }

    Order getOrder(String[] orderRow, OrderColumnIndex orderColumnIndex) {
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

    PostReservation convert(Order order) {
        final String product = getProduct(
                order.getProduct(),
                order.getOption(),
                order.getCount());

        return PostReservation.builder()
                .name(order.getName())
                .postcode(order.getPostcode())
                .address(order.getAddress())
                .contact1(order.getContact1())
                .contact2(order.getContact2())
                .products(new ArrayList<>(List.of(product)))
                .message(order.getMessage())
                .build();
    }

    abstract String getProduct(String product,
                               String option,
                               String count);

    abstract OrderColumnIndex getOrderColumnIndex(String[][] orderSheet);

    abstract int getSheetIndex();

    abstract int getHeaderRowIndex();

}
