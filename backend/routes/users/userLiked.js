const debug = require("debug");
const logger = debug("app:error");
const express = require("express");
const router = express.Router();
const User = require("../../models/user");

/*
 *	POST request to add a liked place for a given user.
 */
router.post("/", async (req, res) => {
	try {
		let user = await User.findById(req.userid);
		if (!user.likedPlaces.includes(req.body.placeid)) {
			user.likedPlaces.push(req.body.placeid);
		}
		await user.save();
		res.status(201).send("Successfully added liked place");
		logger("Successfully added liked place");
	} catch (err) {
		res.status(400).json({ message: err.message });
	}
});

module.exports = router;
