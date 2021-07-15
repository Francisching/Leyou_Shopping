package com.leyou.item.service;

import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.Category;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    /**
     * 测试，先，service，在web，最后再带入前端
     */
    @Test
    public void listCategoryByPid() {
        List<CategoryDTO> categoryDTOS = this.categoryService.listCategoryByPid(0L);
        categoryDTOS.forEach(System.out::println);
    }

    @Test
    public void listCategoryByBrand(){
        List<CategoryDTO> categoryDTOS = this.categoryService.listCategoryByBrand(8557L);

        categoryDTOS.forEach(System.out::println);
    }


}