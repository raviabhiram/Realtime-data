# docker.opexanalytics.com:5000/realtime-data

Start zookeeper server:  
	`bin/zookeeper-server-start.sh config/zookeeper.properties`

Start zookeeper docker:  
	`docker run -d -p 2181:2181 -e SERVICE_2181_NAME=zookeeper -e SERVICE_2181_TAGS=discoverable -v /home/ec2-user/abhiram/Realtime-data/config/zookeeper:/opt/kafka_2.11-0.9.0.1/config/zookeeper.properties docker.opexanalytics.com:5000/realtime-kafka /opt/kafka_2.11-0.9.0.1/bin/zookeeper-server-start.sh /opt/kafka_2.11-0.9.0.1/config/zookeeper.properties`

Start kafka server:  
	`bin/kafka-server-start.sh config/server.properties`

Start kafka docker:  
	`docker run -d -p 9092:9092 -e SERVICE_9092_NAME=kafkaserver -e SERVICE_9092_TAGS=discoverable -v /home/ec2-user/abhiram/Realtime-data/config/kafka:/opt/kafka_2.11-0.9.0.1/config/server.properties docker.opexanalytics.com:5000/realtime-kafka /opt/kafka_2.11-0.9.0.1/bin/kafka-server-start.sh /opt/kafka_2.11-0.9.0.1/config/server.properties`

Create topic:  
	`bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test2`

List topics:  
	`bin/kafka-topics.sh --list --zookeeper localhost:2181`

Run producer:  
	`java -cp "/opt/kafka_2.11-0.9.0.1/libs/*":. BikeStream`  

Run producer docker:  
	`docker run -d -v /home/ec2-user/abhiram/Realtime-data/config/citiBike.json:/bikeConfig.json -v /home/ec2-user/abhiram/Realtime-data/data:/bikeData docker.opexanalytics.com:5000/realtime-java java -cp "/opt/kafka_2.11-0.9.0.1/libs/*":. BikeStream`  

	`docker run -d -v /home/ec2-user/abhiram/Realtime-data/config/divvyBike.json:/bikeConfig.json -v /home/ec2-user/abhiram/Realtime-data/data:/bikeData docker.opexanalytics.com:5000/realtime-java java -cp "/opt/kafka_2.11-0.9.0.1/libs/*":. BikeStream`

Run consumer:  
		`java -cp "/opt/kafka_2.11-0.9.0.1/libs/*":. ConsumerGroup test2 congrp
		java -cp "/opt/kafka_2.11-0.9.0.1/libs/*":. ConsumerGroup test2 congrp1`

Run consumer docker:  
		`docker run -d -v /home/ec2-user/abhiram/Realtime-data/config/citiBike.json:/bikeConfig.json -v /home/ec2-user/abhiram/Realtime-data/data:/bikeData docker.opexanalytics.com:5000/realtime-java java -cp "/java/jars/*":. FlinkConsumer`  

		`docker run -d -v /home/ec2-user/abhiram/Realtime-data/config/divvyBike.json:/bikeConfig.json -v /home/ec2-user/abhiram/Realtime-data/data:/bikeData docker.opexanalytics.com:5000/realtime-java java -cp "/java/jars/*":. FlinkConsumer`

	For 2 consumers reading data overall.  
		Change partitions for the topic to as many consumers as needed  
		`java -cp "/opt/kafka_2.11-0.9.0.1/libs/*":. ConsumerGroup test2 congrp
		java -cp "/opt/kafka_2.11-0.9.0.1/libs/*":. ConsumerGroup test2 congrp`
