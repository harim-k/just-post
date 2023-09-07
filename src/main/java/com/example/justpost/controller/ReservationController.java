package com.example.justpost.controller;

import com.example.justpost.domain.post.ConvertType;
import com.example.justpost.domain.post.DownloadType;
import com.example.justpost.domain.post.Posts;
import com.example.justpost.service.post.PostService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@Controller
@AllArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {
    public final PostService postService;

    @PostMapping("/convert/{downloadType}")
    public void convert(HttpServletResponse response,
                        @RequestParam("orderFile") MultipartFile orderFile,
                        @RequestParam("convertType") ConvertType convertType,
                        @PathVariable("downloadType") DownloadType downloadType,
                        Model model) {
        Posts posts = postService.convertAndSave(orderFile, convertType);
        model.addAttribute("reservations", posts);
        try {
            postService.downloadFile(response, downloadType, convertType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
