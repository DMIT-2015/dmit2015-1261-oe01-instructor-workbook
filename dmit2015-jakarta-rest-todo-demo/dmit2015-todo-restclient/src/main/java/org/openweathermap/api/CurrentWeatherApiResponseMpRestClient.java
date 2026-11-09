package org.openweathermap.api;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RequestScoped
@RegisterRestClient
public interface CurrentWeatherApiResponseMpRestClient {

    @GET
    @Path("/weather")
    CurrentWeatherApiResponse getCurrentWeatherByCity(
            @QueryParam("q") String city,
            @QueryParam("appid") String apiKey,
            @DefaultValue("metric") @QueryParam("units") String units   // The units of measurement: standard, metric, imperial
    );

    @GET
    @Path("/weather")
    CurrentWeatherApiResponse getCurrentWeatherByGeographicalCoordinates(
            @QueryParam("lat") String latitude,
            @QueryParam("lon") String longitude,
            @QueryParam("appid") String apiKey,
            @QueryParam("units") String units
    );

}