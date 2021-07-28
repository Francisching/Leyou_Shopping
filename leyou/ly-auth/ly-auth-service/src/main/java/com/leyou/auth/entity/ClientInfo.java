package com.leyou.auth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.leyou.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_client_info")
public class ClientInfo extends BaseEntity {
    @TableId
    private Long id;
    @TableField("client_id")
    private String clientId;
    private String secret;
    private String info;
}