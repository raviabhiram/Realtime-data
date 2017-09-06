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

public class BikeStream {
public static void main(String[] args) throws Exception {
        try{
                BikeStream init = new BikeStream();
                init.run(args);
        } catch(Exception e) {
                e.printStackTrace();
        }
}

public void run(String[] args) throws Exception {
        try{
                if(args.length == 0) {
                        System.out.println("Enter topic name");
                        return;
                }

                Timer time = new Timer();
                StreamData sd = new StreamData(args[0].toString());
                long delay = 0;
                long interval = 10000;
                time.schedule(sd,delay,interval);
        } catch(Exception e) {
                e.printStackTrace();
        }
}

public class StreamData extends TimerTask {

String topicName;

public StreamData(){
        topicName="default";
}

public StreamData(String topic){
        topicName=topic;
}

private final String USER_AGENT = "Mozilla/5.0";


public void run(){
        try{
                System.out.println("Streaming data to kafka topic "+topicName);
                StreamData http = new StreamData();

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
        }catch(Exception e) {
                e.printStackTrace();
        }
}

private String sendGet() throws Exception {

        try{
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

                return response.toString();
        }catch(Exception e) {
                e.printStackTrace();
                return "Error!";
        }
}
}
}
