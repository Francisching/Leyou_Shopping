package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.entity.Sku;

import java.util.List;

public interface SkuService extends IService<Sku> {
    List<SkuDTO> listSkuBySpuId(Long id);

    List<SkuDTO> listSkuByIds(List<Long> ids);
}
