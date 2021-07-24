package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.dto.PageDTO;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.entity.Sku;
import com.leyou.item.entity.Spu;
import com.leyou.item.entity.SpuDetail;
import com.leyou.item.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private SpuService spuService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private SpuDetailService spuDetailService;

    @Autowired
    private SpecParamService specParamService;

    @Override
    public PageDTO<SpuDTO> pageQuery(Integer page, Integer rows, Long brandId, Long categoryId, Long id, Boolean saleable) {
        return this.spuService.pageQuery(page, rows, brandId, categoryId, id, saleable);
    }

    @Override
    public SpuDTO querySpuById(Long id) {
        return this.spuService.querySpuById(id);
    }

    @Override
    public List<SkuDTO> listSkuBySpuId(Long id) {
        return this.skuService.listSkuBySpuId(id);
    }

    @Override
    public List<SkuDTO> listSkuByIds(List<Long> ids) {
        return this.skuService.listSkuByIds(ids);
    }

    @Override
    public SpuDetailDTO querySpuDetailById(Long id) {
        return this.spuDetailService.querySpuDetailById(id);
    }

    /**
     * 商品查询 商品==spu + spuDetail + skus
     *
     * @param id
     * @return
     */
    @Override
    public SpuDTO queryGoodsById(Long id) {

        //只要是获取得到的对象就要判空，
        PageDTO<SpuDTO> spuDTOPageDTO = this.spuService.pageQuery(1, 1, null, null, id, null);

        //判断查询是否有效
        if (null != spuDTOPageDTO && !CollectionUtils.isEmpty(spuDTOPageDTO.getItems())) {

            SpuDTO spuDTO = spuDTOPageDTO.getItems().get(0);
            //根据spuId查询对应的spuDetail
            spuDTO.setSpuDetail(this.spuDetailService.querySpuDetailById(id));
            //根据spuId查询对应的sku集合
            spuDTO.setSkus(this.skuService.listSkuBySpuId(id));
            return spuDTO;

        } else {
            throw new LyException(204, "对应id数据不存在");
        }

    }

    @Override
    @Transactional
    public void addGoods(SpuDTO spuDTO) {

        Spu spu = spuDTO.toEntity(Spu.class);

        //保存spu，并主键回显
        this.spuService.save(spu);

        SpuDetail spuDetail = spuDTO.getSpuDetail().toEntity(SpuDetail.class);
        spuDetail.setSpuId(spu.getId());

        //保存spuDetail
        this.spuDetailService.save(spuDetail);

        //spu中取出skuDTO集合，转换成sku的集合并依次给每一个sku对象设置对应的spuId
        List<Sku> skus = spuDTO.getSkus().stream().map(skuDTO -> {
            Sku sku = skuDTO.toEntity(Sku.class);
            sku.setSpuId(spu.getId());
            return sku;
        }).collect(Collectors.toList());

        this.skuService.saveBatch(skus);

        //消息发送，实现，商品在数据库新增的同时，索引库也多个商品
        this.amqpTemplate.convertAndSend("jhj", "item.up", spu.getId());

//        log.debug(spuDTO.toString());
//        log.debug(spuDTO.getSpuDetail().toString());
//        spuDTO.getSkus().forEach(SkuDTO->log.debug(SkuDTO.toString()));
    }

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 商品下架
     *
     * @param spuId
     * @param saleable
     */
    @Override
    @Transactional
    public void modifySaleable(Long spuId, Boolean saleable) {
        //上下架处理，要同时对，spu和sku最关联处理
        Spu spu = new Spu();
        spu.setId(spuId);
        spu.setSaleable(saleable);

        this.spuService.updateById(spu);

        Sku sku = new Sku();
        sku.setSaleable(saleable);

        //update tb_sku set saleable = #{saleable} where spu_id = #{spuId}
        this.skuService.update(sku, new QueryWrapper<Sku>().eq("spu_id", spuId));

        //this.searchClient.modifyGoods(saleable,spuId);

        String routingKey = saleable ? "item.up" : "item.down";

        //消息发送，
        this.amqpTemplate.convertAndSend("jhj", routingKey, spuId);

    }

    @Override
    @Transactional
    public void update(SpuDTO spuDTO) {

        //判断，想要修改什么

        //要修改spu
        if (spuDTO.getId() != null) {

            this.spuService.updateById(spuDTO.toEntity(Spu.class));
        }

        //要修改spuDetail
        if (null != spuDTO.getSpuDetail()) {
            this.spuDetailService.updateById(spuDTO.getSpuDetail().toEntity(SpuDetail.class));
        }

        //要修改sku,三种可能，新增，删除，修改
        if (!CollectionUtils.isEmpty(spuDTO.getSkus())) {

            List<Sku> toADD = new ArrayList<>();
            List<Sku> toDelete = new ArrayList<>();
            List<Sku> toUpdate = new ArrayList<>();

            //遍历传递来的sku的集合，分离，
            spuDTO.getSkus().forEach(skuDTO -> {
                Sku sku = skuDTO.toEntity(Sku.class);
                //没有id，表示要新增
                if (sku.getId() == null) {
                    toADD.add(sku);
                    //saleable为空表示要修改
                } else if (sku.getSaleable() == null) {
                    toUpdate.add(sku);
                    //saleable不为空，表示要删除
                } else {
                    toDelete.add(sku);
                }
            });

            if (!CollectionUtils.isEmpty(toADD)) {
                this.skuService.saveBatch(toADD);
            }

            if (!CollectionUtils.isEmpty(toDelete)) {
                this.skuService.removeByIds(toDelete.stream().map(Sku::getId).collect(Collectors.toList()));
            }

            if (!CollectionUtils.isEmpty(toUpdate)) {
                this.skuService.updateBatchById(toUpdate);
            }

        }
    }

    @Override
    public List<SpecParamDTO> listSpecWithValue(Long spuId, Boolean searching) {

        //根据商品id查询对应的spu
        Spu spu = this.spuService.getById(spuId);

        if (null == spu) {
            throw new LyException(204, "此商品不存在");
        }

        //1,查询规格参数，
        List<SpecParamDTO> specParamDTOS = this.specParamService.listSpecParam(null, spu.getCid3(), searching);

        //判断查询条件下的规格参数是否存在
        if (CollectionUtils.isEmpty(specParamDTOS)) {

            //TODO,如果商品在，规格，品牌，分类，三个有一个缺失，说明数据库被恶意删除了，一定要报警
            throw new LyException(500, "商品对应的规格参数不存在");
        }

        //2,查询规格参数对应的属性值

        SpuDetail spuDetail = this.spuDetailService.getById(spuId);

        if (null == spuDetail) {
            throw new LyException(500, "对应的商品详情不存在");
        }

        //json字符串，1，java，map，2，js，对象,map的key，就是规格参数的id，就是specParamDTO.getId(),value就是对应的规格参数的值
        Map<Long, Object> specParamMap =
                JsonUtils.nativeRead(spuDetail.getSpecification(),
                        new TypeReference<Map<Long, Object>>() {
                        });

        //3，适配规格参数以及属性值
        specParamDTOS.forEach(specParamDTO -> specParamDTO.setValue(specParamMap.get(specParamDTO.getId())));


        return specParamDTOS;
    }
}
