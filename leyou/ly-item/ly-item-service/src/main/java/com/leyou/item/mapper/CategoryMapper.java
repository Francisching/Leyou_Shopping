package com.leyou.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyou.item.entity.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CategoryMapper extends BaseMapper<Category> {

    List<Category> listCategoryByBid(@Param("bid") Long bid);
}