package ewm.stats;

import ewm.client.BaseClient;
import ewm.dto.EndpointHitDTO;
import ewm.dto.StatsRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

@Service
public class StatsClient extends BaseClient {

	@Autowired
	public StatsClient(@Value("${stats.server.url}") String serverUrl, RestTemplateBuilder builder) {
		super(
				builder
						.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
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
		StringBuilder sb = new StringBuilder("?");
		return sb.append(getKeyValueUrl("start", headersDto.getStart()))
				.append("&")
				.append(getKeyValueUrl("end", headersDto.getEnd()))
				.append("&")
				.append(getKeyValueUrl("uris", urls))
				.append("&")
				.append(getKeyValueUrl("unique", headersDto.getUnique()))
				.toString();
	}

	private String getKeyValueUrl(String key, Object value) {
		return key + "=" + value;
	}
}
