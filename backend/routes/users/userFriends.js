const debug = require("debug");
const logger = debug("app:error");
const express = require("express");
const router = express.Router();
const User = require("../../models/user");

/*
 *	GET request to see a user's friends list.
 */
router.get("/", async (req, res) => {    
    try {
        let user = await User.findById(req.userid);
        res.json(user.friends);
	} catch (err) {
		return res.status(404).send("User not found");
	}
});

/*
 *	POST request to add a friend.
 */
router.post("/", async (req, res) => {
	try {
		let user = await User.findById(req.userid);
		if (!user.friends.includes(req.body.userid)) {
			user.friends.push(req.body.userid);
		}
		await user.save();
		res.status(201).send("Successfully added friend");
		logger("Successfully added friend");
	} catch (err) {
		res.status(400).json({ message: err.message });
	}
});

/*
 *	DELETE request to remove a friend.
 */
router.delete("/", async (req, res) => {
	try {
		let user = await User.findById(req.userid);

		if (user.friends.includes(req.body.userid)) {
			user.friends.pull(req.body.userid);
		}
		await user.save();
		res.status(200).send("Successfully removed friend");
		logger("Successfully removed friend");
	} catch (err) {
		res.status(400).send("Bad request");
	}
});

module.exports = router;
