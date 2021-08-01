package com.leyou.item.web;

import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;


    /**
     * spu分页查询以及其他动态查询
     *
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageDTO<SpuDTO>> pageQuery(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "brandId", required = false) Long brandId,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "saleable", required = false) Boolean saleable
    ) {

        return ResponseEntity.ok(this.goodsService.pageQuery(page, rows, brandId, categoryId, id, saleable));
    }

    /**
     * 根据spuId查询对应的spu
     */
    @GetMapping("/spu/{id}")
    public ResponseEntity<SpuDTO> querySpuById(@PathVariable("id") Long id) {

        return ResponseEntity.ok(this.goodsService.querySpuById(id));
    }


    /**
     * 根据spuId查询对应的sku的集合
     *
     * @return
     */
    @GetMapping("/sku/of/spu")
    public ResponseEntity<List<SkuDTO>> listSkuBySpuId(
            @RequestParam("id") Long id) {

        return ResponseEntity.ok(this.goodsService.listSkuBySpuId(id));
    }

    /**
     * 根据sku的id集合查询对应的sku集合
     *
     * @param ids
     * @return
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<SkuDTO>> listSkuByIds(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(this.goodsService.listSkuByIds(ids));
    }

    /**
     * 根据spuId查询对应的spuDetail对象
     *
     * @param id
     * @return
     */
    @GetMapping("/spu/detail")
    public ResponseEntity<SpuDetailDTO> querySpuDetailById(@RequestParam("id") Long id) {

        return ResponseEntity.ok(this.goodsService.querySpuDetailById(id));
    }

    /**
     * 商品查询，spu，spuDetail，List<sku>
     */
    @GetMapping("/{id}")
    public ResponseEntity<SpuDTO> queryGoodsById(@PathVariable("id") Long id) {

        return ResponseEntity.ok(this.goodsService.queryGoodsById(id));
    }

    /**
     * 商品新增，包含spu，sku以及spuDetail
     *
     * @param spuDTO
     * @return
     */
    @PostMapping("/spu")
    public ResponseEntity<Void> addGoods(
            @RequestBody SpuDTO spuDTO) {

        this.goodsService.addGoods(spuDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/saleable")
    public ResponseEntity<Void> modifySaleable(
            @RequestParam("id") Long spuId,
            @RequestParam("saleable") Boolean saleable) {

        this.goodsService.modifySaleable(spuId, saleable);
        return ResponseEntity.ok().build();
    }

    /**
     * 商品修改，可能包含spu，sku以及spuDetail
     *
     * @param spuDTO
     * @return
     */
    @PutMapping("/spu")
    public ResponseEntity<Void> update(
            @RequestBody SpuDTO spuDTO) {

        this.goodsService.update(spuDTO);

        return ResponseEntity.ok().build();
    }

    /**
     * 根据商品id，查询商品对应的规格属性以及对应的值
     *
     * @param spuId
     * @param searching
     * @return
     */
    @GetMapping("/spec/value")
    public ResponseEntity<List<SpecParamDTO>> listSpecWithValue(
            @RequestParam("id") Long spuId,
            @RequestParam(value = "searching", required = false) Boolean searching) {
        return ResponseEntity.ok(this.goodsService.listSpecWithValue(spuId,searching));
    }

    /**
     * 批量减库存
     * @param skuMap
     * @return
     */
    @PutMapping("/sku/minus/stock")
    public ResponseEntity<Void> minusStock(
            @RequestBody Map<Long,Integer> skuMap){

        this.goodsService.minusStock(skuMap);
        return ResponseEntity.ok().build();
    }

}
