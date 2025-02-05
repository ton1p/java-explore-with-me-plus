package ewm.stats;

import ewm.client.BaseClient;
import ewm.dto.EndpointHitDTO;
import ewm.dto.StatsRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

@Service
public class StatsClient extends BaseClient {
    @Autowired
    public StatsClient(
            RestTemplateBuilder builder,
            DiscoveryClient discoveryClient
    ) {
        super(
                builder
                        .uriTemplateHandler(
                                new DefaultUriBuilderFactory(
                                        discoveryClient
                                                .getInstances("stats-server")
                                                .getFirst()
                                                .getUri()
                                                .toString()
                                )
                        )
                        .build()
        );
    }

    public ResponseEntity<Object> saveHit(EndpointHitDTO requestDto) {
        return post("/hit", requestDto);
    }

    public ResponseEntity<Object> getStats(StatsRequestDTO headersDto) {
        Map<String, Object> params = Map.of(
                "start", headersDto.getStart(),
                "end", headersDto.getEnd(),
                "unique", headersDto.getUnique()
        );
        return get("/stats" + getUrlParams(headersDto), params);
    }

    private String getUrlParams(StatsRequestDTO headersDto) {

        String urls = String.join(",", headersDto.getUris());
        String sb = "?" + getKeyValueUrl("start", headersDto.getStart()) +
                "&" +
                getKeyValueUrl("end", headersDto.getEnd()) +
                "&" +
                getKeyValueUrl("uris", urls) +
                "&" +
                getKeyValueUrl("unique", headersDto.getUnique());
        return sb;
    }

    private String getKeyValueUrl(String key, Object value) {
        return key + "=" + value;
    }
}
