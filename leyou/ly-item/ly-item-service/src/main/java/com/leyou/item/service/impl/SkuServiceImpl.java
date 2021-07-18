package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.entity.Sku;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.service.SkuService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku> implements SkuService {
    @Override
    public List<SkuDTO> listSkuBySpuId(Long id) {

        return SkuDTO.convertEntityList(this.query().eq("spu_id", id).list());
    }

    @Override
    public List<SkuDTO> listSkuByIds(List<Long> ids) {
        return SkuDTO.convertEntityList(this.listByIds(ids));
    }
}
