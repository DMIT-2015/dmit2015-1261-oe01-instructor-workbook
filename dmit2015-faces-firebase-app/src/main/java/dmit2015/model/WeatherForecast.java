package dmit2015.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.util.UUID;
import java.util.random.RandomGenerator;

@Getter
@Setter
@NoArgsConstructor
public class WeatherForecast {

    private String id;

    private LocalDate date;

    private int temperatureC;

    private String summary;

    public int getTemperatureF() {
        return (int) (32 + temperatureC / 0.5556);
    }

    public static WeatherForecast copyOf(WeatherForecast other) {
        return new WeatherForecast(other);
    }

    public WeatherForecast(WeatherForecast other) {
        setId(other.getId());
        setDate(other.getDate());
        setTemperatureC(other.getTemperatureC());
        setSummary(other.getSummary());
    }

    public static WeatherForecast of(Faker faker) {
        WeatherForecast currentWeatherForecast = new WeatherForecast();
        currentWeatherForecast.setId(UUID.randomUUID().toString());
        currentWeatherForecast.setDate(LocalDate.now().plusDays(RandomGenerator.getDefault().nextInt(1, 6)));
        currentWeatherForecast.setTemperatureC(faker.number().numberBetween(-20,50));
        currentWeatherForecast.setSummary(faker.weather().description());
        return currentWeatherForecast;
    }

}
