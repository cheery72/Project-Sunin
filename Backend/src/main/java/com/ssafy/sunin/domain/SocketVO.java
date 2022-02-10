package com.ssafy.sunin.domain;

import lombok.Data;

// Data 어노테이션은 getter, setter를 자동 생성합니다.
@Data
// AllArgsConstructor 어노테이션은 생성자를 자동 생성합니다.
public class SocketVO {
    // 유저의 이름을 저장하기 위한 변수
    private String userName;

    // 메시지의 내용을 저장하기 위한 변
    private String content;

    public SocketVO(){

    }

    public SocketVO(String userName, String content){

        this.userName=userName;
        this.content=content;
    }


}