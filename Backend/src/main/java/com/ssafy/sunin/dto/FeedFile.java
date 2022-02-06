package com.ssafy.sunin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
public class FeedFile {
    @NotBlank
    private String id;
    private List<MultipartFile> files;
    @NotBlank
    private String userId;
}
