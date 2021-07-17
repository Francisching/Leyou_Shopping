package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.SpecParam;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.service.SpecParamService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecParamServiceImpl extends ServiceImpl<SpecParamMapper, SpecParam> implements SpecParamService {
    @Override
    public List<SpecParamDTO> listSpecParam(Long gid, Long cid, Boolean searching) {

        //根据查询条件动态查询规格参数
        List<SpecParam> specParams = this.query()
                .eq(gid!=null,"group_id", gid)
                .eq(cid!=null,"category_id", cid)
                .eq(searching!=null,"searching", searching)
                .list();

        //根据规格参数组id，查询组内参数，entity集合转换为dto集合
        return SpecParamDTO.convertEntityList(specParams);
    }

    @Override
    public void saveSpecParam(SpecParamDTO specParamDTO) {

        SpecParam specParam = specParamDTO.toEntity(SpecParam.class);

        this.save(specParam);

    }
}
