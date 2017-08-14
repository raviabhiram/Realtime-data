import java.util.Properties;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BikeStream {

   private final String USER_AGENT = "Mozilla/5.0";
   
   public static void main(String[] args) throws Exception{
      if(args.length == 0){
         System.out.println("Enter topic name");
         return;
      }
      
      BikeStream http = new BikeStream();

      String topicName = args[0].toString();
      String key = "station_information";
      String data = http.sendGet();
      Properties props = new Properties();
      props.put("bootstrap.servers", "kafkaServer:9092");
      props.put("acks", "all");
      props.put("retries", 0);
      props.put("batch.size", 16384);
      props.put("linger.ms", 1);
      props.put("buffer.memory", 33554432);
      props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
      props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

      Producer<String, String> producer = new KafkaProducer
         <String, String>(props);
            
      producer.send(new ProducerRecord<String, String>(topicName, key, data));
      System.out.println("Message sent successfully");
      producer.close();
   }

   private String sendGet() throws Exception {

      String url = "https://gbfs.citibikenyc.com/gbfs/en/station_information.json";

      URL obj = new URL(url);
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();

      // optional default is GET
      con.setRequestMethod("GET");

      //add request header
      con.setRequestProperty("User-Agent", USER_AGENT);

      int responseCode = con.getResponseCode();
      System.out.println("\nSending 'GET' request to URL : " + url);
      System.out.println("Response Code : " + responseCode);

      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String inputLine;
      StringBuffer response = new StringBuffer();

      while ((inputLine = in.readLine()) != null) {
         response.append(inputLine);
      }
      in.close();

      //print result
      // System.out.println(response.toString());
      return response.toString();

   }
}