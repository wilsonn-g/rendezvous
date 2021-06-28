const debug = require("debug");
const logger = debug("db:error");
const config = require("./config");
const express = require("express");
const app = require("./index.js");
const mongoose = require("mongoose");
const admin = require("firebase-admin");
const serviceAccount = require("./firebasekey_rendezvous.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://rendezvous-1cddb.firebaseio.com"
});

mongoose.connect(config.DATABASE_URL, { useNewUrlParser: true, useUnifiedTopology: true });
var db = mongoose.connection;
db.on("error", (error) => logger(error));
db.once("open", () => logger("Database connected"));

app.listen(8000, () => logger("Server started"));
