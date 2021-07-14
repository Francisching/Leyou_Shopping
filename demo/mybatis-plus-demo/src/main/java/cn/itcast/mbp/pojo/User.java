package cn.itcast.mbp.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")
public class User {
    @TableId(value = "id",type = IdType.ID_WORKER) // 局部配置
    private Long id;
    private String userName;
    private Integer age;
    private String email;
}