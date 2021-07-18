package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.entity.Spu;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.service.BrandService;
import com.leyou.item.service.CategoryService;
import com.leyou.item.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpuServiceImpl extends ServiceImpl<SpuMapper, Spu> implements SpuService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Override
    public PageDTO<SpuDTO> pageQuery(Integer page, Integer rows, Long brandId, Long categoryId, Long id, Boolean saleable) {
        IPage<Spu> iPage = new Page<>(page,rows);

        //分页查询,条件查询
        page(iPage,new QueryWrapper<Spu>()
                .eq(brandId!=null,"brand_id",brandId)
                .eq(categoryId!=null,"cid3",categoryId)
                .eq(id!=null,"id",id)
                .eq(saleable!=null,"saleable",saleable)
        );

        //类型转换
        List<SpuDTO> spuDTOS = SpuDTO.convertEntityList(iPage.getRecords());

        spuDTOS.forEach(spuDTO -> {
            //根据品牌id查询对应的品牌对象，并获取名称
            spuDTO.setBrandName(this.brandService.queryBrandById(spuDTO.getBrandId()).getName());

            //根据分类的id集合查询分类集合，并且获取分类名称，已分隔符分割各个分类名称
            String categoryNames = this.categoryService
                    .listCategoryByIds(spuDTO.getCategoryIds())
                    .stream()
                    .map(CategoryDTO::getName)
                    .collect(Collectors.joining("/"));
            spuDTO.setCategoryName(categoryNames);
        });


        return new PageDTO<>(iPage.getTotal(),iPage.getPages(),spuDTOS);
    }

    @Override
    public SpuDTO querySpuById(Long id) {
        return new SpuDTO(this.getById(id));
    }
}
