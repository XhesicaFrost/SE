package com.blm.takeout.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeoService {

    @Value("${amap.api.key}")
    private String amapApiKey;

    private static final String GEOCODE_API_URL = "https://restapi.amap.com/v3/geocode/geo";

    public Map<String, Double> getCoordinates(String address) {
        try {
            // 不对地址进行编码，直接使用中文
            String url = String.format("%s?address=%s&key=%s", GEOCODE_API_URL, address, amapApiKey);

            System.out.println("调用高德地图 API，URL: " + url);

            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            ).getBody();

            System.out.println("高德地图 API 响应: " + response);

            if (response != null && "1".equals(response.get("status"))) {
                ObjectMapper mapper = new ObjectMapper();
                List<Map<String, Object>> geocodes = mapper.convertValue(
                    response.get("geocodes"),
                    new TypeReference<List<Map<String, Object>>>() {}
                );
                if (geocodes != null && !geocodes.isEmpty()) {
                    String[] location = ((String) geocodes.get(0).get("location")).split(",");
                    return Map.of(
                        "longitude", Double.parseDouble(location[0]),
                        "latitude", Double.parseDouble(location[1])
                    );
                }
            }

            throw new RuntimeException("无法获取地址的经纬度，检查地址或密钥是否正确");
        } catch (Exception e) {
            throw new RuntimeException("调用高德地图 API 时发生错误: " + e.getMessage(), e);
        }
    }
}