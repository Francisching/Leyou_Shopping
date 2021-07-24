package com.leyou.search.web;

import com.leyou.search.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private IndexService indexService;

    /**
     * 接口，专被item-service请求，商品上下架时请求，当saleable为true，则表示上架新增，false表示下架
     * @param saleable
     * @param id
     * @return
     */
    @PutMapping("/{saleable}/{id}")
    public ResponseEntity<Void> modifyGoods(
            @PathVariable("saleable")Boolean saleable,
            @PathVariable("id")Long id){

        this.indexService.modifyGoods(saleable,id);

        return ResponseEntity.ok().build();
    }
}
