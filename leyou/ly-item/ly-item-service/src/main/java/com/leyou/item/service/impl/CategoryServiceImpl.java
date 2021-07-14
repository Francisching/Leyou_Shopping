package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.Category;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Override
    public List<CategoryDTO> listCategoryByPid(Long pid) {

        //select * from tb_category where parent_id = #{pid}
        List<Category> categoryList = this.list(new QueryWrapper<Category>().eq("parent_id", pid));

        //List<Category>===>List<CategoryDTO>
        return  CategoryDTO.convertEntityList(categoryList);
    }
}