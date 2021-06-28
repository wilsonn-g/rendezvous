const mongoose = require("mongoose");

const messageSchema = new mongoose.Schema({
    senderName: {
        type: String,
    },
    message: {
        type: String
    },
    timeStamp: {
        type: Date
    }
}, {collection: "groupDB"});

module.exports = mongoose.model("Message", messageSchema);
