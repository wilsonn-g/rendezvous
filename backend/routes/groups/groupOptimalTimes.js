const debug = require("debug");
const logger = debug("app:error");
const express = require("express");
const router = express.Router();
const Group = require("../../models/group");
const User = require("../../models/user");

/*
 *	This function takes in a valid group object and returns a list of sorted availability indexes. 
 */
async function getOptimalTimes(group) {
	let members = group.members;

	let returnList = [];
	let optimalList = [];

	group.commonAvailability.forEach((available, index) => {
		if (available) {
			if (index % 48 > 23) {
				optimalList.push(index);
			} else {
				returnList.push(index);
			} 
		}
	});

	if (optimalList.length >= 3) {
		return optimalList.slice(0,3);
	}
	let timeMap = new Map();
	for (let i = 0; i < members.length; i++) {
		const memberId = members[parseInt(i, 10)];
		let user = await User.findById(memberId);
		let memberAvailability = user.availability;
		memberAvailability.forEach((value, index) => {
			let count = timeMap.get(index);
			if (count == null) {
				count = 0;
			}
			if (value) {
				if (index % 48 > 23) {
					count+=2;
				}
				timeMap.set(index, ++count);
			}
		});
	}
	returnList = [];
	let sortedTimeMap = new Map([...timeMap.entries()].sort((a, b) => b[1] - a[1]));
	sortedTimeMap.forEach((key, value) => {
		if (value > 0 && returnList.length < 3) {
			returnList.push(key);
		}
	});

	return returnList;
}

/*
 *	GET request on optimal times of a group
 */
router.get("/", async (req, res) => {
	try {
        let group = await Group.findById(req.groupid);
        let optimalList = await getOptimalTimes(group);
		logger("optimalList:" + optimalList);
		res.json(optimalList);
	} catch (err) {
		return res.status(404).send("Group not found");
	}
});


module.exports = router;