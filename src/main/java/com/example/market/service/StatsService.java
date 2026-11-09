package com.example.market.service;

import com.example.market.mapper.StatsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsMapper statsMapper;

    /** 一次返回整个看板需要的数据,前端一次请求全拿到,减少往返 */
    public Map<String, Object> dashboard() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("overview", statsMapper.overview());
        data.put("orderStatus", statsMapper.orderStatus());
        data.put("productCategory", statsMapper.productCategory());
        data.put("orderTrend", statsMapper.orderTrend());
        data.put("topProducts", statsMapper.topProducts());
        return data;
    }
}
