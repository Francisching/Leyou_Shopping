package com.leyou.search.service;

import com.leyou.search.dto.SearchParamDTO;
import com.leyou.search.entity.Goods;
import com.leyou.starter.elastic.entity.PageInfo;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface SearchService {
    Mono<List<String>> suggest(String key);

    Mono<PageInfo<Goods>> pageQuery(SearchParamDTO searchParamDTO);

    Mono<Map<String, List<?>>> filterQuery(SearchParamDTO searchParamDTO);
}
