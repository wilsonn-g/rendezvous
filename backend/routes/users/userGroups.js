const debug = require("debug");
const logger = debug("app:error");
const express = require("express");
const router = express.Router();
const User = require("../../models/user");

/*
 *	GET a list of all groups associated to a given user.
 */
router.get("/", async (req, res) => {    
    try {
        let user = await User.findById(req.userid);
        res.json(user.groups);
	} catch (err) {
		return res.status(404).send("User not found");
	}
});

/*
 *	POST a new group to the list of user groups.
 */
router.post("/", async (req, res) => {
	try {
		let user = await User.findById(req.userid);
		if (!user.groups.includes(req.body.group)) {
			user.groups.push(req.body.group);
		}
		logger("Adding group (" + req.body.group + ") to " + req.userid);
		await user.save();
		res.status(201).send("Successfully added group");
		logger("Successfully added group");
	} catch (err) {
		res.status(400).json({ message: err.message });
	}
});

/*
 *	DELETE request to remove a group from a user
 */
router.delete("/", async (req, res) => {
	try {
		let user = await User.findById(req.userid);

		if (user.groups.includes(req.body.group)) {
			user.groups.pull(req.body.group);
		}
		await user.save();
		res.status(200).send("Successfully removed group");
		logger("Successfully removed group");
	} catch (err) {
		res.status(400).send("Bad request");
	}
});

module.exports = router;
