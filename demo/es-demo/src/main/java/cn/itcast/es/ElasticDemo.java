package cn.itcast.es;

import cn.itcast.es.pojo.Goods;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Mono;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ElasticDemo {

    private RestHighLevelClient client;

    /**
     * 建立连接
     */
    @Before
    public void init() throws IOException {
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.206.99", 9200, "http")
                )
        );
    }

    /**
     * 关闭客户端连接
     */
    @After
    public void close() throws IOException {
        client.close();
    }


    /**
     * 索引库的创建和删除
     *
     * @throws IOException
     */
    @Test
    public void testCreateIndexes() throws IOException {

        try {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("goods");

            AcknowledgedResponse acknowledgedResponse = this.client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);

            System.out.println("acknowledgedResponse = " + acknowledgedResponse.isAcknowledged());
        } catch (Throwable e) {
        }


        CreateIndexRequest createIndexRequest = new CreateIndexRequest("goods");

        createIndexRequest.source("{\n" +
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
                "          \"type\": \"pinyin\",\n" +
                "          \"keep_full_pinyin\": false,\n" +
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
                "      \"name\": {\n" +
                "        \"type\": \"completion\",\n" +
                "        \"analyzer\": \"my_pinyin\",\n" +
                "        \"search_analyzer\": \"ik_smart\"\n" +
                "      },\n" +
                "      \"title\":{\n" +
                "        \"type\": \"text\",\n" +
                "        \"analyzer\": \"my_pinyin\",\n" +
                "        \"search_analyzer\": \"ik_smart\"\n" +
                "      },\n" +
                "      \"price\":{\n" +
                "        \"type\": \"long\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}", XContentType.JSON);

        CreateIndexResponse createIndexResponse = this.client.indices().create(createIndexRequest, RequestOptions.DEFAULT);

        System.out.println("createIndexResponse.isAcknowledged() = " + createIndexResponse.isAcknowledged());
    }


    @Test
    public void testBulkDocument() throws IOException {
        // 1.准备文档数据
        List<Goods> list = new ArrayList<>();
        list.add(new Goods(1L, "红米9", "红米9手机 数码", 1499L));
        list.add(new Goods(2L, "三星 Galaxy A90", "三星 Galaxy A90 手机 数码 疾速5G 骁龙855", 3099L));
        list.add(new Goods(3L, "Sony WH-1000XM3", "Sony WH-1000XM3 降噪耳机 数码", 2299L));
        list.add(new Goods(4L, "松下剃须刀", "松下电动剃须刀高转速磁悬浮马达", 599L));
        // 2.创建BulkRequest对象
        BulkRequest bulkRequest = new BulkRequest();
        // 3.创建多个IndexRequest对象，并添加到BulkRequest中
        for (Goods goods : list) {
            bulkRequest.add(new IndexRequest("goods")
                    .id(goods.getId().toString())
                    .source(JSON.toJSONString(goods), XContentType.JSON)
            );
        }
        // 4.发起请求
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);

        System.out.println("status: " + bulkResponse.status());
    }

    @Test
    public void testQueryWithId() throws IOException {

        GetRequest getRequest = new GetRequest("goods");
        getRequest.id("1");

        GetResponse getResponse = this.client.get(getRequest, RequestOptions.DEFAULT);

        String goodsJson = getResponse.getSourceAsString();

        Goods goods = JSON.parseObject(goodsJson, Goods.class);

        System.out.println("goods = " + goods);
    }


    @Test
    public void testQueryWithParam() throws IOException {

        //查询请求对象
        SearchRequest searchRequest = new SearchRequest();


        //武大，查询，分页，高亮，source过滤，排序
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        sourceBuilder.fetchSource(new String[]{"id", "title", "price"}, null);

//        sourceBuilder.from(0);
//        sourceBuilder.size(1);

        sourceBuilder.sort(SortBuilders.fieldSort("id").order(SortOrder.DESC));

        //构建分词查询
        sourceBuilder.query(QueryBuilders.matchQuery("title", "数码"));

        //封装高亮条件
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");

        sourceBuilder.highlighter(highlightBuilder);


        //添加搜索条件
        searchRequest.source(sourceBuilder);


        SearchResponse searchResponse = this.client.search(searchRequest, RequestOptions.DEFAULT);

        SearchHits hits = searchResponse.getHits();

        //命中的数量
        long value = hits.getTotalHits().value;

        System.out.println("命中元素 = " + value + " 个");

        SearchHit[] searchHits = hits.getHits();

        //实际查询到的数量
        int length = searchHits.length;

        System.out.println("实际查询到 = " + length);

        for (SearchHit searchHit : searchHits) {
            float score = searchHit.getScore();
            Goods goods = JSON.parseObject(searchHit.getSourceAsString(), Goods.class);

            //获取高亮结果，高亮是可以做多字段的，所以map的key，就是高亮字段名称，value就是高亮信息
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();

            HighlightField highlightTitle = highlightFields.get("title");


            Text[] fragments = highlightTitle.getFragments();

            String highlightTitleResult = StringUtils.join(fragments);

            goods.setTitle(highlightTitleResult);


            System.out.println("得分：" + score + " 对象：" + goods);
        }

    }

    @Test
    public void testSuggest() throws IOException {

        SearchRequest searchRequest = new SearchRequest("goods");

        //武大，六大，自动补全
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //构建自动提示，对象
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("heima", SuggestBuilders.completionSuggestion("name").prefix("s").size(30));

        sourceBuilder.suggest(suggestBuilder);


        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = this.client.search(searchRequest, RequestOptions.DEFAULT);

        Suggest suggest = searchResponse.getSuggest();

        suggest.getSuggestion("heima").forEach(sgt -> {
            sgt.getOptions().forEach(sgtResult -> {
                String suggestResult = sgtResult.getText().toString();

                System.out.println("suggestResult = " + suggestResult);

            });
        });

    }


    @Test
    public void testGetDocumentByIdAsync() throws IOException, InterruptedException {

        System.out.println("准备开始查询");
        // 准备一个查询文档的请求
        GetRequest request = new GetRequest("goods", "1");
        // 异步查询一个文档，耗时50ms
        client.getAsync(request, RequestOptions.DEFAULT, new ActionListener<GetResponse>() {
            @Override
            public void onResponse(GetResponse response) {
                // 获取source
                String json = response.getSourceAsString();
                // 把json反序列化
                Goods goods = JSON.parseObject(json, Goods.class);

                System.out.println("查询结束，得到结果： " + goods);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });

        System.out.println("请求已经发出，等待执行结果！");

        Thread.sleep(2000);
    }

    @Test
    public void testASyncQuery() throws InterruptedException {

        Mono<List<Goods>> listMono = this.aSyncQuery();

        //使用subscribe订阅返回结果，:::测试案例有订阅，开发中坚决不能写订阅，如果写了订阅相当于破坏了异步逻辑，项目中的订阅，是web程序，webFlux
        listMono.subscribe(System.out::println);
        //Thread.sleep(10000);
    }


    //手动使用mono封装返回结果
    public Mono<List<Goods>> aSyncQuery() {
        SearchRequest searchRequest = new SearchRequest("goods");


        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        sourceBuilder.query(QueryBuilders.matchQuery("title", "数码"));

        searchRequest.source(sourceBuilder);


        return Mono.create(sink -> {
            this.client.searchAsync(searchRequest, RequestOptions.DEFAULT, new ActionListener<SearchResponse>() {
                @Override
                public void onResponse(SearchResponse searchResponse) {

                    SearchHit[] hits = searchResponse.getHits().getHits();


//                for (SearchHit hit : hits) {
//                    Goods goods = JSON.parseObject(hit.getSourceAsString(), Goods.class);
//                }

                    List<Goods> goodsList = Stream
                            .of(hits)
                            .map(hit -> JSON.parseObject(hit.getSourceAsString(), Goods.class))
                            .collect(Collectors.toList());

                    sink.success(goodsList);
                }

                @Override
                public void onFailure(Exception e) {

                    System.out.println("对比起，不对外服务");
                }
            });

        });


    }


}