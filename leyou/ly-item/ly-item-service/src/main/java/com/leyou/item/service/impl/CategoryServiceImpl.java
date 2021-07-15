package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.Category;
import com.leyou.item.entity.CategoryBrand;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.service.CategoryBrandService;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {


    private final CategoryBrandService categoryBrandService;

    public CategoryServiceImpl(CategoryBrandService categoryBrandService){
        this.categoryBrandService = categoryBrandService;
    }

    @Override
    public List<CategoryDTO> listCategoryByPid(Long pid) {

        //select * from tb_category where parent_id = #{pid}
        List<Category> categoryList = this.list(new QueryWrapper<Category>().eq("parent_id", pid));

        //List<Category>===>List<CategoryDTO>
        return  CategoryDTO.convertEntityList(categoryList);
    }

    @Override
    public CategoryDTO queryCategoryById(Long id) {

        return new CategoryDTO(this.getById(id));
    }

    @Override
    public List<CategoryDTO> listCategoryByIds(List<Long> ids) {

        //select * from tb_category where id in (x,x,x)
        return CategoryDTO.convertEntityList(this.listByIds(ids));
    }

    @Override
    public List<CategoryDTO> listCategoryByBrand(Long bid) {
//        List<Category> categories = baseMapper.listCategoryByBid(bid);
//        return CategoryDTO.convertEntityList(categories);

//        1,先访问中间表，根据品牌id，获取，分类id集合；
//        2,根据分类id的集合查询分类的集合

        List<Long> categoryIds = this.categoryBrandService
                .query()
                .eq("brand_id", bid)
                .list()
                .stream()
                .map(CategoryBrand::getCategoryId)
                .collect(Collectors.toList());

       return this.listCategoryByIds(categoryIds);
    }
}