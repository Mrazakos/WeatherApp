import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;
    public WeatherAppGui(){
        // set up the gui
        super("Cloudy");

        // configure gui to end the program's process once closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // set the size of our gui
        setSize(450, 650);

        // load gui to the center
        setLocationRelativeTo(null);

        // make layout managment null, to manually position our components
        setLayout(null);

        // make not resizable
        setResizable(false);

        addGuiComponents();
    }

    private void addGuiComponents(){
        //Search box
        JTextField searchTextField = new JTextField();
        searchTextField.setBounds(15,15,351,45);

        // change fonts
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextField);

        //weather image

        JLabel weatherImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherImage.setBounds(0, 125, 450, 217);
        add(weatherImage);

        // temperature Text
        JLabel temperatureText = new JLabel("Search for a location!");
        temperatureText.setBounds(0, 340, 450,54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 35));

        //align to middle
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // Weather condition description

        JLabel weatherCondition = new JLabel("");
        weatherCondition.setBounds(0, 405, 450, 36);
        weatherCondition.setFont(new Font("Dialog", Font.BOLD, 32));
        weatherCondition.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherCondition);

        // Humidity
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(20, 500, 74, 66);
        add(humidityImage);

        //humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> </br> -----</html>");
        humidityText.setBounds(95, 500, 80, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        // windspeed
        JLabel windSpeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windSpeedImage.setBounds(220, 500, 74, 66);
        add(windSpeedImage);
        // windspeed text
        JLabel windSpeedText = new JLabel("<html><b>Windspeed</b> </br> -----</html>");
        windSpeedText.setBounds(310, 500, 100, 66);
        windSpeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windSpeedText);

        //search button
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        // change cursor on search button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = searchTextField.getText();

                //validate input
                if (userInput.replaceAll("\\s", "").length() <= 0){
                    return;
                }

                //get data
                weatherData = WeatherApp.getWeatherData(userInput);

                // update gui

                // update weather image
                String weatherCon = (String) weatherData.get("weather_condition");

                // depending on the condition update image
                switch (weatherCon){
                    case "Clear":
                        weatherImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Snow":
                        weatherImage.setIcon(loadImage("src/assets/snow.png"));
                        break;
                    case "Rain":
                        weatherImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                }

                //update temperature
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C°");
                temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

                weatherCondition.setText(weatherCon);

                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> </br> " + humidity + "%</html>");

                double windspeed = (double) weatherData.get("windspeed");
                windSpeedText.setText("<html><b>Windspeed</b> </br> " + windspeed + "km/h</html>");
            }
        });
        add(searchButton);

    }


    private ImageIcon loadImage(String path){
        try{
            //read the image file
            BufferedImage image = ImageIO.read(new File(path));

            //return the image icon
            return new ImageIcon(image);
        } catch (IOException e) {
            e.printStackTrace();

        }
        System.out.println("Could not find resource");
        return null;

    }
}
