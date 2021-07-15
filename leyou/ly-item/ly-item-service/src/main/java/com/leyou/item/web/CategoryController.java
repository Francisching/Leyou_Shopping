package com.leyou.item.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 根据分类的父id查询对应的子的集合
     * @param pid
     * @return
     */
    @GetMapping("/of/parent")
    public ResponseEntity<List<CategoryDTO>> listCategoryByPid(
            @RequestParam("pid")Long pid){

        return ResponseEntity.ok(this.categoryService.listCategoryByPid(pid));
    }

    /**
     * 根据分类id查询对应的分类信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> queryCategoryById(@PathVariable("id")Long id){

        return ResponseEntity.ok(this.categoryService.queryCategoryById(id));
    }

    /**
     * 根据id集合查询对应的分类对象集合
     * @param ids
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<CategoryDTO>> listCategoryByIds(@RequestParam("ids")List<Long> ids){

        return ResponseEntity.ok(this.categoryService.listCategoryByIds(ids));
    }

    /**
     * 根据品牌id，查询对应的分类集合
     * @param bid
     * @return
     */
    @GetMapping("/of/brand")
    public ResponseEntity<List<CategoryDTO>> listCategoryByBrand(@RequestParam("id")Long bid){

        return ResponseEntity.ok(this.categoryService.listCategoryByBrand(bid));
    }
}
