const debug = require("debug");
const logger = debug("app:error");
const express = require("express");
const router = express.Router();
const Group = require("../../models/group");
const User = require("../../models/user");
const calcCommonHelper = require("./calcCommonHelper");

/*
 *	GET request to get members of a specific group.
 */
router.get("/", async (req, res) => {
	try {
        let group = await Group.findById(req.groupid);
        res.json(group.members);
	} catch (err) {
		return res.status(404).send("Group not found");
	}
});

/*
 *	POST request to add members to a group.
 */
router.post("/", async (req, res) => {
	try {
		let group = await Group.findById(req.groupid);
		if (!group.members.includes(req.body.member)) {
			group.members.push(req.body.member);
			await group.save();
        }
        logger("Adding member (" + req.body.member + ") to " + req.groupid);

		//reconfigure common interests and liked places
        await calcCommonHelper(group);

		res.status(201).send("Successfully added member");
		logger("Successfully added member");
	} catch (err) {
		res.status(400).json({ message: err.message });
	}
});

/*
 *	DELETE members of a specific group.
 */
router.delete("/", async (req, res) => {
	try {
		let group = await Group.findById(req.groupid);

		if (group.members.includes(req.body.member)) {
			group.members.pull(req.body.member);
		}

		//reconfigure common interests and liked places
        await calcCommonHelper(group);
        
		res.status(200).send("Successfully removed member");
		logger("Successfully removed member");
	} catch (err) {
		res.status(400).send("Bad request");
	}
});

module.exports = router;