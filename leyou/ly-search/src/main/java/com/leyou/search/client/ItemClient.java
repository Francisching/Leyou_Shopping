package com.leyou.search.client;

import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("item-service")
public interface ItemClient {


    /**
     * spu分页查询以及其他动态查询
     *
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/goods/spu/page")
    PageDTO<SpuDTO> pageQuery(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "brandId", required = false) Long brandId,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "saleable", required = false) Boolean saleable
    );


    /**
     * 根据spuId查询对应的sku的集合
     *
     * @return
     */
    @GetMapping("/goods/sku/of/spu")
    List<SkuDTO> listSkuBySpuId(@RequestParam("id") Long id);


    /**
     * 根据商品id，查询商品对应的规格属性以及对应的值
     *
     * @param spuId
     * @param searching
     * @return
     */
    @GetMapping("/goods/spec/value")
    List<SpecParamDTO> listSpecWithValue(
            @RequestParam("id") Long spuId,
            @RequestParam(value = "searching", required = false) Boolean searching);


    /**
     * 根据id集合查询品牌对象
     *
     * @param ids
     * @return
     */
    @GetMapping("/brand/list")
    List<BrandDTO> listBrandByIds(
            @RequestParam("ids") List<Long> ids);


    /**
     * 根据id集合查询对应的分类对象集合
     *
     * @param ids
     * @return
     */
    @GetMapping("/category/list")
    List<CategoryDTO> listCategoryByIds(@RequestParam("ids") List<Long> ids);
}
