FROM docker.opexanalytics.com:5000/realtime-kafka

COPY . /java

RUN yum -y update && \
	cd /java && \
	cp jars/json-simple-1.1.1.jar /opt/kafka_2.11-0.9.0.1/libs && \
	javac -cp "/opt/kafka_2.11-0.9.0.1/libs/*" BikeStream.java && \
	javac -cp "/opt/kafka_2.11-0.9.0.1/libs/*" StreamConsumer.java && \
	javac -cp "/java/jars/*" FlinkConsumer.java

WORKDIR /java
