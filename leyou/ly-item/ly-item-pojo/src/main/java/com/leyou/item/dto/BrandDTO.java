package com.leyou.item.dto;

import com.leyou.common.dto.BaseDTO;
import com.leyou.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BrandDTO extends BaseDTO {
    private Long id;
    private String name;
    private String image;
    private Character letter;

	public BrandDTO(BaseEntity entity) {
		super(entity);
	}

	/**
	 * 将PO集合转为DTO集合
	 * @param list PO对象的集合
	 * @param <T> PO的类型
	 * @return DTO集合
	 */
	public static <T extends BaseEntity> List<BrandDTO> convertEntityList(Collection<T> list){
		if(list == null){
			return Collections.emptyList();
		}
		return list.stream().map(BrandDTO::new).collect(Collectors.toList());
	}
}