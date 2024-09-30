package org.example;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import org.json.JSONObject;
public class WeatherApp {
    public static String makeApiCall(String apiUrl) {
        StringBuilder response = new StringBuilder();
        try {
            // Create a URL object
            URL url = new URL(apiUrl);
            // Open a connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // Set the request method
            conn.setRequestMethod("GET");
            // Set request headers (if needed)
            conn.setRequestProperty("Accept", "application/json");
            // Check the response code
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK
                // Create an InputStream to read the response
                InputStream inputStream = conn.getInputStream();
                // Use Scanner to read the response
                Scanner scanner = new Scanner(inputStream);
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();
            } else {
                System.out.println("GET request not worked. Response Code: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    public static void printWeather(String jsonResponse) {
        JSONObject jsonObject = new JSONObject(jsonResponse);

        // Extract relevant data
        String city = jsonObject.getString("name");
        String country = jsonObject.getJSONObject("sys").getString("country");
        double temperature = jsonObject.getJSONObject("main").getDouble("temp") - 273.15; // Convert from Kelvin to Celsius
        String weatherDescription = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
        double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
        int humidity = jsonObject.getJSONObject("main").getInt("humidity");
        int time = jsonObject.getInt("timezone");
        Instant nowUtc = Instant.now();
        // Convert to the local time of the city
        LocalDateTime localTime = LocalDateTime.ofInstant(nowUtc, ZoneOffset.UTC).plusSeconds(time);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = localTime.format(formatter);
        // Print formatted output
        System.out.println("Weather report in " + city + "," + country + " as of " + formattedTime);
        System.out.printf("Temperature: %.2f°C%n", temperature);
        System.out.println("Condition: " + weatherDescription);
        System.out.println("Wind Speed: " + windSpeed + " m/s");
        System.out.println("Humidity: " + humidity + "%");
    }
    public static void main(String[] args) {
        String apiKey = "b28a006384caddf06739fabc1aaaab0f"; // Replace with your actual API key
        String city = "Hyderabad"; // Replace with the desired city
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;
        String jsonResponse = makeApiCall(apiUrl);
        printWeather(jsonResponse);
    }
}
