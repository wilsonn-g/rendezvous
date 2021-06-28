const mongoose = require("mongoose");

const placeSchema = new mongoose.Schema({
    placeid: {
        type: String,
        required: true
    },
    name: {
        type: String
    },
    lat: {
        type: Number
    },
    lng: {
        type: Number
    },
    photo: {
        type: String
    }
}, { collection: "placeDB" });

module.exports = mongoose.model("Place", placeSchema);