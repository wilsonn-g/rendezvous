const User = require("../../models/user");

/*
 *  The following function recalculates the common interests and likedPlaces among the members of a group.
 *  The input needs to be a valid group object and the function is called when a user is added or removed to a group.
 */
async function calculateCommonDetails(group) {
	let members = group.members;
	let commonInterests = new Set();
	let commonLikedPlaces = new Set();
	let commonAvailability = new Array(48 * 7).fill(true);

	for (let i = 0; i < members.length; i++) {
		const memberId = members[parseInt(i, 10)];
		let user = await User.findById(memberId);

		user.interests.forEach((interest) => {
			commonInterests.add(interest);
		});

		user.likedPlaces.forEach((place) => {
			commonLikedPlaces.add(place);
		});

		let memberAvailability = user.availability;

		memberAvailability.forEach((a, j) => {
			commonAvailability[parseInt(j, 10)] = commonAvailability[parseInt(j, 10)] && a;
		});
	}
	group.interests = [...commonInterests];
	group.likedPlaces = [...commonLikedPlaces];
    group.commonAvailability = commonAvailability;
    await group.save();
}

module.exports = calculateCommonDetails;