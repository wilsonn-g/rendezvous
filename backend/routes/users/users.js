const debug = require("debug");
const logger = debug("app:error");
const express = require("express");
const router = express.Router();
const User = require("../../models/user");

/*
 *	Middleware function to send in the userid to child routers.
 */
function sendUserId(req, res, next) {
	req.userid = req.params.userid;
	next();
}

//Routers for different functionalities
const userRegisterRouter = require("./userRegister");
const userLoginRouter = require("./userLogin");
const userGroupsRouter = require("./userGroups");
const userFriendReqRouter = require("./userFriendRequests");
const userFriendRouter = require("./userFriends");
const userBookmarkedRouter = require("./userBookmarked");
const userLikedRouter = require("./userLiked");
const userSeenRouter = require("./userSeen");
const userSearchRouter = require("./userSearch");

router.use("/:userid/friends", sendUserId, userFriendRouter);
router.use("/:userid/groups", sendUserId, userGroupsRouter);
router.use("/:userid/friendrequests", sendUserId, userFriendReqRouter);
router.use("/:userid/bookmarked", sendUserId, userBookmarkedRouter);
router.use("/:userid/liked", sendUserId, userLikedRouter);
router.use("/:userid/seen", sendUserId, userSeenRouter);
router.use("/:userid/search", sendUserId, userSearchRouter);
router.use("/", userRegisterRouter);
router.use("/login", userLoginRouter);

/*
 *	Helper function to get a user by userid.
 */
async function getUser(req, res, next) {
	let user;
	try {
		user = await User.findById(req.params.userid);
	} catch (err) {
		return res.status(404).send(err.message);
	}
	res.user = user;
	next();
}

/*
 *	GET request for a specific user.
 */
router.get("/:userid", getUser, (req, res) => {
	res.json(res.user);
});

/*
 *	DELETE request to delete a user.
 */
router.delete("/:userid", getUser, (req, res) => {
	res.user.remove();
	res.send("Deleted user");
	logger("Deleted user");
});

/*
 *	UPDATE request on fields of user
 */
router.put("/:userid", async (req, res) => {
	try {
		let user = await User.findById(req.params.userid);
		logger(req.body);
		
		if (req.body.interests != null) {
			user.interests = req.body.interests;
		}
		if (req.body.availability != null) {
			user.availability = req.body.availability;
		}
		await user.save();
		res.status(200).send("Successfully updated user details");
	} catch (err) {
		res.status(400).send(err.message);
	}
});

module.exports = router;
