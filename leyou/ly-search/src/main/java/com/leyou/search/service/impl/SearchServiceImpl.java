package com.leyou.search.service.impl;

import com.leyou.common.exception.LyException;
import com.leyou.search.client.ItemClient;
import com.leyou.search.dto.SearchParamDTO;
import com.leyou.search.entity.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import com.leyou.starter.elastic.entity.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ItemClient itemClient;

    @Override
    public Mono<List<String>> suggest(String key) {

        return this.goodsRepository.suggestBySingleField("suggestion", key);

        //return Mono.create(sink->sink.success(Arrays.asList("华为","花王","华硕","华擎")));
    }

    @Override
    public Mono<PageInfo<Goods>> pageQuery(SearchParamDTO searchParamDTO) {


        //武大对象
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //封装查询条件
        sourceBuilder.query(getQueryBuilder(searchParamDTO));

        //封装分页条件
        sourceBuilder.from(searchParamDTO.getFrom());
        sourceBuilder.size(searchParamDTO.getSize());


        return this.goodsRepository.queryBySourceBuilderForPageHighlight(sourceBuilder);
    }


    //封装查询条件
    private QueryBuilder getQueryBuilder(SearchParamDTO searchParamDTO) {
        //搜索条件
        String key = searchParamDTO.getKey();

        if (StringUtils.isEmpty(key)) {
            throw new LyException(400, "必须输入查询条件");
        }

        //创建boolean查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        //添加查询条件
        queryBuilder.must(QueryBuilders.matchQuery("title", key).operator(Operator.AND));

        Map<String, String> filters = searchParamDTO.getFilters();

        //不为空，表示要进行过滤
        if (!CollectionUtils.isEmpty(filters)) {


            filters.entrySet().forEach(entry -> {
                String specName = entry.getKey();
                String specValue = entry.getValue();

                if ("品牌".equals(specName)) {
                    specName = "brandId";
                    queryBuilder.filter(QueryBuilders.termQuery(specName, specValue));

                } else if ("分类".equals(specName)) {
                    specName = "categoryId";
                    queryBuilder.filter(QueryBuilders.termQuery(specName, specValue));
                } else {

                    //构建其他过滤规格参数的过滤条件
                    BoolQueryBuilder filterBool = QueryBuilders.boolQuery();

                    filterBool.must(QueryBuilders.termQuery("specs.name",specName));
                    filterBool.must(QueryBuilders.termQuery("specs.value",specValue));

                    QueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("specs", filterBool, ScoreMode.None);

                    queryBuilder.filter(nestedQueryBuilder);

                }

            });
        }

        return queryBuilder;
    }

    @Override
    public Mono<Map<String, List<?>>> filterQuery(SearchParamDTO searchParamDTO) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //添加查询条件
        sourceBuilder.query(getQueryBuilder(searchParamDTO));


        String brandAgg = "BRAND_AGG";

        String categoryAgg = "categoryAgg";

        //添加品牌聚合
        sourceBuilder.aggregation(AggregationBuilders.terms(brandAgg).field("brandId").size(100));

        //添加分类聚合
        sourceBuilder.aggregation(AggregationBuilders.terms(categoryAgg).field("categoryId").size(100));

        //条件规格参数聚合
        sourceBuilder.aggregation(
                //nested类型聚合
                AggregationBuilders.nested("specAgg", "specs")
                        .subAggregation(
                                //name聚合
                                AggregationBuilders.terms("nameAgg").field("specs.name").size(100)
                                        .subAggregation(
                                                //value聚合
                                                AggregationBuilders.terms("valueAgg").field("specs.value").size(100))
                        )
        );


        //查询聚合,把查询返回Mono<Aggregations>转换为Mono<Map<String,List<Object>>>
        return this.goodsRepository.aggregationBySourceBuilder(sourceBuilder).map(
                aggregations -> {
                    Map<String, List<?>> result = new LinkedHashMap<>();

                    //根据聚合名称获取聚合结果
                    Terms brandIdAgg = aggregations.get(brandAgg);

                    //从品牌聚合结果中获取到品牌id的集合
                    List<Long> brandIds = brandIdAgg
                            .getBuckets()
                            .stream()
                            .map(brandBucket -> brandBucket.getKeyAsNumber().longValue())
                            .collect(Collectors.toList());


                    //根据聚合名称获取聚合结果
                    Terms categoryIdAgg = aggregations.get(categoryAgg);

                    List<Long> categoryIds = categoryIdAgg.getBuckets().stream().map(categoryIdBucket -> ((Terms.Bucket) categoryIdBucket).getKeyAsNumber().longValue()).collect(Collectors.toList());

                    if (!CollectionUtils.isEmpty(categoryIds)) {
                        result.put("分类", this.itemClient.listCategoryByIds(categoryIds));
                    }

                    if (!CollectionUtils.isEmpty(brandIds)) {
                        result.put("品牌", this.itemClient.listBrandByIds(brandIds));
                    }

                    //获取nested聚合
                    Nested specAgg = aggregations.get("specAgg");

                    //获取nested聚合下的所有的聚合
                    Terms nameAgg = specAgg.getAggregations().get("nameAgg");

                    nameAgg.getBuckets().forEach(nameBucket -> {
                        String specName = nameBucket.getKeyAsString();

                        //获取nameBucket的所有子聚合
                        Terms valueAgg = nameBucket.getAggregations().get("valueAgg");
                        //获取nameBucket的子聚合中的所有的子内容
                        List<String> specValueList = valueAgg.getBuckets().stream().map(valueBucket -> ((Terms.Bucket) valueBucket).getKeyAsString()).collect(Collectors.toList());

                        result.put(specName, specValueList);

                    });

                    return result;
                }

        );

    }
}
