package com.example.sis.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlarmArgs {

    // 알람을 발생시킨 사람
    private Integer fromUserId;
    private Integer targetId; // postId 등등
}

//comment : @@씨가 새 코멘트를 작성했습니다 -> PostId, commentId
//@@외 2명이 새 코멘트를 작성했습니다. -> commentId, commentId
