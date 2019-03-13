import java.util.Properties;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;
import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/*
Main class to start the bike sharing data streaming.
*/
public class BikeStream {
  public static void main(String[] args) throws Exception {
    try {
      BikeStream init = new BikeStream();
      init.run(); // Start a new thread to stream the data.
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void run() throws Exception {
    try {
      // Get all configs from config.json.
      JSONParser parser = new JSONParser();
      Object configObject = parser.parse(new FileReader("/bikeConfig.json"));
      JSONObject config = (JSONObject) configObject;
      String topic = (String) config.get("topic"); // Each topic is a channel to which the data is written.
      String api = (String) config.get("API");
      long interval = (long) config.get("interval");

      // Start streaming data.
      StreamData sd = new StreamData(topic, api);

      // Set up scheduler to ping the API at regular intervals.
      Timer time = new Timer();
      long delay = 0;
      time.schedule(sd, delay, interval);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /*
  Child class to stream the data and write to the channel/topic in Kafka.
  */
  public class StreamData extends TimerTask {

    String topicName;
    String streamUrl;

    public StreamData() {
      topicName = "default";
    }

    public StreamData(String topic, String url) {
      topicName = topic; // Set the channle to which the data should be written into.
      streamUrl = url; // Set the url which needs to be pinged.
    }

    private final String USER_AGENT = "Mozilla/5.0";

    /*
    A new thread is created for each stream.
    This helps with streaming from multiple data sources in parallel.
    */
    public void run() {
      try {
        System.out.println("Streaming data to kafka topic " + topicName);
        StreamData http = new StreamData();
        String key = "station_status"; // The key to the Kafka topic.
        String data = http.sendGet(streamUrl); // Send a GET request to get data from the bike sharing API.
        Properties props = new Properties(); // Set the properties for the producer.
        props.put("bootstrap.servers", "kafkaServer:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = new KafkaProducer<String, String>(props); // Create a new Kafka Producer.
        producer.send(new ProducerRecord<String, String>(topicName, key, data));
        System.out.println("Message sent successfully");
        producer.close(); // Close the producer once the message has been sent.
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    /*
    Function to send a GET request to get data from the bike sharing API.
    */
    private String sendGet(String streamUrl) throws Exception {

      try {
        URL obj = new URL(streamUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        // Optional default is GET
        con.setRequestMethod("GET");
        // Add request header
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + streamUrl);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer(); // To store the entire response from the API call.
        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
        in.close();
        return response.toString();
      } catch (Exception e) {
        e.printStackTrace();
        return "Error!";
      }
    }
  }
}
