package com.ssafy.sunin.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class FeedUpdate {
    @NotBlank
    private String id;
    private String content;
    @NotNull
    private Long userId;
    private List<String> hashtags;
//    private List<String> files;
}