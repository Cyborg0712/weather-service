package com.skyapi.weatherforecast;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtility {
    public static String getIPAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");
        if(ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        log.info(ip) ;
        return ip;
    }
}
