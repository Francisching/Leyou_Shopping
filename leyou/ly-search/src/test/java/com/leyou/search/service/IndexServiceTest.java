package com.leyou.search.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IndexServiceTest {

    @Autowired
    private IndexService indexService;

    /**
     * 测试业务中不应该出现逻辑业务
     */
    @Test
    public void loadData() {
        this.indexService.loadData();

    }
}