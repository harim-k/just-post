package com.example.justpost.controller;

import com.example.justpost.domain.ConvertType;
import com.example.justpost.domain.DownloadType;
import com.example.justpost.domain.PostReservation;
import com.example.justpost.service.post.PostService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/postReservation")
public class PostReservationController {
    public final PostService postService;

    @PostMapping("/convert")
    public String convert(@RequestParam("orderFile") MultipartFile orderFile,
                          @RequestParam("convertType") ConvertType convertType,
                          Model model) {
        List<PostReservation> postReservations = postService.convertAndSave(orderFile, convertType);
        model.addAttribute("postReservations", postReservations);
        return "index";
    }


    @GetMapping("/download/{downloadType}")
    public void downloadFile(HttpServletResponse response,
                             @PathVariable("downloadType") DownloadType downloadType,
                             @RequestParam("convertType") ConvertType convertType) {
        try {
            postService.downloadFile(response, downloadType, convertType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
