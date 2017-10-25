'use strict';

var express = require('express'),
  path = require('path'),
  router = express.Router();

var DataProvider = require('../../dataProvider');

var dataProvider = new DataProvider();

module.exports = function() {

  router.get('/', function(req, res) {
    res.sendFile(path.join(__dirname, '../../../public/views/index.html'));
  });

  router.get('/data', dataProvider.getData)

  return router;
};
