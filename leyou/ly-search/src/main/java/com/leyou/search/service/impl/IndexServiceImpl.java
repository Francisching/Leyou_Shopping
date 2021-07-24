package com.leyou.search.service.impl;

import com.leyou.common.dto.PageDTO;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.search.entity.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.IndexService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IndexServiceImpl implements IndexService {
    //1,搜索，but缺数据，so，导入数据
    //2,数据来自于item-service,查询引入

    //向item-service发起web请求的feign的客户端
    private ItemClient itemClient;

    //向Elasticsearch服务发起操作请求的对象
    private GoodsRepository goodsRepository;

    public IndexServiceImpl(GoodsRepository goodsRepository, ItemClient itemClient) {
        this.itemClient = itemClient;
        this.goodsRepository = goodsRepository;
    }


    public void loadData() {

        //先清理索引库
        try {
            //删除索引库
            this.goodsRepository.deleteIndex();
        } catch (Throwable e) {
        }

        //索引库重建
        this.goodsRepository.createIndex("{\n" +
                "  \"settings\": {\n" +
                "    \"analysis\": {\n" +
                "      \"analyzer\": {\n" +
                "        \"my_pinyin\": {\n" +
                "          \"tokenizer\": \"ik_smart\",\n" +
                "          \"filter\": [\n" +
                "            \"py\"\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      \"filter\": {\n" +
                "        \"py\": {\n" +
                "\t\t  \"type\": \"pinyin\",\n" +
                "          \"keep_full_pinyin\": true,\n" +
                "          \"keep_joined_full_pinyin\": true,\n" +
                "          \"keep_original\": true,\n" +
                "          \"limit_first_letter_length\": 16,\n" +
                "          \"remove_duplicated_term\": true\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"mappings\": {\n" +
                "    \"properties\": {\n" +
                "      \"id\": {\n" +
                "        \"type\": \"keyword\"\n" +
                "      },\n" +
                "      \"suggestion\": {\n" +
                "        \"type\": \"completion\",\n" +
                "        \"analyzer\": \"my_pinyin\",\n" +
                "        \"search_analyzer\": \"ik_smart\"\n" +
                "      },\n" +
                "      \"title\":{\n" +
                "        \"type\": \"text\",\n" +
                "        \"analyzer\": \"my_pinyin\",\n" +
                "        \"search_analyzer\": \"ik_smart\"\n" +
                "      },\n" +
                "      \"image\":{\n" +
                "        \"type\": \"keyword\",\n" +
                "        \"index\": false\n" +
                "      },\n" +
                "      \"updateTime\":{\n" +
                "        \"type\": \"date\"\n" +
                "      },\n" +
                "      \"specs\":{\n" +
                "        \"type\": \"nested\",\n" +
                "        \"properties\": {\n" +
                "          \"name\":{\"type\": \"keyword\" },\n" +
                "          \"value\":{\"type\": \"keyword\" }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}");


        int page = 1;

        //死循环查询商品数据库，当没有查到或者到达最后一页停止
        while (true) {
            PageDTO<SpuDTO> spuDTOPageDTO = this.itemClient.pageQuery(page++, 50, null, null, null, null);

            //第一页就没有查询到
            if (null == spuDTOPageDTO || CollectionUtils.isEmpty(spuDTOPageDTO.getItems())) {
                break;
            }

            //获取数据
            List<Goods> goodsList = spuDTOPageDTO
                    .getItems()
                    .stream()
                    .map(this::buildGoods)
                    .collect(Collectors.toList());

            this.goodsRepository.saveAll(goodsList);


            //第一页查到了终止条件
            if (page > spuDTOPageDTO.getTotalPage()) {
                break;
            }
        }

    }


    //负责把spuDTO转换goods
    public Goods buildGoods(SpuDTO spuDTO) {

        Goods goods = new Goods();
        BeanUtils.copyProperties(spuDTO, goods);

        //categoryId
        goods.setCategoryId(spuDTO.getCid3());
        //updateTime更新时间为当前时间
        goods.setUpdateTime(new Date());

        //suggestion 中应该包含什么,1,品牌，2，分类，3，商品名称，4，品牌+分类，5，分类+品牌


        String s1 = StringUtils.substringBefore(spuDTO.getBrandName(), "（");

        //电脑办公/电脑配件/显示器====>显示器
        String s2 = StringUtils.substringAfterLast(spuDTO.getCategoryName(), "/");
        String s3 = spuDTO.getName();
        String s4 = s1 + s2;
        String s5 = s2 + s1;

        //suggestion
        goods.setSuggestion(Arrays.asList(s1, s2, s3, s4, s5));


        //根据spuId查询对应的sku集合
        List<SkuDTO> skuDTOS = this.itemClient.listSkuBySpuId(spuDTO.getId());

        long sold = 0;
        Set<Long> prices = new HashSet<>();
        for (SkuDTO skuDTO : skuDTOS) {
            skuDTO.getPrice();
            sold += skuDTO.getSold();
            prices.add(skuDTO.getPrice());
        }

        //多个图片中取一个
        //image,spu下至少要有一个sku，所以这里不会出错
        goods.setImage(StringUtils.substringBefore(skuDTOS.get(0).getImages(), ","));

        //sold
        goods.setSold(sold);

        //prices
        goods.setPrices(prices);

        //specs

        List<Map<String,Object>> specs = new ArrayList<>();

        //根据商品id以及可搜索条件查询规格参数集合
        List<SpecParamDTO> specParamDTOS = this.itemClient.listSpecWithValue(spuDTO.getId(),true);

        //迭代所有的可搜索规格参数，获取规格参数的名称以及规格参数的值
        specParamDTOS.forEach(specParamDTO -> {

            Map<String,Object> specMap = new HashMap<>();
            specMap.put("name",specParamDTO.getName());

            //如果字段是数值类型，并字段有区间，则应该，把具体的数值转换为区间存储
            specMap.put("value",chooseSegment(specParamDTO));
            specs.add(specMap);
        });

        goods.setSpecs(specs);

        return goods;
    }


    @Override
    public void addGoods(Long spuId) {
        //根据传入的spuId，查询对应的 spuDTO
        PageDTO<SpuDTO> spuDTOPageDTO = this.itemClient.pageQuery(1, 1, null, null, spuId, null);

        SpuDTO spuDTO = spuDTOPageDTO.getItems().get(0);

        //把查询到的spuDTO转换为goods然后保存新增
        this.goodsRepository.save(buildGoods(spuDTO));
    }

    @Override
    public void deleteGoods(Long spuId) {
        this.goodsRepository.deleteById(spuId);
    }


    @Override
    public void modifyGoods(Boolean saleable, Long id) {
        //上架新增
        if (saleable){

            addGoods(id);

        }else{//下架
            deleteGoods(id);
        }
    }




    private Object chooseSegment(SpecParamDTO p) {
        Object value = p.getValue();
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "其它";
        }
        if (!p.getNumeric() || StringUtils.isBlank(p.getSegments()) || value instanceof Collection) {
            return value;
        }
        double val = parseDouble(value.toString());
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = parseDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = parseDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }

}
