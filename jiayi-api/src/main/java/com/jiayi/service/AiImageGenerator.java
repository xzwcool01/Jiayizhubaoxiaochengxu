package com.jiayi.service;

public interface AiImageGenerator {
    String generate(String prompt, String userPhotoPath, String productImagePath, String outputDir);
}
