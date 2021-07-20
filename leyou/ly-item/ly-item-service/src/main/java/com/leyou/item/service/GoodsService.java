package com.leyou.item.service;

import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;

import java.util.List;

public interface GoodsService {
    PageDTO<SpuDTO> pageQuery(Integer page, Integer rows, Long brandId, Long categoryId, Long id, Boolean saleable);

    SpuDTO querySpuById(Long id);

    List<SkuDTO> listSkuBySpuId(Long id);

    List<SkuDTO> listSkuByIds(List<Long> ids);

    SpuDetailDTO querySpuDetailById(Long id);

    SpuDTO queryGoodsById(Long id);

    void addGoods(SpuDTO spuDTO);

    void modifySaleable(Long spuId, Boolean saleable);

    void update(SpuDTO spuDTO);

    List<SpecParamDTO> listSpecWithValue(Long spuId, Boolean searching);
}
