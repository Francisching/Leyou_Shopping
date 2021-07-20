package cn.itcast.flux.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class User {
    private String name;
    private int age;
}