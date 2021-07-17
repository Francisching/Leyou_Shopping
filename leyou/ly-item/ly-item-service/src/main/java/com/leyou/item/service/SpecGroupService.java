package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.entity.SpecGroup;

import java.util.List;

public interface SpecGroupService  extends IService<SpecGroup> {
    List<SpecGroupDTO> listSpecGroupByCategory(Long cid);

    void saveSpecGroup(SpecGroupDTO specGroupDTO);

    List<SpecGroupDTO> listSpecGroupWithParams(Long cid);
}
