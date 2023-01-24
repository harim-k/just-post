package com.example.justpost.controller;

import com.example.justpost.domain.utils.FileUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@AllArgsConstructor
public class FileController {

    @GetMapping("/merge")
    public void mergeExcelFiles(@RequestParam("file1") MultipartFile file1,
                                @RequestParam("file2") MultipartFile file2) {
        try {
            FileUtil.mergeExcelFiles(file1, file2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
