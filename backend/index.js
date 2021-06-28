const debug = require("debug");
const logger = debug("db:error");
const express = require("express");
const app = express();
const bodyParser = require("body-parser");

app.use(express.json());

const usersRouter = require("./routes/users/users");
const groupsRouter = require("./routes/groups/groups");
const recommendRouter = require("./routes/recommend/recommend");
const placesRouter = require("./routes/places/places");

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use("/users", usersRouter);
app.use("/groups", groupsRouter);
app.use("/recommend", recommendRouter);
app.use("/places", placesRouter);

module.exports = app;
