package com.ssafy.sunin.payload.request.feed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedFile {
    @NotBlank
    private String id;
    private List<MultipartFile> files;
    @NotNull
    private Long userId;
}
