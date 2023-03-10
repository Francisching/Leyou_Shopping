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
     * ????????????
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
     * ?????????????????????
     */
    @After
    public void close() throws IOException {
        client.close();
    }


    /**
     * ???????????????????????????
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
        // 1.??????????????????
        List<Goods> list = new ArrayList<>();
        list.add(new Goods(1L, "??????9", "??????9?????? ??????", 1499L));
        list.add(new Goods(2L, "?????? Galaxy A90", "?????? Galaxy A90 ?????? ?????? ??????5G ??????855", 3099L));
        list.add(new Goods(3L, "Sony WH-1000XM3", "Sony WH-1000XM3 ???????????? ??????", 2299L));
        list.add(new Goods(4L, "???????????????", "?????????????????????????????????????????????", 599L));
        // 2.??????BulkRequest??????
        BulkRequest bulkRequest = new BulkRequest();
        // 3.????????????IndexRequest?????????????????????BulkRequest???
        for (Goods goods : list) {
            bulkRequest.add(new IndexRequest("goods")
                    .id(goods.getId().toString())
                    .source(JSON.toJSONString(goods), XContentType.JSON)
            );
        }
        // 4.????????????
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

        //??????????????????
        SearchRequest searchRequest = new SearchRequest();


        //????????????????????????????????????source???????????????
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        sourceBuilder.fetchSource(new String[]{"id", "title", "price"}, null);

//        sourceBuilder.from(0);
//        sourceBuilder.size(1);

        sourceBuilder.sort(SortBuilders.fieldSort("id").order(SortOrder.DESC));

        //??????????????????
        sourceBuilder.query(QueryBuilders.matchQuery("title", "??????"));

        //??????????????????
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");

        sourceBuilder.highlighter(highlightBuilder);


        //??????????????????
        searchRequest.source(sourceBuilder);


        SearchResponse searchResponse = this.client.search(searchRequest, RequestOptions.DEFAULT);

        SearchHits hits = searchResponse.getHits();

        //???????????????
        long value = hits.getTotalHits().value;

        System.out.println("???????????? = " + value + " ???");

        SearchHit[] searchHits = hits.getHits();

        //????????????????????????
        int length = searchHits.length;

        System.out.println("??????????????? = " + length);

        for (SearchHit searchHit : searchHits) {
            float score = searchHit.getScore();
            Goods goods = JSON.parseObject(searchHit.getSourceAsString(), Goods.class);

            //????????????????????????????????????????????????????????????map???key??????????????????????????????value??????????????????
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();

            HighlightField highlightTitle = highlightFields.get("title");


            Text[] fragments = highlightTitle.getFragments();

            String highlightTitleResult = StringUtils.join(fragments);

            goods.setTitle(highlightTitleResult);


            System.out.println("?????????" + score + " ?????????" + goods);
        }

    }

    @Test
    public void testSuggest() throws IOException {

        SearchRequest searchRequest = new SearchRequest("goods");

        //??????????????????????????????
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //???????????????????????????
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

        System.out.println("??????????????????");
        // ?????????????????????????????????
        GetRequest request = new GetRequest("goods", "1");
        // ?????????????????????????????????50ms
        client.getAsync(request, RequestOptions.DEFAULT, new ActionListener<GetResponse>() {
            @Override
            public void onResponse(GetResponse response) {
                // ??????source
                String json = response.getSourceAsString();
                // ???json????????????
                Goods goods = JSON.parseObject(json, Goods.class);

                System.out.println("?????????????????????????????? " + goods);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });

        System.out.println("??????????????????????????????????????????");

        Thread.sleep(2000);
    }

    @Test
    public void testASyncQuery() throws InterruptedException {

        Mono<List<Goods>> listMono = this.aSyncQuery();

        //??????subscribe?????????????????????:::????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????web?????????webFlux
        listMono.subscribe(System.out::println);
        //Thread.sleep(10000);
    }


    //????????????mono??????????????????
    public Mono<List<Goods>> aSyncQuery() {
        SearchRequest searchRequest = new SearchRequest("goods");


        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        sourceBuilder.query(QueryBuilders.matchQuery("title", "??????"));

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

                    System.out.println("???????????????????????????");
                }
            });

        });


    }


}