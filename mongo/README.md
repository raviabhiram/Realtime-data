#mongo cluster  

Build image:  
  `docker build -f Dockerfile -t realtime-mongo`

Run shard servers:  
  `docker run -d -p 20001:27017 -e SERVICE_27017_NAME=shardone -v /vagrant/Realtime-data/config/mongo-shardsvr.yaml:/config.yaml -v /vagrant/Realtime-data/mongo/data:/data realtime-mongo mongod --config /config.yaml`  
  `docker run -d -p 20002:27017 -e SERVICE_27017_NAME=shardtwo -v /vagrant/Realtime-data/config/mongo-shardsvr.yaml:/config.yaml -v /vagrant/Realtime-data/mongo/data1:/data realtime-mongo mongod --config /config.yaml`  

Run config server:  
  `docker run -d -p 20003:27017 -e SERVICE_27017_NAME=configsvr -v /vagrant/Realtime-data/config/mongo-configsvr.yaml:/config.yaml -v /vagrant/Realtime-data/mongo/config:/data realtime-mongo mongod --config /config.yaml`  

Run router:  
  `docker run -d -p 20004:27017 -e SERVICE_27017_NAME=mongo_router -v /vagrant/Realtime-data/config/mongo-router.yaml:/config.yaml realtime-mongo mongos --config /config.yaml`
