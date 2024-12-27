package com.example.justpost.service.shipment;

import com.example.justpost.domain.post.ConvertType;
import com.example.justpost.domain.post.InvoiceMap;
import com.example.justpost.domain.posthandler.PostHandler;
import com.example.justpost.domain.posthandler.PostHandlerFactory;
import com.example.justpost.domain.store.shipment.ShipmentConverter;
import com.example.justpost.domain.store.shipment.ShipmentConverterFactory;
import com.example.justpost.domain.utils.FileUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class ShipmentService {

    private final ShipmentConverterFactory shipmentConverterFactory;
    private final PostHandlerFactory postHandlerFactory;

    @SneakyThrows
    public List<List<String>> convertAndSave(MultipartFile orderFile,
                                             ConvertType convertType,
                                             MultipartFile postFile,
                                             String postString) {

        ShipmentConverter shipmentConverter = shipmentConverterFactory.get(convertType);
        PostHandler postHandler = postHandlerFactory.get(postString);
        InvoiceMap invoiceMap;
        if (StringUtils.equals(postString, "")) {
            invoiceMap = postHandler.getInvoiceMap(postFile);
        } else {
            invoiceMap = postHandler.getInvoiceMap(postString);
        }

        return shipmentConverter.convertAndSave(orderFile, invoiceMap);
    }


    public void downloadFile(HttpServletResponse response,
                             ConvertType convertType) throws IOException {
        ShipmentConverter shipmentConverter = shipmentConverterFactory.get(convertType);
        String filePath = shipmentConverter.getShipmentFilePath();
        FileUtil.downloadFile(response, filePath);
    }
}
