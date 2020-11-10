package cn.xlucky.framework.elasticsearch.config;

import cn.xlucky.framework.elasticsearch.ElasticsearchService;
import com.google.common.base.Splitter;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.JestHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * es配置
 * @author xlucky
 * @date 2020/5/28
 * @version 1.0.0
 */
@Configuration
public class ElasticsearchAutoConfiguration {
    @Value("${elasticsearch.host}")
    private String host;
    @Value("${elasticsearch.timeout:10000}")
    private Integer timeout;
    @Value("${elasticsearch.maxTotalConnection:32}")
    private Integer maxTotalConnection;

    @Bean(name = "jestHttpClient")
    @ConditionalOnMissingBean
    public JestHttpClient jestHttpClient() {

        List<String> hostList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(host);
        JestClientFactory factory = new JestClientFactory();

        factory.setHttpClientConfig(new HttpClientConfig
                .Builder(hostList)
                .multiThreaded(true)
                .connTimeout(timeout)
                .maxTotalConnection(maxTotalConnection)
                .build());

        return (JestHttpClient) factory.getObject();
    }

    @Bean
    public ElasticsearchService saasElasticsearchService() {
        return new ElasticsearchService();
    }
}