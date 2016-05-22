package org.hameister.service;

import org.hameister.model.Temperatur;
import org.hameister.model.Token;
import org.hameister.repository.TemperaturRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Created by hameister on 14.05.16.
 */
@RunWith(MockitoJUnitRunner.class)
public class TemperaturServiceTest {

    @InjectMocks
    TemperaturService temperaturService;

    @Mock
    TemperaturRepository repository;

    @Mock
    RestTemplate restTemplate;

    @Mock
    Logger logger;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }


    private String getJSON() {

        String json = "{\"body\":{ \"devices\":[{\"dashboard_data\":{ \"time_utc\":1463854353, \"Temperature\":21.7}, \"data_type\":[ \"Temperature\"]}]}}}";
        return json;
    }

    @Test
    public void temperaturShouldBeSavedInRepoWithValuesFromJSONResponse() {
        Token token = new Token();

        MultiValueMap<String, String> mvm = new LinkedMultiValueMap();

        mvm.add("client_id", null);
        mvm.add("client_secret", null);
        mvm.add("grant_type", "password");
        mvm.add("username", null);
        mvm.add("password", null);
        mvm.add("scope", "read_station read_thermostat write_thermostat");

        ResponseEntity<String> resp = mock(ResponseEntity.class);
        when(resp.getBody()).thenReturn(getJSON());

        when(restTemplate.postForObject("https://api.netatmo.net/oauth2/token", mvm, Token.class)).thenReturn(token);

        when(restTemplate.getForEntity("https://api.netatmo.net/api/devicelist?access_token=" + token.getAccess_token(), String.class)).thenReturn(resp);

        temperaturService.getTemperatur();


        // check if repo save is called
        ArgumentCaptor<Temperatur> temperatur = ArgumentCaptor.forClass(Temperatur.class);

        verify(repository, times(1)).save(temperatur.capture());
        assertThat(temperatur.getValue().getDate(), is(new Date(1463854353l * 1000)));
        assertThat(temperatur.getValue().getTemperatur(), is("21.7"));
    }

    @Test
    public void shouldReturnEmptyTemperatureIfServerIsDown() {
        Temperatur temperatur = temperaturService.getTemperatur();
        assertNull(temperatur.getTemperatur());
        assertNull(temperatur.getDate());
    }

    @Test
    public void shouldReturnLastTemperatureValueIfServerIsDown() {

        Temperatur temperaturExpected = new Temperatur();
        temperaturExpected.setTemperatur("999.9");
        temperaturExpected.setDate(new Date(1463854353l));


        List<Temperatur> temperaturs = mock(ArrayList.class);

        when(repository.findAll()).thenReturn(temperaturs);

        when(temperaturs.size()).thenReturn(1);
        when(temperaturs.get(0)).thenReturn(temperaturExpected);


        Temperatur temperatur = temperaturService.getTemperatur();
        assertThat(temperatur.getTemperatur(), is(temperaturExpected.getTemperatur()));
        assertThat(temperatur.getDate(), is(temperaturExpected.getDate()));
    }

    @Test
    public void shouldWriteLogIfJSONIsInvalid() {
        Token token = new Token();

        MultiValueMap<String, String> mvm = new LinkedMultiValueMap();

        mvm.add("client_id", null);
        mvm.add("client_secret", null);
        mvm.add("grant_type", "password");
        mvm.add("username", null);
        mvm.add("password", null);
        mvm.add("scope", "read_station read_thermostat write_thermostat");

        ResponseEntity<String> resp = mock(ResponseEntity.class);
        when(resp.getBody()).thenReturn("Invalid{} JSON");

        when(restTemplate.postForObject("https://api.netatmo.net/oauth2/token", mvm, Token.class)).thenReturn(token);

        when(restTemplate.getForEntity("https://api.netatmo.net/api/devicelist?access_token=" + token.getAccess_token(), String.class)).thenReturn(resp);


        ArgumentCaptor<String> logEntry = ArgumentCaptor.forClass(String.class);

        temperaturService.getTemperatur();
        verify(logger, times(1)).error(logEntry.capture());
        assertThat(logEntry.getValue(), is("Invalid JSON from Server."));

    }

    @Test
    public void restTemplateSouldBeCreatedIfNull() {

        temperaturService.setRestTemplate(null);

        try {
            temperaturService.getTemperatur();
        } catch (NullPointerException e) {
 fail("No Exception expected.");
        }
    }
}
