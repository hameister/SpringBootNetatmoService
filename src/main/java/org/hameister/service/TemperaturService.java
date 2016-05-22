package org.hameister.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.spi.LoggerFactory;
import org.hameister.model.Temperatur;
import org.hameister.model.Token;
import org.hameister.repository.TemperaturRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by hameister on 14.05.16.
 */
@Service
public class TemperaturService {

    public Logger logger = (Logger) org.slf4j.LoggerFactory.getLogger(TemperaturService.class);

    @Value("${client_id}")
    String client_id;
    @Value("${client_secret}")
    String client_secret;
    @Value("${username}")
    String username;
    @Value("${password}")
    String password;

    @Autowired
    TemperaturRepository repository;

    private RestTemplate restTemplate;

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Temperatur getTemperatur() {
        try {
            readNetatmoTemperature(getNetatmoToken());
        } finally {
            List<Temperatur> temperatures = repository.findAll();
            if( temperatures.size()>0) {;
                return temperatures.get(temperatures.size() - 1);
            }
            else {
                // If there is no old value
                return  new Temperatur();
            }
        }
    }


    private void readNetatmoTemperature(Token token) throws  RestClientException{
        ResponseEntity<String> resp =restTemplate.getForEntity("https://api.netatmo.net/api/devicelist?access_token=" + token.getAccess_token(), String.class);

        String json = resp.getBody();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = mapper.readTree(json);
        } catch (IOException e) {
            logger.error("Invalid JSON from Server.");

        }
        JsonNode devices = jsonNode.findValue("devices");
        JsonNode dashboard = devices.findValue("dashboard_data");
        JsonNode temperature = dashboard.findValue("Temperature");
        JsonNode time = dashboard.findValue("time_utc");

        Temperatur temperatur = new Temperatur();
        temperatur.setDate(new Date(time.asLong() * 1000));
        temperatur.setTemperatur(temperature.asText());
        repository.save(temperatur);
        System.out.println("Temperatur:" + temperature.asDouble());
    }

    protected Token getNetatmoToken() throws RestClientException {
        if(restTemplate == null) {
            restTemplate = new RestTemplate();
        }

        MultiValueMap<String, String> mvm = new LinkedMultiValueMap<>();
        mvm.add("client_id", client_id);
        mvm.add("client_secret", client_secret);
        mvm.add("grant_type", "password");
        mvm.add("username", username);
        mvm.add("password", password);
        mvm.add("scope", "read_station read_thermostat write_thermostat");


        return restTemplate.postForObject("https://api.netatmo.net/oauth2/token", mvm, Token.class);

    }
}
