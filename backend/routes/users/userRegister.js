const debug = require("debug");
const logger = debug("app:error");
const express = require("express");
const router = express.Router();
const User = require("../../models/user");
const passwordHelper = require("./passwordHelper");

/*
 *	POST request to register a user. Takes in email, password, firstname, and lastname and returns status 201 on success.
 */
router.post("/", async (req, res) => {
	try {
		var availabilityArray = new Array(48 * 7).fill(false);
		let hashData = passwordHelper.saltHashPassword(req.body.password);
		const user = new User({
			firstname: req.body.firstname,
			lastname: req.body.lastname,
			name: req.body.firstname + " " + req.body.lastname,
			email: req.body.email,
			deviceToken: "",
			password: hashData.passwordHash,
			salt: hashData.salt,
			availability: availabilityArray
		});

		User.countDocuments({ "email": req.body.email }, async function (err, count) {

			if (count !== 0) {
				res.status(400).send("User already exists with that email");
				logger("User already exists with that email");
			} else {
				await user.save();
				logger("User created");
				res.status(201).json(user);
			}

		});
	} catch (err) {
		res.status(400).json({ message: err.message });
	}
});

module.exports = router;