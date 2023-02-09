package com.example.justpost.controller;

import com.example.justpost.domain.ConvertType;
import com.example.justpost.domain.DownloadType;
import com.example.justpost.domain.Post;
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
@RequestMapping("/post")
public class PostController {
    public final PostService postService;

    @PostMapping("/convert")
    public String convert(@RequestParam("file") MultipartFile file,
                          @RequestParam("convertType") ConvertType convertType,
                          Model model) {
        List<Post> posts = postService.convertAndSave(file, convertType);
        model.addAttribute("postInfos", posts);
        return "/index";
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
