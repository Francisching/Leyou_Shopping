package com.leyou.search.web;

import com.leyou.search.dto.SearchParamDTO;
import com.leyou.search.entity.Goods;
import com.leyou.search.service.SearchService;
import com.leyou.starter.elastic.entity.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/goods")
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 自动补全
     * @param key
     * @return
     */
    @GetMapping("/suggestion")
    public Mono<List<String>> suggest(@RequestParam("key")String key){

        return this.searchService.suggest(key);
    }

    /**
     * 最基本的搜索查询处理
     * @param searchParamDTO
     * @return
     */
    @PostMapping("/list")
    public Mono<PageInfo<Goods>> pageQuery(@RequestBody SearchParamDTO searchParamDTO){

        return this.searchService.pageQuery(searchParamDTO);
    }


    /**
     * 过滤条件的获取
     * @param searchParamDTO
     * @return
     */
    @PostMapping("/filter")
    public Mono<Map<String,List<?>>> filterQuery(@RequestBody SearchParamDTO searchParamDTO){

        return this.searchService.filterQuery(searchParamDTO);
    }
}
