package com.example.justpost.domain.post;

import com.example.justpost.domain.InvoiceNumberMap;
import com.example.justpost.domain.PostReservation;
import com.example.justpost.domain.utils.ExcelUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public abstract class PostHandler {

    public void save(List<PostReservation> postReservations,
                     String storeName) throws Exception {
        Workbook postWorkbook = convertToWorkbook(postReservations);

        // save
        ExcelUtil.save(postWorkbook, getPostFilePath(storeName));

        // close
        postWorkbook.close();
    }

    public abstract InvoiceNumberMap getInvoiceNumberMap(String postString);
    public abstract InvoiceNumberMap getInvoiceNumberMap(MultipartFile postFile) throws Exception;

    abstract Workbook convertToWorkbook(List<PostReservation> postReservationValues) throws Exception;

    public abstract String getPostFilePath(String storeName);

    abstract String getPostTemplateFilePath();
}
