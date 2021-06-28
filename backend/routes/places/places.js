const express = require("express");
const router = express.Router();
const Place = require("../../models/place");

/*
 *  GET request on a specific place
 */
router.get("/:placeid", async (req, res) => {
  const place = await Place.find({ "placeid": req.params.placeid });
  res.json(place);
});

module.exports = router;
