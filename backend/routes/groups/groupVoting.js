const debug = require("debug");
const logger = debug("app:error");
const express = require("express");
const router = express.Router();
const Group = require("../../models/group");
const User = require("../../models/user");
const admin = require("firebase-admin");

/*
 *	Finds the next occurence of a given timeIndex given a current date and returns it as a datetime object.
 */
function getNextOccurenceOfTime(date, timeIndex) {
	const dayOfWeek = (Math.floor(timeIndex / 48) + 1) % 8; //0 is sunday
	const minutesSinceMidnight = (timeIndex % 48) * 30;

	var resultDate = new Date(date.getTime()-3600*8*1000);
	resultDate.setDate(resultDate.getDate() + (7 + dayOfWeek - resultDate.getDay()) % 7);
	resultDate.setHours(Math.floor(minutesSinceMidnight / 60));
	resultDate.setMinutes(minutesSinceMidnight % 60);
	resultDate.setSeconds(0);
	resultDate.setMilliseconds(0);

	return resultDate;
}

/*
 *	GET request on voting events of a specific group.
 */
router.get("/", async (req, res) => {
	try {
        let group = await Group.findById(req.groupid);
        res.json(group.votingEvents);
	} catch (err) {
		return res.status(404).send("Group not found");
	}
});

/*
 *	POST request to suggest events to a group. Takes in the time (as an index) and the placeid and senderId.
 */
router.post("/", async (req, res) => {
	try {
        let group = await Group.findById(req.groupid);
		const members = group.members;
		const timeIndex = req.body.index;
		//refactor this into function
		members.forEach(async (userid) => {
			let user = await User.findById(userid);
			let deviceToken = user.deviceToken;
			if (deviceToken === "") {
				return;
			}

			// FCM Variables
			const payload = {
				notification: {
					title: "Rendezvous",
					body: req.body.sendername.toString() + "has suggested an event to your group",
				}
			};

			const options = {
				priority: "high",
				timeToLive: 86400, // 1 day
			};

			admin.messaging().sendToDevice(deviceToken, payload, options);
		});
		const nextDay = getNextOccurenceOfTime(new Date(), timeIndex);

		const event = { "placeid": req.body.placeid, "photo": req.body.photo, "placeName": req.body.placeName, "datetime": nextDay.getTime(), "senderId": req.body.senderId, "membersYes": [req.body.senderId], "membersTotal": [req.body.senderId], "senderName": req.body.sendername };
		group.votingEvents.push(event);
		await group.save();
		res.status(201).json(event);
		logger("Successfully added event");
	} catch (err) {
		res.status(400).json({ message: err.message });
	}
});

module.exports = router;
