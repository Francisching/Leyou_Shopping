package com.leyou.item.client;

import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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


    /**
     * 根据spuId查询对应的spu
     */
    @GetMapping("/goods/spu/{id}")
    SpuDTO querySpuById(@PathVariable("id") Long id);

    /**
     * 根据spuId查询对应的spuDetail对象
     *
     * @param id
     * @return
     */
    @GetMapping("/goods/spu/detail")
    SpuDetailDTO querySpuDetailById(@RequestParam("id") Long id);


    /**
     * 根据品牌id查询对应的品牌对象
     *
     * @param bid
     * @return
     */
    @GetMapping("/brand/{id}")
    BrandDTO queryBrandById(@PathVariable("id") Long bid);

    /**
     * 根据分类id查询规格参数组集合以及每个规格参数组对应的组内规格参数
     *
     * @param cid
     * @return
     */
    @GetMapping("/spec/list")
    List<SpecGroupDTO> listSpecGroupWithParams(@RequestParam("id") Long cid);

    /**
     * 根据sku的id集合查询对应的sku集合
     *
     * @param ids
     * @return
     */
    @GetMapping("/goods/sku/list")
    List<SkuDTO> listSkuByIds(@RequestParam("ids") List<Long> ids);

    /**
     * 批量减库存
     *
     * @param skuMap
     * @return
     */
    @PutMapping("/goods/sku/minus/stock")
    Void minusStock(
            @RequestBody Map<Long, Integer> skuMap);
}
