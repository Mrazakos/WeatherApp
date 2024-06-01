import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.Scanner;

//this backend logic retrives the dtata from the weather API
public class WeatherApp {
    // fetch data for given location
    public static JSONObject getWeatherData(String locationName){
        JSONArray locationData = getLocationData(locationName);

        // extract latitude and longitude
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");



        // URL building
        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude="+ longitude +"&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";

        try{
            //call api and get response

            HttpURLConnection conn = fetchApiResponse(urlString);

            //check for response status
            // 200 -means success
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Couldn't connect to api");
                return null;
            }

            // sotre resulting json data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()){
                resultJson.append(scanner.next());
            }
            scanner.close();
            conn.disconnect();


            JSONParser parser = new JSONParser();
            JSONObject result = (JSONObject) parser.parse(String.valueOf(resultJson));

            //retrieve hourly data
            JSONObject current = (JSONObject) result.get("current");
            /*
            Hourly solution, less accurate

            JSONObject hourly = (JSONObject) result.get("hourly");
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexofCurrentTime(time);

            //get temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            //get weather code
            JSONArray weathercodeData = (JSONArray) hourly.get("weather_code");
            long weather_code = (long) weathercodeData.get(index);
            String weatherCondition = convertWeatherCode(weather_code);

            //get humidity
            JSONArray humidityData = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) humidityData.get(index);

            // get wind data
            JSONArray windData = (JSONArray) hourly.get("wind_speed_10m");
            double wind = (double) windData.get(index);

            // build the weather json data object


            JSONObject weatherData = new JSONObject();

            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", wind);
            */
            JSONObject weatherData = new JSONObject();

            weatherData.put("temperature", (double)current.get("temperature_2m"));
            long weatherCode = (long) current.get("weather_code");
            String weatherCondition = (String) convertWeatherCode(weatherCode);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", (long)current.get("relative_humidity_2m"));
            weatherData.put("windspeed", (double)current.get("wind_speed_10m"));
            return weatherData;

        } catch(Exception e){

            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray getLocationData(String locationName){
        // replace whitespace characters to +
        locationName = locationName.replaceAll(" ", "+");

        // Api URL build
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
            locationName + "&count=10&language=en&format=json";

        try{
            // call api and get a response
            HttpURLConnection conn = fetchApiResponse(urlString);

            // check response status
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            } else{
                //store API results
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                //read and store resulting data into StringBuilder
                while(scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }
                scanner.close();
                conn.disconnect();
                // put the string into JSONObject
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                // get the data the API generated from the location name
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        try{
            //attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //set request method to get
            conn.setRequestMethod("GET");

            conn.connect();
            return conn;

        } catch (IOException e){
            e.printStackTrace();
        }
        return null;


    }
    private static int findIndexofCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        //iterate through the time list, see which one matches
        for (int i = 0; i < timeList.size(); ++i){
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime)){
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime(){
        LocalDateTime currentDatetime = LocalDateTime.now();

        // format date to be liek in the API

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        // format and return date and time
        String formattedDateTime = currentDatetime.format(formatter);

        return formattedDateTime;

    }

    private static String convertWeatherCode(long weather_code){
        if (weather_code == 0L){
            return "Clear";
        }
        if (weather_code > 0L && weather_code <= 3L){
            return  "Cloudy";
        }
        if((weather_code >= 51L && weather_code <= 67L) || (weather_code >= 80L && weather_code <= 99L)){
            return "Rain";
        }
        if (weather_code>=71L && weather_code <= 77L){
            return "Snow";
        }
        return "";

    }

}
