const debug = require("debug");
const logger = debug("app:error");
const express = require("express");
const router = express.Router();
const User = require("../../models/user");

/*
 *	POST request to add a seen place.
 */
router.post("/", async (req, res) => {
	try {
		let user = await User.findById(req.userid);
		if (!user.seenPlaces.includes(req.body.placeid)) {
			user.seenPlaces.push(req.body.placeid);
		}
		await user.save();
		res.status(201).send("Successfully added seen place");
		logger("Successfully added seen place");
	} catch (err) {
		res.status(400).json({ message: err.message });
	}
});

module.exports = router;