package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.entity.Spu;

public interface SpuService extends IService<Spu> {
    PageDTO<SpuDTO> pageQuery(Integer page, Integer rows, Long brandId, Long categoryId, Long id, Boolean saleable);

    SpuDTO querySpuById(Long id);
}
