package org.hameister.controller;

import org.hameister.model.Temperatur;
import org.hameister.service.TemperaturService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Created by hameister on 22.05.16.
 */
@RunWith(MockitoJUnitRunner.class)
public class TemperaturControllerTest {
    @InjectMocks
    TemperaturController temperaturController;

    @Mock
    TemperaturService temperaturService;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnAValidTemperaturAndReturnCode(){
        Temperatur temperatur = new Temperatur();
        temperatur.setTemperatur("999.9");

        when(temperaturService.getTemperatur()).thenReturn(temperatur);
        ResponseEntity<String> entity = temperaturController.getTemperature();

        assertThat(entity.getStatusCode(), is(HttpStatus.OK));
        assertThat(entity.getBody(), is("999.9"));
    }
}
