package dmit2015.service;

import dmit2015.model.WeatherForecast;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import net.datafaker.Faker;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In-memory implementation of the WeatherForecastService interface.
 *
 * <p>This class stores data in memory using a shared application-scoped list.
 * Because the service is {@code @ApplicationScoped}, the same service instance
 * may be used by multiple requests. A {@code CopyOnWriteArrayList} is used to
 * avoid common concurrent access problems with {@code ArrayList}.</p>
 *
 * <p>The service also uses defensive copying. Objects returned from this service
 * are copies of the stored objects, not direct references to the internal list.
 * This prevents external code from accidentally changing stored data without
 * calling an update method.</p>
 *
 * <p>This implementation is intended for development, testing, and learning.
 * In a production application, this service would normally be replaced by a
 * database-backed implementation.</p>
 */

@Named("memoryWeatherForecastService")
@ApplicationScoped
public class MemoryWeatherForecastService implements WeatherForecastService {

    private final List<WeatherForecast> weatherForecasts = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void init() {

        var faker = new Faker();
        for (int counter = 1; counter <= 5; counter++) {
            var currentWeatherForecast = WeatherForecast.of(faker);
            weatherForecasts.add(currentWeatherForecast);
        }

    }

    @Override
    public WeatherForecast createWeatherForecast(WeatherForecast weatherForecast) {
        Objects.requireNonNull(weatherForecast, "WeatherForecast to create must not be null");

        // Assign a fresh id on create to ensure uniqueness (ignore any incoming id)
        WeatherForecast stored = WeatherForecast.copyOf(weatherForecast);
        stored.setId(UUID.randomUUID().toString());
        weatherForecasts.add(stored);

        // Return a defensive copy
        return WeatherForecast.copyOf(stored);
    }

    @Override
    public Optional<WeatherForecast> getWeatherForecastById(String id) {
        Objects.requireNonNull(id, "id must not be null");

        return weatherForecasts.stream()
                .filter(currentWeatherForecast -> currentWeatherForecast.getId().equals(id))
                .findFirst()
                .map(WeatherForecast::copyOf); // return a copy to avoid external mutation

    }

    /**
     * Returns an unmodifiable snapshot of all stored WeatherForecast objects.
     *
     * <p>Each item is copied before being returned so callers cannot accidentally
     * modify the internal in-memory list.</p>
     */
    @Override
    public List<WeatherForecast> getAllWeatherForecasts() {
        // Unmodifiable snapshot of copies
        return weatherForecasts.stream().map(WeatherForecast::copyOf).toList();
    }

    @Override
    public WeatherForecast updateWeatherForecast(WeatherForecast weatherForecast) {
        Objects.requireNonNull(weatherForecast, "WeatherForecast to update must not be null");
        Objects.requireNonNull(weatherForecast.getId(), "WeatherForecast id must not be null");

        // Find index of existing task by id
        int index = -1;
        for (int i = 0; i < weatherForecasts.size(); i++) {
            if (weatherForecasts.get(i).getId().equals(weatherForecast.getId())) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            throw new NoSuchElementException("Could not find WeatherForecast with id: " + weatherForecast.getId());
        }

        // Replace stored item with a copy (preserve id)
        WeatherForecast stored = WeatherForecast.copyOf(weatherForecast);
        weatherForecasts.set(index, stored);

        return WeatherForecast.copyOf(stored);
    }

    @Override
    public void deleteWeatherForecastById(String id) {
        Objects.requireNonNull(id, "id must not be null");

        boolean removed = weatherForecasts.removeIf(currentWeatherForecast -> id.equals(currentWeatherForecast.getId()));
        if (!removed) {
            throw new NoSuchElementException("Could not find WeatherForecast with id: " + id);
        }
    }
}