package com.leyou.item.web;

import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.service.SpecGroupService;
import com.leyou.item.service.SpecParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spec")
public class SpecController {

    @Autowired
    private SpecGroupService specGroupService;

    @Autowired
    private SpecParamService specParamService;

    /**
     * 根据分类id查询对应的规格参数组集合
     * @param cid
     * @return
     */
    @GetMapping("/groups/of/category")
    public ResponseEntity<List<SpecGroupDTO>> listSpecGroupByCategory(@RequestParam("id")Long cid){

        return ResponseEntity.ok(this.specGroupService.listSpecGroupByCategory(cid));
    }

    /**
     * 根据查询条件动态查询规格参数
     * @param cid
     * @param searching
     * @param gid
     * @return
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParamDTO>> listSpecParam(
            @RequestParam(value = "categoryId",required = false)Long cid,
            @RequestParam(value = "searching",required = false)Boolean searching,
            @RequestParam(value = "groupId",required = false) Long gid){

        return ResponseEntity.ok(this.specParamService.listSpecParam(gid,cid,searching));
    }

    /**
     * 规格参数组新增
     * @param specGroupDTO
     * @return
     */
    @PostMapping("/group")
    public ResponseEntity<Void> saveSpecGroup(
            @RequestBody SpecGroupDTO specGroupDTO){

        this.specGroupService.saveSpecGroup(specGroupDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 新增规格参数
     * @param specParamDTO
     * @return
     */
    @PostMapping("/param")
    public ResponseEntity<Void> saveSpecParam(
            @RequestBody SpecParamDTO specParamDTO){

        this.specParamService.saveSpecParam(specParamDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据分类id查询规格参数组集合以及每个规格参数组对应的组内规格参数
     * @param cid
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<SpecGroupDTO>> listSpecGroupWithParams(@RequestParam("id")Long cid){

        return ResponseEntity.ok(this.specGroupService.listSpecGroupWithParams(cid));
    }
}
