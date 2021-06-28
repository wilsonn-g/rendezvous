const debug = require("debug");
const logger = debug("app:error");
const express = require("express");
const router = express.Router();
const User = require("../../models/user");
const passwordHelper = require("./passwordHelper");

/*
 *	POST request to login a user. Takes in email and password. Returns the userid.
 */
router.post("/", async (req, res) => {
	try {
		const userEmail = req.body.email;
		const user = await User.findOne({ "email": userEmail });

		const inputPassword = req.body.password;
		const salt = user.salt;
		const encryptedPassword = user.password;
		const hashedPassword = passwordHelper.checkHashPassword(inputPassword, salt).passwordHash;
		if (encryptedPassword === hashedPassword) {
			user.deviceToken = req.body.token;
			logger(req.body.token);
			await user.save();
			res.status(201).json(`Success:${user._id}`);
			logger(`Success:${user._id}`);
		} else {
			res.status(400).send("Incorrect Password");
			logger("Incorrect Password");
		}
	} catch (err) {
		res.status(400).json({ message: err.message });
	}
});

module.exports = router;