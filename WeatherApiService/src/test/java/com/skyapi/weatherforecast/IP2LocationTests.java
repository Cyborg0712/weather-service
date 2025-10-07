package com.skyapi.weatherforecast;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class IP2LocationTests {
    private String DBPath = "ip2locdb/IP2LOCATION-LITE-DB3.BIN";

    @Test
    public void testInvalidIP() throws IOException {
        IP2Location ipLocator = new IP2Location();
        ipLocator.Open(DBPath);

        String ipAddress = "abc";
        IPResult ipResult = ipLocator.IPQuery(ipAddress);

        assertThat(ipResult.getStatus()).isEqualTo("INVALID_IP_ADDRESS");
        System.out.println(ipResult);
    }

    @Test
    public void testValidIP1() throws IOException {
        IP2Location ipLocator = new IP2Location();
        ipLocator.Open(DBPath);

        String ipAddress = "124.197.25.216";
        IPResult ipResult = ipLocator.IPQuery(ipAddress);

        assertThat(ipResult.getStatus()).isEqualTo("OK");
        assertThat(ipResult.getCity()).isEqualTo("Hanoi");

        System.out.println(ipResult);
    }
}
