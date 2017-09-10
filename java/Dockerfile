FROM docker.opexanalytics.com:5000/centos-node:6.0

COPY . /java

RUN yum -y update && \
	yum -y install deltarpm java java-devel wget && \
	wget http://www-us.apache.org/dist/kafka/0.9.0.1/kafka_2.11-0.9.0.1.tgz && \
	tar -xvf kafka_2.11-0.9.0.1.tgz && \
	mv kafka_2.11-0.9.0.1 /opt && \
	mv json-simple-1.1.1.jar /opt/kafka_2.11-0.9.0.1/libs && \
	javac -cp "/opt/kafka_2.11-0.9.0.1/libs/*" BikeStream.java ** \
	javac -cp "/opt/kafka_2.11-0.9.0.1/libs/*" StreamConsumer.java
