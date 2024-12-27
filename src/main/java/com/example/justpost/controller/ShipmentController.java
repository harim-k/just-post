package com.example.justpost.controller;


import com.example.justpost.domain.post.ConvertType;
import com.example.justpost.service.shipment.ShipmentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/shipment")
public class ShipmentController {
    public final ShipmentService shipmentService;

    @PostMapping("/convert")
    public void convert(HttpServletResponse response,
                        @RequestParam("orderFile") MultipartFile orderFile,
                        @RequestParam("convertType") ConvertType convertType,
                        @RequestParam(value = "postFile", required = false) MultipartFile postFile,
                        @RequestParam(value = "shipmentString", required = false) String shipmentString,
                        Model model) {
        List<List<String>> shipmentValues = shipmentService.convertAndSave(
                orderFile, convertType, postFile, shipmentString);
        model.addAttribute("shipmentExcelData", shipmentValues);

        try {
            shipmentService.downloadFile(response, convertType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/download")
    public void downloadFile(HttpServletResponse response,
                             @RequestParam("convertType") ConvertType convertType) {
        try {
            shipmentService.downloadFile(response, convertType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}