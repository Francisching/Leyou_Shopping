package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.SpecGroup;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.service.SpecGroupService;
import com.leyou.item.service.SpecParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SpecGroupServiceImpl extends ServiceImpl<SpecGroupMapper, SpecGroup> implements SpecGroupService {

    @Autowired
    private SpecParamService specParamService;

    @Override
    public List<SpecGroupDTO> listSpecGroupByCategory(Long cid) {

        //根据分类id查询对应的规格参数组集合，entity转换为dto
        return SpecGroupDTO.convertEntityList(this.query().eq("category_id",cid).list());
    }

    @Override
    @Transactional
    public void saveSpecGroup(SpecGroupDTO specGroupDTO) {
        this.save(specGroupDTO.toEntity(SpecGroup.class));
    }


    @Override
    public List<SpecGroupDTO> listSpecGroupWithParams(Long cid) {

        //根分类id查询规格参数组集合
        List<SpecGroupDTO> specGroupDTOS = listSpecGroupByCategory(cid);

        //根据分类id查询当前分类下所有的规格参数
        List<SpecParamDTO> specParamDTOS = this.specParamService.listSpecParam(null, cid, null);

        //map的key就是groupId，value，就是相同groupId对应的规格参数集合
        Map<Long, List<SpecParamDTO>> specMap = specParamDTOS
                .stream()
                .collect(Collectors.groupingBy(SpecParamDTO::getGroupId));


        //遍历规格参数组集合，给每一个规格参数组对象，添加组内规格参数
        specGroupDTOS.forEach(specGroupDTO -> {
            //从分组map中取出对应的规格参数
            specGroupDTO.setParams(specMap.get(specGroupDTO.getId()));
        });

//        //根据规格参数组的id，查询组内参数
//        specGroupDTO.setParams(this
//                .specParamService
//                .listSpecParam(specGroupDTO.getId(),null,null));

        return specGroupDTOS;

        //TODO 1,少写代码
        //TODO 2,减少IO，以及数据库等的访问
        //TODO 3,加入缓存

    }
}
