const mongoose = require("mongoose");

//Defines the schema for a user
const userSchema = new mongoose.Schema({
	firstname: {
		type: String
	},
	lastname: {
		type: String
	},
	name: {
		type: String
	},
	email: {
		type: String
	},
	password: {
		type: String
	},
	salt: {
		type: String
	},
	friends: [String],
	groups: [String],
	interests: [String],
	likedPlaces: [String],
	seenPlaces: [String],
	bookmarkedPlaces: [String],
	friendRequests: [String],
	deviceToken: {
		type: String
	},
	availability: [Boolean]
}, { collection: "userDB" });

module.exports = mongoose.model("User", userSchema);
