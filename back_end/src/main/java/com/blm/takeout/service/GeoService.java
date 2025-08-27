package com.blm.takeout.service;

import org.springframework.beans.factory.annotation.Value;
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
            String url = String.format("%s?address=%s&key=%s", GEOCODE_API_URL, address, amapApiKey);

            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && "1".equals(response.get("status"))) {
                List<Map<String, Object>> geocodes = (List<Map<String, Object>>) response.get("geocodes");
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