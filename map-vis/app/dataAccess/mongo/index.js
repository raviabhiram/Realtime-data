"use strict";

var BluebirdPromise = require("bluebird");

var dbConManager = require("./dbConnectionManager").getInstance();

var _instance;

var MongoDataAccess = function() {

  var findDocument = function(collection, query, options) {
    return new BluebirdPromise(function(resolve, reject) {
      dbConManager.connect()
        .then(function(db) {
          db.collection(collection).find().toArray(function(err, result) {
            if (err) {
              reject(err);
            } else {
              resolve(result[0]);
            }
            db.close();
          });
        });
    });
  };

  var findLatestDocument = function(collection, query) {
    return new BluebirdPromise(function(resolve, reject) {
      dbConManager.connect()
        .then(function(db) {
          db.collection(collection).find().sort({
            "last_updated": -1
          }).limit(1).toArray(function(err, result) {
            if (err) {
              reject(err);
            } else {
              resolve(result[0]);
            }
            db.close();
          });
        });
    });
  };

  return {
    findLatestDocument: findLatestDocument,
    findDocument: findDocument
  };
};

module.exports = {
  getInstance: function() {
    if (!_instance) {
      _instance = new MongoDataAccess();
    }
    return _instance;
  }
};
