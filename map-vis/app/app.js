"use strict";

var express = require("express"),
  app = express(),
  http = require("http"),
  path = require('path');

var Routes = require("./middleware/routes"),
  serverConfig = require("./config.json").server;

var routes = new Routes(),
  server = http.createServer(app);


server.timeout = 0; //set server timeout to infinity.

// app.use(bodyParser.json());
// app.use(bodyParser.urlencoded({
//   extended: false
// }));

app.use(express.static('./public'));
// app.set('views', './public/views');

app.use("/", routes);

server.listen(serverConfig.port, function(err) {
  if (err) {
    console.log('Error while starting server...', err);
  } else {
    console.log('Server started and listening on Port:', serverConfig.port);
  }
});
