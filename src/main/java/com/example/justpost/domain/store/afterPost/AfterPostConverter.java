package com.example.justpost.domain.store.afterPost;

import com.example.justpost.domain.Invoice;
import org.apache.commons.math3.util.Pair;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AfterPostConverter {

    public abstract List<List<String>> convertAndSave(MultipartFile file,
                                                      List<Invoice> invoices) throws Exception;

    public abstract String getAfterPostFilePath();

    public Map<Pair<String, String>, String> makeInvoiceMap(List<Invoice> invoices) {
        Map<Pair<String, String>, String> invoiceMap = new HashMap<>();

        for (Invoice invoice : invoices) {
            invoiceMap.put(
                    new Pair<>(invoice.getName(), invoice.getPostcode()),
                    invoice.getInvoiceNumber());
        }

        return invoiceMap;
    }

}
