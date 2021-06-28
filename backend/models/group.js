const mongoose = require("mongoose");

const groupSchema = new mongoose.Schema({
    groupname: {
        type: String,
        required: true
    },
    lastMessage: {
        type: String
    },
    lastSender: {
        type: String
    },
    members: [String],
    interests: [String],
    likedPlaces: [String],
    commonAvailability: [Boolean],
    plannedEvents: [{
        placeid: {
            type: String
        },
        datetime: {
            type: Number
        },
        groupname: {
            type: String
        }
    }],
    votingEvents: [{
        placeid: {
            type: String
        },
        datetime: {
            type: Number
        },
        photo: {
            type: String
        },
        membersYes: [String],
        membersTotal: [String],
        senderId: {
            type: String
        },
        placeName: {
            type: String
        },
        senderName: {
            type: String
        }
    }],
    messages: [{
        senderName: {
            type: String
        },
        message: {
            type: String,
        },
        timeStamp: {
            type: Date
        }
    }]
}, {collection: "groupDB"});

module.exports = mongoose.model("Group", groupSchema);
