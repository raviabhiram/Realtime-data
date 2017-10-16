import java.util.Properties;
import java.util.Arrays;
import java.io.FileReader;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.apache.flink.streaming.util.serialization.DeserializationSchema;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

public class FlinkConsumer {

static String mongoHost;
static Integer mongoPort;
static String dbName;
static String collName;

public static void main(String[] args) throws Exception {
        try{
                FlinkConsumer init = new FlinkConsumer();
                init.run(args);
        } catch(Exception e) {
                e.printStackTrace();
        }
}
public void run(String[] args) throws Exception {
        try{
                StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
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
                mongoHost=(String) config.get("mongoHost");
                mongoPort=((Long) config.get("mongoPort")).intValue();
                dbName=(String)config.get("dbName");
                collName=(String)config.get("collection");

                Properties properties = new Properties();
                properties.setProperty("bootstrap.servers", "kafkaserver:9092");
                properties.setProperty("zookeeper.connect", "zookeeper:2181");
                properties.setProperty("group.id", group);
                env.enableCheckpointing(5000); // checkpoint every 5000 msecs

                DataStream<String> messageStream = env.addSource(new FlinkKafkaConsumer09<>(topic, new SimpleStringSchema(), properties));

                messageStream.addSink(new MongoPublisher());
                env.execute();
        }catch(Exception e) {
                e.printStackTrace();
        }
}

public class MongoPublisher extends RichSinkFunction<String> {

@Override
public void invoke(String streamValue){
        try{
                JSONParser parser = new JSONParser();
                Object configObject = parser.parse(streamValue);
                JSONObject json = (JSONObject) configObject;

                MongoClient mongo = new MongoClient( mongoHost, mongoPort );
                DB db = mongo.getDB(dbName);
                DBCollection collection = db.getCollection(collName);
                BasicDBObject bson = ( BasicDBObject ) JSON.parse( streamValue );
                collection.insert(bson);
                mongo.close();
        }catch(Exception e) {
                e.printStackTrace();
        }
}
}
}
