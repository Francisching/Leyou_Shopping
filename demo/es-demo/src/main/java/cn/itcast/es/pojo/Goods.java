package cn.itcast.es.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class Goods {
    private Long id;
    private String name;
    private String title;
    private Long price;
}