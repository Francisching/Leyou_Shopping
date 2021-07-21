package com.leyou.search.service;

import com.leyou.item.dto.SpuDTO;
import com.leyou.search.entity.Goods;

public interface IndexService {

    void loadData();

    Goods buildGoods(SpuDTO spuDTO);
}
