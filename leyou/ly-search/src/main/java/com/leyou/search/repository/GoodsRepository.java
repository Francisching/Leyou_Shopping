package com.leyou.search.repository;

import com.leyou.search.entity.Goods;
import com.leyou.starter.elastic.repository.Repository;

//用来访问elasticSearch
public interface GoodsRepository extends Repository<Goods,Long> {
}
