const debug = require("debug");
const logger = debug("app:error");
const express = require("express");
const router = express.Router();
const Group = require("../../models/group");

/*
 *	POST request to vote for a place and add planned event if necessary. Requires userid, datetime (in ms), placeid, and vote
 */
router.post("/", async (req, res) => {
	try {
		let group = await Group.findById(req.groupid);
		logger(req.body);
		
		group.votingEvents.forEach((event, index) => {
			if (req.body.placeid === event.placeid && Number(req.body.datetime) === event.datetime) {
				if (event.membersTotal.includes(req.body.userid)) {
					return;
				}
				event.membersTotal.push(req.body.userid);
				if (req.body.vote === "yes") {
					event.membersYes.push(req.body.userid);
				}

				if (event.membersTotal.length > 0.6 * group.members.length) {
					if (event.membersYes.length > 0.5 * event.membersTotal.length) {
						let newEvent = { "placeid": event.placeid, "datetime": event.datetime, "groupname": group.groupname };
						group.plannedEvents.push(newEvent);
					}
					group.votingEvents.pull(event);
				}
			}
		});

		await group.save();


		res.status(201).send("Successfully added vote");
		logger("Successfully added vote");
	} catch (err) {
		res.status(400).json({ message: err.message });
	}
});

module.exports = router;