package com.blm.takeout.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@Service
public class AmapServiceImpl {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${amap.key}")
    private String amapKey;

    @Value("${amap.api.driving}")
    private String drivingUrl;

    @Value("${amap.api.geocode}")
    private String geocodeUrl;

    @Value("${amap.api.regeo}")
    private String regeoUrl;

    @Value("${amap.api.distance}")
    private String distanceUrl;

    public AmapServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 获取驾驶路线规划
     */
    public Map<String, Object> getDrivingRoute(double startLng, double startLat, 
                                             double endLng, double endLat) {
        try {
            String url = String.format("%s?key=%s&origin=%f,%f&destination=%f,%f&extensions=all",
                drivingUrl,
                amapKey,
                startLng,
                startLat,
                endLng,
                endLat
            );

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            Map<String, Object> result = objectMapper.readValue(response.getBody(), Map.class);
            
            if ("1".equals(result.get("status"))) {
                return (Map<String, Object>) ((Map<String, Object>) result.get("route")).get("paths");
            }
            throw new RuntimeException("高德地图API调用失败: " + result.get("info"));
        } catch (Exception e) {
            throw new RuntimeException("获取路线规划失败: " + e.getMessage());
        }
    }

    /**
     * 地理编码（地址转坐标）
     */
    public Map<String, Object> geocode(String address) {
        try {
            String url = String.format("%s?key=%s&address=%s",
                geocodeUrl,
                amapKey,
                address
            );

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            Map<String, Object> result = objectMapper.readValue(response.getBody(), Map.class);
            
            if ("1".equals(result.get("status"))) {
                return (Map<String, Object>) ((Map<String, Object>) result.get("geocodes")).get(0);
            }
            throw new RuntimeException("地理编码失败: " + result.get("info"));
        } catch (Exception e) {
            throw new RuntimeException("地理编码服务调用失败: " + e.getMessage());
        }
    }

    /**
     * 逆地理编码（坐标转地址）
     */
    public Map<String, Object> reverseGeocode(double lng, double lat) {
        try {
            String url = String.format("%s?key=%s&location=%f,%f",
                regeoUrl,
                amapKey,
                lng,
                lat
            );

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            Map<String, Object> result = objectMapper.readValue(response.getBody(), Map.class);
            
            if ("1".equals(result.get("status"))) {
                return (Map<String, Object>) result.get("regeocode");
            }
            throw new RuntimeException("逆地理编码失败: " + result.get("info"));
        } catch (Exception e) {
            throw new RuntimeException("逆地理编码服务调用失败: " + e.getMessage());
        }
    }

    /**
     * 计算两点间距离
     */
    public int calculateDistance(double startLng, double startLat, 
                               double endLng, double endLat) {
        try {
            String url = String.format("%s?key=%s&origins=%f,%f&destination=%f,%f",
                distanceUrl,
                amapKey,
                startLng,
                startLat,
                endLng,
                endLat
            );

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            Map<String, Object> result = objectMapper.readValue(response.getBody(), Map.class);
            
            if ("1".equals(result.get("status"))) {
                Map<String, Object> results = (Map<String, Object>) ((Map<String, Object>) result.get("results")).get(0);
                return Integer.parseInt(results.get("distance").toString());
            }
            throw new RuntimeException("距离计算失败: " + result.get("info"));
        } catch (Exception e) {
            throw new RuntimeException("距离计算服务调用失败: " + e.getMessage());
        }
    }
} 