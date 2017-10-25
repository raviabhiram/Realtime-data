var Bluebird = require('bluebird');

var mongoClient = require('../dataAccess/mongo').getInstance();

var DataProvider = function() {}

var _getStations = function() {
  var stations = [];
  console.log('Getting stations.');
  return mongoClient.findDocument('divvyBikeStations', {})
    .then(function(data) {
      var rawData = data.data.stations;
      rawData.forEach(function(station) {
        if (!stations[station.station_id]) {
          stations[station.station_id] = {};
          stations[station.station_id] = {
            id: station.station_id,
            name: station.name,
            lat: station.lat,
            lon: station.lon,
            capacity: station.capacity
          };
        } else {
          stations[station.station_id] = {
            id: station.station_id,
            name: station.name,
            lat: station.lat,
            lon: station.lon,
            capacity: station.capacity
          };
        }
      })
      return stations;
    })
    .catch(function(err) {
      console.log('Error getting stations!', err);
      throw err;
    });
};

var _getStatus = function(stations) {
  console.log('Getting status.');
  return mongoClient.findLatestDocument('divvyBike', {})
    .then(function(data) {
      var rawData = data.data.stations;
      rawData.forEach(function(station) {
        stationData = stations[station.station_id];
        stationData.last_reported = station.last_reported;
        stationData.num_bikes_available = station.num_bikes_available;
        stationData.num_bikes_disabled = station.num_bikes_disabled;
        stationData.num_docks_available = station.num_docks_available;
        stationData.num_docks_disabled = station.num_docks_disabled;
        stationData.is_installed = station.is_installed;
        stationData.is_renting = station.is_renting;
        stationData.is_returning = station.is_returning;
      });
      return stations;
    })
    .catch(function(err) {
      console.log('Error getting stations!', err);
      throw err;
    });
}

DataProvider.prototype.getData = function(req, res) {
  console.log('Inside data provider.');
  return _getStations()
    .then(function(stations) {
      console.log('Retireved stations list.');
      return _getStatus(stations)
    })
    .then(function(data) {
      console.log('Retrieved station data.');
      res.status(200).send(data);
      console.log('Successfully sent data.');
    })
    .catch(function(err) {
      console.log('Error!', err);
      res.status(500).send(err);
      throw err;
    });
};

module.exports = DataProvider;
