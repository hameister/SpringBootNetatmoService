package org.hameister.controller;

import org.hameister.model.Temperatur;
import org.hameister.service.TemperaturService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

/**
 * Created by hameister on 14.05.16.
 */
@RestController
public class TemperaturController {

    @Autowired
    TemperaturService temperaturService;

    /**************
     Temperature
     **************/
    @RequestMapping(value = "/api/temperature", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> getTemperature() {
        Temperatur temperatur = temperaturService.getTemperatur();
        return new ResponseEntity<String>(temperatur.getTemperatur(), HttpStatus.OK);
    }

}
