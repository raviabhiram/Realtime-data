"use strict";

var BluebirdPromise = require("bluebird"),
  MongoClient = require("mongodb").MongoClient,
  ObjectID = require("mongodb").ObjectID;

var dbConfig = require("../../config.json").database.mongo;

var _instance;

var DBConnectionManager = function() {

  var url = "mongodb://" + dbConfig.host + ":" + dbConfig.port + "/" + dbConfig.dbName,
    options = {
      poolSize: dbConfig.poolSize,
      connectTimeoutMS: dbConfig.connectTimeoutMS,
      socketTimeoutMS: dbConfig.socketTimeoutMS,
      autoReconnect: dbConfig.autoReconnect,
      reconnectTries: dbConfig.reconnectTries,
      reconnectInterval: dbConfig.reconnectInterval,
      validateOptions: dbConfig.validateOptions
    };

  var connect = function() {
    return new BluebirdPromise(function(resolve, reject) {
      MongoClient.connect(url, options, function(err, db) {
        if (err || !db) {
          reject(err);
        } else if (db) {
          resolve(db);
        }
      });
    });
  };

  var getObjectId = function(id) {
    return id ? new ObjectID(id) : new ObjectID();
  };

  var isValidObjectId = function(id) {
    return ObjectID.isValid(id);
  };

  return {
    connect: connect,
    getObjectId: getObjectId,
    isValidObjectId: isValidObjectId
  };

};

module.exports = {
  getInstance: function() {
    if (!_instance) {
      _instance = new DBConnectionManager();
    }
    return _instance;
  }
};
