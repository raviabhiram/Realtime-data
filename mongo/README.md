#mongo cluster  

Build image:  
  `docker build -f Dockerfile -t realtime-mongo`

Run config server:  
    `docker run -d -p 20002:27019 -e SERVICE_27019_NAME=configsvrone -e SERVICE_27019_TAGS=discoverable -v /home/ec2-user/abhiram/Realtime-data/config/mongo-configsvr.yaml:/config.yaml -v /home/ec2-user/abhiram/Realtime-data/mongo/config:/data/db realtime-mongo mongod --config /config.yaml`  
    `docker run -d -p 20003:27019 -e SERVICE_27019_NAME=configsvrtwo -e SERVICE_27019_TAGS=discoverable -v /home/ec2-user/abhiram/Realtime-data/config/mongo-configsvr.yaml:/config.yaml -v /home/ec2-user/abhiram/Realtime-data/mongo/config1:/data/db realtime-mongo mongod --config /config.yaml`

Run shard servers:  
  `docker run -d -p 20010:27019 -e SERVICE_27019_NAME=shardone -e SERVICE_27019_TAGS=discoverable -v /home/ec2-user/abhiram/Realtime-data/config/mongo-shardsvr1.yaml:/config.yaml -v /home/ec2-user/abhiram/Realtime-data/mongo/data:/data/db realtime-mongo mongod --config /config.yaml`  
  `docker run -d -p 20020:27019 -e SERVICE_27019_NAME=shardtwo -e SERVICE_27019_TAGS=discoverable -v /home/ec2-user/abhiram/Realtime-data/config/mongo-shardsvr2.yaml:/config.yaml -v /home/ec2-user/abhiram/Realtime-data/mongo/data1:/data/db realtime-mongo mongod --config /config.yaml`  

Run router:  
  `docker run -d -p 20001:27019 -e SERVICE_27019_NAME=mongo_router -e SERVICE_27019_TAGS=discoverable -v /home/ec2-user/abhiram/Realtime-data/config/mongo-router.yaml:/config.yaml realtime-mongo mongos --config /config.yaml`

Run rs.initiate():  
  `rs.initiate(
    {
      _id : "realTimeDataConfig",
      configsvr: true,
      members: [
        { _id : 0, host : "configsvrone:20002" },
        { _id : 1, host : "configsvrtwo:20003" }
      ]
    }
  )

  rs.initiate(
    {
      _id : "realTimeDataShard1",
      members: [
        { _id : 0, host : "shardone:20010" },
      ]
    }
  )

  rs.initiate(
    {
      _id : "realTimeDataShard2",
      members: [
        { _id : 1, host : "shardtwo:20020" },
      ]
    }
  )`

Enable sharding through router:  
  `sh.addShard("realTimeDataShard1/shardone:20010")`
  `sh.addShard("realTimeDataShard2/shardtwo:20020")`
  `sh.enableSharding("bikeSharingData")`
  `sh.shardCollection("bikeSharingData.citiBike",{"last_updated":"hashed"})`
  `sh.shardCollection("bikeSharingData.divvyBike",{"last_updated":"hashed"})`  
