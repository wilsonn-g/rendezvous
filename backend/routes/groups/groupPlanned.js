const debug = require("debug");
const logger = debug("app:error");
const express = require("express");
const router = express.Router();
const Group = require("../../models/group");

/*
 *	GET request on planned events of a specific group.
 */
router.get("/", async (req, res) => {
	try {
		let group = await Group.findById(req.groupid);
		let curDate = new Date();
		group.plannedEvents.forEach((event, index) => {
			if (event.datetime < curDate.getTime()-3600*8000) {
				group.plannedEvents.pull(event);
			}
		});
		await group.save();
        res.json(group.plannedEvents);
	} catch (err) {
		return res.status(404).send("Group not found");
	}
});

module.exports = router;