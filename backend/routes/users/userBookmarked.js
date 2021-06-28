const debug = require("debug");
const logger = debug("app:error");
const express = require("express");
const router = express.Router();
const User = require("../../models/user");

/*
 *	GET request to see a user's lsit of bookmarked places
 */
router.get("/", async (req, res) => {    
    try {
        let user = await User.findById(req.userid);
        res.json(user.bookmarkedPlaces);
	} catch (err) {
		return res.status(404).send("User not found");
	}
});

/*
 *	POST request to add a bookmarked place.
 */
router.post("/", async (req, res) => {
	try {
		let user = await User.findById(req.userid);
		if (!user.bookmarkedPlaces.includes(req.body.placeid)) {
			user.bookmarkedPlaces.push(req.body.placeid);
		}
		await user.save();
		res.status(201).send("Successfully added bookmarked place");
		logger("Successfully added bookmarked place");
	} catch (err) {
		res.status(400).json({ message: err.message });
	}
});

/*
 *	DELETE request to remove a bookmarked place.
 */
router.delete("/", async (req, res) => {
	try {
		let user = await User.findById(req.userid);

		if (user.bookmarkedPlaces.includes(req.body.placeid)) {
			user.bookmarkedPlaces.pull(req.body.placeid);
		}
		await user.save();
		res.status(200).send("Successfully removed bookmarked place");
		logger("Successfully removed bookmarked place");
	} catch (err) {
		res.status(400).send("Bad request");
	}
});

module.exports = router;
