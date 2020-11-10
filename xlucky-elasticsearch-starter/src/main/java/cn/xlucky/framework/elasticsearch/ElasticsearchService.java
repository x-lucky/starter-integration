package cn.xlucky.framework.elasticsearch;

import cn.xlucky.framework.elasticsearch.support.QueryMap;
import cn.xlucky.framework.elasticsearch.exception.DocumentNotFoundException;
import cn.xlucky.framework.elasticsearch.exception.VersionConflictException;
import com.alibaba.fastjson.JSON;
import io.searchbox.action.BulkableAction;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.client.http.JestHttpClient;
import io.searchbox.core.BulkResult;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.core.SearchResult;
import io.searchbox.core.UpdateByQueryResult;
import io.searchbox.core.Index.Builder;
import io.searchbox.indices.script.ScriptLanguage;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * es封装服务类
 * @author xlucky
 * @date 2020/5/29
 * @version 1.0.0
 */
public class ElasticsearchService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchService.class);
    private static final String JSON_KEY_RESULT = "result";
    private static final String JSON_VALUE_RESULT_NOT_FOUND = "not_found";
    private static final String JSON_KEY_FOUND = "found";
    @Resource
    private JestHttpClient jestHttpClient;

    public ElasticsearchService() {
    }

    public Index buildIndexAction(String indexName, String indexType, Object indexData, Object indexId) {
        return this.buildIndexAction(indexName, indexType, indexData, indexId, null);
    }

    public Index buildIndexAction(String indexName, String indexType, Object indexData, Object indexId, Map<String, String> restParameters) {
        Builder indexBuilder = new Builder(indexData).index(indexName).type(indexType);
        if (indexId != null) {
            indexBuilder.id(indexId.toString());
        }

        if (!mapIsEmpty(restParameters)) {
            Iterator var7 = restParameters.entrySet().iterator();

            while(var7.hasNext()) {
                Entry<String, String> parameter = (Entry)var7.next();
                indexBuilder.setParameter(parameter.getKey(), parameter.getValue());
            }
        }

        return indexBuilder.build();
    }

    public Delete buildDeleteAction(String indexName, String indexType, Object indexId) {
        return this.buildDeleteAction(indexName, indexType, indexId, null);
    }

    public Delete buildDeleteAction(String indexName, String indexType, Object indexId, Map<String, String> restParameters) {
        io.searchbox.core.Delete.Builder deleteBuilder = (new io.searchbox.core.Delete.Builder(indexId.toString()).index(indexName)).type(indexType);
        if (!mapIsEmpty(restParameters)) {
            Iterator var6 = restParameters.entrySet().iterator();

            while(var6.hasNext()) {
                Entry<String, String> parameter = (Entry)var6.next();
                deleteBuilder.setParameter(parameter.getKey(), parameter.getValue());
            }
        }

        return deleteBuilder.build();
    }

    public void bulkActions(List<BulkableAction<?>> actions) throws IOException {
        this.bulkActions(actions, null);
    }

    public void bulkActions(List<BulkableAction<?>> actions, Map<String, String> restParameters) throws IOException {
        io.searchbox.core.Bulk.Builder bulkBuilder = new io.searchbox.core.Bulk.Builder();
        Iterator var4 = actions.iterator();

        BulkableAction action;
        while(var4.hasNext()) {
            action = (BulkableAction)var4.next();
            bulkBuilder.addAction(action);
        }

        if (!mapIsEmpty(restParameters)) {
            var4 = restParameters.entrySet().iterator();

            while(var4.hasNext()) {
                Entry<String, String> parameter = (Entry)var4.next();
                bulkBuilder.setParameter(parameter.getKey(), parameter.getValue());
            }
        }

        try {
            BulkResult bulkResult = this.jestHttpClient.execute(bulkBuilder.build());
            if (!bulkResult.isSucceeded()) {
                throw new IOException(bulkResult.getJsonString());
            } else {
                action = actions.get(0);
                LOGGER.info("Succeeded to operate documents, first index: {}, first type: {}, bulk size: {}", new Object[]{action.getIndex(), action.getType(), actions.size()});
            }
        } catch (IOException var6) {
            action = actions.get(0);
            LOGGER.error(String.format("Failed to operate documents, first index: %s, first type: %s, bulk size: %s", action.getIndex(), action.getType(), actions.size()), var6);
            throw var6;
        }
    }

    public void index(String indexName, String indexType, Object indexData, Object indexId) throws IOException {
        this.index(indexName, indexType, indexData, indexId, null);
    }

    public void index(String indexName, String indexType, Object indexData, Object indexId, Map<String, String> restParameters) throws IOException {
        try {
            JestResult jestResult = this.jestHttpClient.execute(this.buildIndexAction(indexName, indexType, indexData, indexId, restParameters));
            if (!jestResult.isSucceeded()) {
                throw new IOException(jestResult.getJsonString());
            } else {
                LOGGER.info("Succeeded to index document, index: {}, type: {}, id: {}, data:\n{}", new Object[]{indexName, indexType, indexId, JSON.toJSONString(indexData)});
            }
        } catch (IOException var7) {
            LOGGER.error(String.format("Failed to index document, index: %s, type: %s, id: %s, data:\n%s", indexName, indexType, indexId, JSON.toJSONString(indexData)), var7);
            throw var7;
        }
    }

    public void indexAsync(String indexName, String indexType, Object indexData, Object indexId) {
        this.indexAsync(indexName, indexType, indexData, indexId, null);
    }

    public void indexAsync(final String indexName, final String indexType, final Object indexData, final Object indexId, Map<String, String> restParameters) {
        io.searchbox.core.Bulk.Builder bulkBuilder = (new io.searchbox.core.Bulk.Builder()).addAction(this.buildIndexAction(indexName, indexType, indexData, indexId));
        if (!mapIsEmpty(restParameters)) {
            Iterator var7 = restParameters.entrySet().iterator();

            while(var7.hasNext()) {
                Entry<String, String> parameter = (Entry)var7.next();
                bulkBuilder.setParameter(parameter.getKey(), parameter.getValue());
            }
        }

        this.jestHttpClient.executeAsync(bulkBuilder.build(), new JestResultHandler<JestResult>() {
            @Override
            public void completed(JestResult jestResult) {
                if (jestResult.isSucceeded()) {
                    ElasticsearchService.LOGGER.info("Succeeded to index document, index: {}, type: {}, id: {}, data:\n{}", new Object[]{indexName, indexType, indexId, JSON.toJSONString(indexData)});
                } else {
                    ElasticsearchService.LOGGER.error("Failed to index document, index: {}, type: {}, id: {}, data:\n{}\n, and message:{}", new Object[]{indexName, indexType, indexId, JSON.toJSONString(indexData), jestResult.getJsonString()});
                }

            }
            @Override
            public void failed(Exception ex) {
                ElasticsearchService.LOGGER.error(String.format("Failed to index document, index: %s, type: %s, id: %s, data:\n%s", indexName, indexType, indexId, JSON.toJSONString(indexData)), ex);
            }
        });
    }

    public void index(String indexName, String indexType, List<Object> indexDataList, String indexIdField) throws IOException {
        this.index(indexName, indexType, indexDataList, indexIdField, null);
    }

    public void index(String indexName, String indexType, List<Object> indexDataList, String indexIdField, Map<String, String> restParameters) throws IOException {
        io.searchbox.core.Bulk.Builder bulkBuilder = new io.searchbox.core.Bulk.Builder();

        Iterator var7;
        Object indexData;
        Object indexId;
        for(var7 = indexDataList.iterator(); var7.hasNext(); bulkBuilder.addAction(this.buildIndexAction(indexName, indexType, indexData, indexId, restParameters))) {
            indexData = var7.next();
            indexId = null;
            if (!StringUtils.isBlank(indexIdField)) {
                indexId = reflectUtilGetter(indexData, indexIdField);
            }
        }

        if (!mapIsEmpty(restParameters)) {
            var7 = restParameters.entrySet().iterator();

            while(var7.hasNext()) {
                Entry<String, String> parameter = (Entry)var7.next();
                bulkBuilder.setParameter(parameter.getKey(), parameter.getValue());
            }
        }

        try {
            BulkResult bulkResult = this.jestHttpClient.execute(bulkBuilder.build());
            if (!bulkResult.isSucceeded()) {
                throw new IOException(bulkResult.getJsonString());
            } else {
                LOGGER.info("Succeeded to index documents, index: {}, type: {}, id field: {}, data size: {}", new Object[]{indexName, indexType, indexIdField, indexDataList.size()});
            }
        } catch (IOException var10) {
            LOGGER.error(String.format("Failed to index documents, index: %s, type: %s, id field: %s, data size: %s", indexName, indexType, indexIdField, indexDataList.size()), var10);
            throw var10;
        }
    }

    public void updateByQuery(String indexName, String indexType, Map<String, Object> queries, Map<String, Object> sources) throws IOException, VersionConflictException {
        this.updateByQuery(indexName, indexType, queries, sources, null);
    }

    public void updateByQuery(String indexName, String indexType, Map<String, Object> queries, Map<String, Object> sources, Map<String, String> restParameters) throws IOException, VersionConflictException {
        Map<String, Object> payload = new HashMap(4);
        List<Map<String, Object>> terms = new ArrayList(queries.size());
        Iterator var8 = queries.entrySet().iterator();

        while(var8.hasNext()) {
            Entry<String, Object> entry = (Entry)var8.next();
            terms.add(QueryMap.buildMap(entry.getKey(), entry.getValue()));
        }

        payload.put("query", Collections.singletonMap("bool", Collections.singletonMap("must", terms)));
        Map<String, Object> script = new HashMap(6);
        StringBuilder scriptSource = new StringBuilder();
        Iterator var10 = sources.entrySet().iterator();

        while(var10.hasNext()) {
            Entry<String, Object> entry = (Entry)var10.next();
            scriptSource.append("ctx._source.").append(entry.getKey()).append(' ').append('=').append(' ').append("params.").append(entry.getKey()).append(';');
        }

        script.put("source", scriptSource.toString());
        script.put("params", sources);
        script.put("lang", ScriptLanguage.PAINLESS.pathParameterName);
        payload.put("script", script);
        io.searchbox.core.UpdateByQuery.Builder updateByQueryBuilder = ((new io.searchbox.core.UpdateByQuery.Builder(payload)).addIndex(indexName)).addType(indexType);
        if (!mapIsEmpty(restParameters)) {
            Iterator var17 = restParameters.entrySet().iterator();

            while(var17.hasNext()) {
                Entry<String, String> parameter = (Entry)var17.next();
                updateByQueryBuilder.setParameter(parameter.getKey(), parameter.getValue());
            }
        }

        try {
            UpdateByQueryResult updateByQueryResult = this.jestHttpClient.execute(updateByQueryBuilder.build());
            if (!updateByQueryResult.isSucceeded()) {
                if (updateByQueryResult.getConflictsCount() != 0L) {
                    throw new VersionConflictException(updateByQueryResult.getJsonString());
                } else {
                    throw new IOException(updateByQueryResult.getJsonString());
                }
            } else {
                LOGGER.info("Succeeded to update documents by query, index: {}, type: {}, payload:\n{}", new Object[]{indexName, indexType, JSON.toJSONString(payload)});
            }
        } catch (IOException var13) {
            LOGGER.error(String.format("Failed to update documents by query, index: %s, type: %s, payload:\n%s", indexName, indexType, JSON.toJSONString(payload)), var13);
            throw var13;
        }
    }

    public void delete(String indexName, String indexType, Object indexId) throws IOException, DocumentNotFoundException {
        this.delete(indexName, indexType, indexId, null);
    }

    public void delete(String indexName, String indexType, Object indexId, Map<String, String> restParameters) throws IOException, DocumentNotFoundException {
        try {
            JestResult jestResult = this.jestHttpClient.execute(this.buildDeleteAction(indexName, indexType, indexId, restParameters));
            if (!jestResult.isSucceeded()) {
                if ("not_found".equals(jestResult.getJsonObject().get("result").getAsString())) {
                    throw new DocumentNotFoundException(jestResult.getJsonString());
                } else {
                    throw new IOException(jestResult.getJsonString());
                }
            } else {
                LOGGER.info("Succeeded to delete document, index: {}, type: {}, id: {}", new Object[]{indexName, indexType, indexId});
            }
        } catch (IOException var6) {
            LOGGER.error(String.format("Failed to delete document, index: %s, type: %s, id: %s", indexName, indexType, indexId), var6);
            throw var6;
        }
    }

    public void deleteByQuery(String indexName, String indexType, Map<String, Object> queries) throws IOException {
        this.deleteByQuery(indexName, indexType, queries, null);
    }

    public void deleteByQuery(String indexName, String indexType, Map<String, Object> queries, Map<String, String> restParameters) throws IOException {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        Iterator var6 = queries.entrySet().iterator();

        while(var6.hasNext()) {
            Entry<String, Object> entry = (Entry)var6.next();
            boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
        }

        String queryString = (new SearchSourceBuilder()).query(boolQueryBuilder).toString();
        io.searchbox.core.DeleteByQuery.Builder deleteByQueryBuilder = ((new io.searchbox.core.DeleteByQuery.Builder(queryString)).addIndex(indexName)).addType(indexType);
        if (!mapIsEmpty(restParameters)) {
            Iterator var8 = restParameters.entrySet().iterator();

            while(var8.hasNext()) {
                Entry<String, String> parameter = (Entry)var8.next();
                deleteByQueryBuilder.setParameter(parameter.getKey(), parameter.getValue());
            }
        }

        try {
            JestResult jestResult = this.jestHttpClient.execute(deleteByQueryBuilder.build());
            if (!jestResult.isSucceeded()) {
                throw new IOException(jestResult.getJsonString());
            } else {
                LOGGER.info("Succeeded to delete documents by query, index: {}, type: {}, query:\n{}", new Object[]{indexName, indexType, queryString});
            }
        } catch (IOException var10) {
            LOGGER.error(String.format("Failed to delete documents by query, index: %s, type: %s, query:\n%s", indexName, indexType, queryString), var10);
            throw var10;
        }
    }

    public <T> T get(String indexName, String indexType, Object indexId, Class<T> resultType) throws IOException {
        return this.get(indexName, indexType, indexId, null, resultType);
    }

    public <T> T get(String indexName, String indexType, Object indexId, Map<String, String> restParameters, Class<T> resultType) throws IOException {
        io.searchbox.core.Get.Builder getBuilder = new io.searchbox.core.Get.Builder(indexName, indexId.toString()).type(indexType);
        if (!mapIsEmpty(restParameters)) {
            Iterator var7 = restParameters.entrySet().iterator();

            while(var7.hasNext()) {
                Entry<String, String> parameter = (Entry)var7.next();
                getBuilder.setParameter(parameter.getKey(), parameter.getValue());
            }
        }

        try {
            JestResult jestResult = this.jestHttpClient.execute(getBuilder.build());
            if (!jestResult.isSucceeded()) {
                if (!jestResult.getJsonObject().get("found").getAsBoolean()) {
                    return null;
                } else {
                    throw new IOException(jestResult.getJsonString());
                }
            } else {
                T result = jestResult.getSourceAsObject(resultType, false);
                LOGGER.info("Succeeded to get document, index: {}, type: {}, id: {}, result:\n{}", new Object[]{indexName, indexType, indexId, JSON.toJSONString(result)});
                return result;
            }
        } catch (IOException var9) {
            LOGGER.error(String.format("Failed to get document, index: %s, type: %s, id: %s", indexName, indexType, indexId), var9);
            throw var9;
        }
    }

    public SearchResult search(String indexName, String indexType, SearchSourceBuilder searchSourceBuilder) throws IOException {
        return this.search(indexName, indexType, searchSourceBuilder, null);
    }

    public SearchResult search(String indexName, String indexType, SearchSourceBuilder searchSourceBuilder, Map<String, String> restParameters) throws IOException {
        String query = searchSourceBuilder.toString();
        io.searchbox.core.Search.Builder searchBuilder = new io.searchbox.core.Search.Builder(query).addIndex(indexName).addType(indexType);
        if (!mapIsEmpty(restParameters)) {
            Iterator var7 = restParameters.entrySet().iterator();

            while(var7.hasNext()) {
                Entry<String, String> parameter = (Entry)var7.next();
                searchBuilder.setParameter(parameter.getKey(), parameter.getValue());
            }
        }

        try {
            SearchResult searchResult = this.jestHttpClient.execute(searchBuilder.build());
            if (!searchResult.isSucceeded()) {
                throw new IOException(searchResult.getJsonString());
            } else {
                LOGGER.info("Succeeded to search documents, index: {}, type: {}, query:\n{}", new Object[]{indexName, indexType, query});
                return searchResult;
            }
        } catch (IOException var9) {
            LOGGER.error(String.format("Failed to search documents, index: %s, type: %s, query:\n%s", indexName, indexType, query), var9);
            throw var9;
        }
    }

    private boolean mapIsEmpty(Map map){
        return map == null || map.size() == 0;
    }

    private static Object reflectUtilGetter(Object object, String propertyName) {
        try {
            return getter(object, new PropertyDescriptor(propertyName, object.getClass()));
        } catch (IntrospectionException var3) {
            throw new RuntimeException(var3);
        }
    }

    private static Object getter(Object object, PropertyDescriptor propertyDescriptor) {
        try {
            return propertyDescriptor.getReadMethod().invoke(object);
        } catch (InvocationTargetException | IllegalAccessException var3) {
            throw new RuntimeException(var3);
        }
    }
}