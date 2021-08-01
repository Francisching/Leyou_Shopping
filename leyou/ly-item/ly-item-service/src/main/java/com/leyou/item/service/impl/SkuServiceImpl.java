package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.entity.Sku;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.service.SkuService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //statement，本质此时就是方法
    private final String DEDUCT_STOCK_STATEMENT = "com.leyou.item.mapper.SkuMapper.minusStock";

    @Override
    public void minusStock(Map<Long,Integer> skuMap) {

        //sqlSession连接一次
        executeBatch(sqlSession -> {
            for (Map.Entry<Long, Integer> entry : skuMap.entrySet()) {
                // 准备参数
                Map<String,Object> param = new HashMap<>();
                param.put("id", entry.getKey());
                param.put("num", entry.getValue());
                // 编译statement，namespace.statementId
                sqlSession.update(DEDUCT_STOCK_STATEMENT, param);
            }
            // 刷新，一次批量执行
            sqlSession.flushStatements();
        });

    }
}
