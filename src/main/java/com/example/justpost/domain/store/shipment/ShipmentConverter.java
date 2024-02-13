package com.example.justpost.domain.store.shipment;

import com.example.justpost.domain.post.InvoiceMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public abstract class ShipmentConverter {

    public abstract List<List<String>> convertAndSave(MultipartFile file,
                                                      InvoiceMap invoiceMap) throws Exception;

    public abstract String getShipmentFilePath();

}
