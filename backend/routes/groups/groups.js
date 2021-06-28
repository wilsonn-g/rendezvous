const debug = require("debug");
const logger = debug("app:error");
const express = require("express");
const router = express.Router();
const Group = require("../../models/group");
const calcCommonHelper = require("./calcCommonHelper");

/*
 *	Middleware function to send in the userid to child routers.
 */
function sendGroupId(req, res, next) {
	req.groupid = req.params.groupid;
	next();
}

//Routers for different functionalities
const groupMembersRouter = require("./groupMembers");
const groupPlannedRouter = require("./groupPlanned");
const groupVotingRouter = require("./groupVoting");
const groupOptimalTimesRouter = require("./groupOptimalTimes");
const groupVoteRouter = require("./groupVote");

router.use("/:groupid/members", sendGroupId, groupMembersRouter);
router.use("/:groupid/planned", sendGroupId, groupPlannedRouter);
router.use("/:groupid/voting", sendGroupId, groupVotingRouter);
router.use("/:groupid/optimaltimes", sendGroupId, groupOptimalTimesRouter);
router.use("/:groupid/vote", sendGroupId, groupVoteRouter);

/*
 *	Helper function to get a group by groupid.
 */
async function getGroup(req, res, next) {
	let group;
	try {
		group = await Group.findById(req.params.groupid);
	} catch (err) {
		return res.status(404).send("Cannot find group");
	}
	res.group = group;
	next();
}

/*
 *	POST request to create a new group.
 */
router.post("/", async (req, res) => {
	try {
		const group = new Group({
			groupname: req.body.groupname,
			lastMessage: "",
			lastSender: ""
		});
		await group.save();
		res.status(201).json(group);
	} catch (err) {
		res.status(400).json({ message: err.message });
	}
});

/*
 *	GET request for a specific group.
 */
router.get("/:groupid", getGroup, async (req, res) => {
	await calcCommonHelper(res.group);
	res.json(res.group);
});

/*
 *	DELETE request on a specific group by groupid
 */
router.delete("/:groupid", getGroup, (req, res) => {
	res.group.remove();
	res.send("Deleted group");
	logger("Deleted group");
});

/*
 * GET request for client-end users to retrieve messages
 */
router.get("/:groupid/messages", getGroup, (req, res) => {
	res.json(res.group.messages);
});

module.exports = router;
