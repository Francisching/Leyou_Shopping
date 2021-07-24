package com.leyou.search.service;

import com.leyou.item.dto.SpuDTO;
import com.leyou.search.entity.Goods;
import reactor.core.publisher.Mono;

public interface IndexService {

    void loadData();

    Goods buildGoods(SpuDTO spuDTO);

    void modifyGoods(Boolean saleable, Long id);

    void addGoods(Long spuId);

    void deleteGoods(Long spuId);
}
