// Initialize the map
var map = L.map('map', {
  scrollWheelZoom: false
});

// Set the position and zoom level of the map
map.setView([41.866393, -87.620328], 14);

var osm_mapnik = L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
  maxZoom: 19,
  attribution: '&copy; OSM Mapnik <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
});

var esri_NatGeoWorldMap = L.tileLayer('http://server.arcgisonline.com/ArcGIS/rest/services/NatGeo_World_Map/MapServer/tile/{z}/{y}/{x}', {
  attribution: 'Tiles &copy; Esri &mdash; National Geographic, Esri, DeLorme, NAVTEQ, UNEP-WCMC, USGS, NASA, ESA, METI, NRCAN, GEBCO, NOAA, iPC',
  maxZoom: 16
}).addTo(map);

var esri_WorldImagery = L.tileLayer('http://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
  attribution: 'Tiles &copy; Esri &mdash; Source: Esri, i-cubed, USDA, USGS, AEX, GeoEye, Getmapping, Aerogrid, IGN, IGP, UPR-EGP, and the GIS User Community'
});

// Create base layers group object
var baseLayers = {
  "Open Street Map": osm_mapnik,
  "ESRI National Geographic": esri_NatGeoWorldMap,
  "ESRI World Imagery": esri_WorldImagery
};


var bikeIcon = L.icon({
  iconUrl: '../styles/bike.png',
  iconSize: [39, 39],
  iconAnchor: [18, 39],
  popupAnchor: [10, -35]
});

var _getData = function() {
  var xhr = new XMLHttpRequest();
  xhr.open("GET", "http://localhost:9092/data", false);
  xhr.send();
  return JSON.parse(xhr.response);
};

var _formatData = function(station) {
  var utcSeconds = station.last_reported,
    d = new Date(0); // The 0 there is the key, which sets the date to the epoch
  d.setUTCSeconds(utcSeconds);
  var result = '<b>';
  result += d;
  result += '<br>' + station.name;
  result += '<br>Bikes Available: ' + station.num_bikes_available;
  result += '<br>Docks Available: ' + station.num_docks_available;
  result += '</b>';
  return result;
};

var _getStations = function() {
  var data = _getData();
  var stations = [];
  data.forEach(function(station) {
    if (station) {
      stations.push(L.marker([station.lat, station.lon], {
        icon: bikeIcon
      }).bindPopup(_formatData(station)))
    } else {
      return;
    }
  });
  return stations;
};

var capitals = L.layerGroup(_getStations()).addTo(map);

var overlays = {
  'Capitals': capitals
};

// Add baseLayers and overlays to layer panel
L.control.layers(baseLayers, overlays).addTo(map);
