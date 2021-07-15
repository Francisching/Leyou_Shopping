package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.entity.Brand;
import com.leyou.item.entity.CategoryBrand;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.service.BrandService;
import com.leyou.item.service.CategoryBrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {

    @Autowired
    private CategoryBrandService categoryBrandService;

    @Override
    public PageDTO<BrandDTO> pageQuery(Integer page, Integer rows, String key) {
        //1，构建分页条件
        IPage<Brand> iPage = new Page<>(page,rows);

        //2，声明key是否参与查询
        Boolean condition = StringUtils.isNotBlank(key);

        //3,分页查询同时包含，条件查询
        page(iPage,new QueryWrapper<Brand>()
                .like(condition,"name",key)
                .or()
                .eq(condition,"letter",key)
        );

        //根据查询结果，封装分页响应结果，:::返回的是brandDTO集合，所以要做好转换
        return new PageDTO<>(iPage.getTotal(),iPage.getPages(),BrandDTO.convertEntityList(iPage.getRecords()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBrand(BrandDTO brandDTO, List<Long> cids) {
        //保存品牌
        Brand brand = brandDTO.toEntity(Brand.class);
        //先保存品牌，主键回显
       if (this.save(brand)){

           //根据传入的分类集合，以及品牌id，构建中间表对象集合
           saveCategoryBrand(cids, brand.getId());
       }


    }

    //中间表保存逻辑
    private void saveCategoryBrand(List<Long> cids, Long bid) {
        List<CategoryBrand> categoryBrands =
                cids
                        .stream()
                        .map(cid -> CategoryBrand.of(cid, bid))
                        .collect(Collectors.toList());

//           List<CategoryBrand> categoryBrands1 = new ArrayList<>();
//           for (Long cid : cids) {
//               CategoryBrand categoryBrand = CategoryBrand.of(cid, brand.getId());
//               categoryBrands1.add(categoryBrand);
//           }

        this.categoryBrandService.saveBatch(categoryBrands);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBrand(BrandDTO brandDTO, List<Long> cids) {

        Brand brand = brandDTO.toEntity(Brand.class);
        //如果品牌修改成功，则去操作中间表
        if (this.updateById(brand)){

            //中间逻辑的删除，然后重建
            //delete from tb_category_brand where brand_id = #{bid}
            this.categoryBrandService.remove(new QueryWrapper<CategoryBrand>().eq("brand_id",brand.getId()));

            //重建中间表逻辑
            saveCategoryBrand(cids, brand.getId());
        }
    }

    @Override
    public List<BrandDTO> listBrandByIds(List<Long> ids) {

        //查询返回entity集合===》转换为dto集合
        return BrandDTO.convertEntityList( this.listByIds(ids));
    }

    @Override
    public List<BrandDTO> queryBrandByCategory(Long cid) {

        //根据分类id去中间表查询中间表对象集合，并转换获取中间表对象中所有的品牌id
        List<Long> brandIds = this.categoryBrandService
                .query()
                .eq("category_id", cid)
                .list()
                .stream()
                .map(CategoryBrand::getBrandId)
                .collect(Collectors.toList());
        return this.listBrandByIds(brandIds);
    }

    @Override
    public BrandDTO queryBrandById(Long bid) {
        return new BrandDTO(this.getById(bid));
    }
}