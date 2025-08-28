package org.example.expert.client;

import org.example.expert.client.dto.WeatherDto;
import org.example.expert.domain.common.exception.ServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class WeatherClientTest {

    @InjectMocks
    private WeatherClient weatherClient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestTemplateBuilder builder;

    @BeforeEach
    public void setup() {
        given(builder.build()).willReturn(restTemplate);
        weatherClient = new WeatherClient(builder);
    }

    @Test
    @DisplayName("[WeatherClient] getTodayWeather 메서드 상태코드 오류 테스트")
    public void getTodayWeather_상태코드_오류시_예외발생() {

        given(restTemplate.getForEntity(any(URI.class), eq(WeatherDto[].class)))
                .willReturn(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> weatherClient.getTodayWeather())
                .isInstanceOf(ServerException.class)
                .hasMessageContaining("날씨 데이터를 가져오는데 실패했습니다. 상태 코드: " + HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("[WeatherClient] getTodayWeather 메서드 데이터가 없는 경우 테스트")
    public void getTodayWeather_데이터가_없을시_예외발생() {

        given(restTemplate.getForEntity(any(URI.class), eq(WeatherDto[].class)))
                .willReturn(new ResponseEntity<>(null, HttpStatus.OK));

        assertThatThrownBy(() -> weatherClient.getTodayWeather())
                .isInstanceOf(ServerException.class)
                .hasMessageContaining("날씨 데이터가 없습니다.");
    }

    @Test
    @DisplayName("[WeatherClient] getTodayWeather 메서드 오늘자 데이터가 없는 경우 테스트")
    public void getTodayWeather_오늘자_데이터가_없을시_예외발생() {

        WeatherDto[] fakeResponse = {
                new WeatherDto("01-01", "맑음"),
                new WeatherDto("01-02", "눈")
        };

        given(restTemplate.getForEntity(any(URI.class), eq(WeatherDto[].class)))
                .willReturn(new ResponseEntity<>(fakeResponse, HttpStatus.OK));

        assertThatThrownBy(() -> weatherClient.getTodayWeather())
                .isInstanceOf(ServerException.class)
                .hasMessageContaining("오늘에 해당하는 날씨 데이터를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("[WeatherClient] getTodayWeather 메서드 테스트")
    public void getTodayWeather() {

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd"));
        WeatherDto[] fakeResponse = {
                new WeatherDto(today, "맑음"),
                new WeatherDto("01-02", "눈")
        };

        given(restTemplate.getForEntity(any(URI.class), eq(WeatherDto[].class)))
                .willReturn(new ResponseEntity<>(fakeResponse, HttpStatus.OK));

        String weather = weatherClient.getTodayWeather();

        assertThat(weather).isEqualTo("맑음");
    }
}
