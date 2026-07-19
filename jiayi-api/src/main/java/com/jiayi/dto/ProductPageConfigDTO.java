package com.jiayi.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductPageConfigDTO {
    private Integer aiEnabled;
    private Integer videoEnabled;
    private String videoCover;
    private String videoUrl;
    private Integer galleryEnabled;
    private List<String> galleryImages;
    private Integer disclaimerEnabled;
    private String disclaimerText;
    private String disclaimerColor;
}
