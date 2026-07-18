package com.jiayi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("ums_member_signin")
public class UmsMemberSignin {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private LocalDate signinDate;
    private Integer pointsEarned;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
