package com.leyou.page.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsPageServiceTest {

    @Autowired
    private GoodsPageService goodsPageService;

    @Test
    public void loadSpuData() {
        this.goodsPageService.loadSpuData(2L);
    }

    @Test
    public void loadSpuDetailData() {
        this.goodsPageService.loadSpuDetailData(2L);
    }

    @Test
    public void loadSkuListData() {
        this.goodsPageService.loadSkuListData(2L);
    }

    @Test
    public void loadCategoriesData() {
        this.goodsPageService.loadCategoriesData(Arrays.asList(74L,75L,76L));
    }

    @Test
    public void loadBrandData() {
        this.goodsPageService.loadBrandData(8557L);
    }

    @Test
    public void loadSpecData() {
        this.goodsPageService.loadSpecData(76L);
    }
}