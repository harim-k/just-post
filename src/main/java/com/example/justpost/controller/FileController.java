package com.example.justpost.controller;

import com.example.justpost.domain.utils.FileUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@AllArgsConstructor
public class FileController {

    @PostMapping("/merge")
    public void mergeExcelFiles(HttpServletResponse response,
                                @RequestParam("files") List<MultipartFile> files) {
        try {
            FileUtil.mergeExcelFiles(response, files);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
