import java.util.Properties;
import java.util.Arrays;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class StreamConsumer {
public static void main(String[] args) throws Exception {
        String group;
        if(args.length==0) {
                System.out.println("No group name passed. Assigning default group \'consumer\'");
                group="consumer";
        }
        else{
                group = args[0].toString();
        }

        //Get all configs from config.json.
        JSONParser parser = new JSONParser();
        Object configObject = parser.parse(new FileReader("/bikeConfig.json"));
        JSONObject config = (JSONObject) configObject;
        String topic=(String) config.get("topic");
        String resultsPath=(String) config.get("writePath");

        Properties props = new Properties();
        props.put("bootstrap.servers", "kafkaServer:9092");
        props.put("group.id", group);
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("auto.offset.reset", "earliest");
        props.put("key.deserializer",
                  "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer",
                  "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);

        consumer.subscribe(Arrays.asList(topic));
        System.out.println("Subscribed to topic " + topic);

//Set up the writer to write to file.
        FileWriter fw = new FileWriter(resultsPath, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(bw);

//Keep running the consumer to read from the topic
        while (true) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                        System.out.printf("Read data from server.\n");
                        //Write the read results to the file.
                        try{
                                out.println(",%s",record.value());
                        }
                        catch(Exception e) {
                                System.out.println("Error writing to file!");
                                e.printStackTrace();
                        }
                }
        }
}
}
