package com.example.justpost.controller;


import com.example.justpost.domain.ConvertType;
import com.example.justpost.service.afterPost.AfterPostService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/afterPost")
public class AfterPostController {
    public final AfterPostService afterPostService;

    @PostMapping("/convert")
    public String convert(@RequestParam("orderFile") MultipartFile orderFile,
                          @RequestParam("convertType") ConvertType convertType,
                          @RequestParam(value = "postFile", required = false) MultipartFile postFile,
                          @RequestParam(value = "afterPostString", required = false) String afterPostString,
                          Model model) {
        List<List<String>> afterPostValues = afterPostService.convertAndSave(
                orderFile, convertType, postFile, afterPostString);
        model.addAttribute("afterPostExcelData", afterPostValues);
        return "index";
    }

    @GetMapping("/download")
    public void downloadFile(HttpServletResponse response,
                             @RequestParam("convertType") ConvertType convertType) {
        try {
            afterPostService.downloadFile(response, convertType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}