package com.example.justpost.domain.store.afterPost;

import com.example.justpost.domain.InvoiceMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public abstract class AfterPostConverter {

    public abstract List<List<String>> convertAndSave(MultipartFile file,
                                                      InvoiceMap invoiceMap) throws Exception;

    public abstract String getAfterPostFilePath();

}
