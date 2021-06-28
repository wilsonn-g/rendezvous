const debug = require("debug");
const logger = debug("app:error");
const express = require("express");
const router = express.Router();
const User = require("../../models/user");

/*
 *	GET request to see a user's friendrequests
 */
router.get("/", async (req, res) => {    
    try {
        let user = await User.findById(req.userid);
        res.json(user.friendRequests);
	} catch (err) {
		return res.status(404).send("User not found");
	}
});

/*
 *	POST request to add a friend request
 */
router.post("/", async (req, res) => {
	try {
		let user = await User.findById(req.userid);
		if (!user.friendRequests.includes(req.body.userid)) {
			user.friendRequests.push(req.body.userid);
		}
		await user.save();
		res.status(201).send("Successfully added friend request");
		logger("Successfully added friend request");
	} catch (err) {
		res.status(400).json({ message: err.message });
	}
});

/*
 *	DELETE request to remove a friendrequest.
 */
router.delete("/", async (req, res) => {
	try {
		let user = await User.findById(req.userid);

		if (user.friendRequests.includes(req.body.userid)) {
			user.friendRequests.pull(req.body.userid);
		}
		await user.save();
		res.status(200).send("Successfully removed friend request");
		logger("Successfully removed friend request");
	} catch (err) {
		res.status(400).send("Bad request");
	}
});

module.exports = router;
