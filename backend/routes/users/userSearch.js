const debug = require("debug");
const logger = debug("app:error");
const express = require("express");
const router = express.Router();
const User = require("../../models/user");

/*
 *	GET request to find all users with a firstname or last name who match a query.
 */
router.get("/:query", async (req, res) => {
	try {
		/*if (req.params.query === undefined || req.userid === undefined) {
			return res.status(400).send("Not a valid query");
		}*/
		const searchQuery = ".*" + req.params.query.toString() + ".*";
		let user = await User.findById(req.userid);
		var returnList = [];
		const users = await User.find({ "name": { $regex: searchQuery, $options: "i" } });

		users.forEach((matchingUser) => {
			let matchingUserId = matchingUser._id.toString();
			if (matchingUserId === req.userid.toString() || user.friends.includes(matchingUserId)) {
				return;
			}
			returnList.push(matchingUserId);
		});
		res.status(201).json(returnList);
	} catch (err) {
		res.status(400).send("Not a valid query");
	}
});

module.exports = router;
