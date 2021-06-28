const debug = require("debug");
const logger = debug("app:places");
var config = require("../../config");
const express = require("express");
const router = express.Router();
const User = require("../../models/user");
const Group = require("../../models/group");
const Place = require("../../models/place");
const https = require("https");
const variables = require("./variables.js");


// Global variables //
let RADIUS = variables.radius;
let DISTANCE_WEIGHT_COEFFICIENT = variables.distanceWeightCoefficient;
let INTEREST_WEIGHT_COEFFICIENT = variables.interestWeightCoefficient;
let MAX_PLACES_RETURNED = variables.maxPlacesReturned;
let INTEREST_TYPE_MAP = variables.interestTypeMap();
let INTEREST_WEIGHT_MAP = variables.interestWeightMap();

/* Helper to return parsed JSON object from an HTTPS request given an url
 */
var getResponse = (url) => new Promise((resolve, reject) => {
    https.get(url, (res) => {
        let data = "";

        res.on("data", (chunk) => {
            data += chunk;
        });

        //whole response received
        res.on("end", () => {
            resolve(JSON.parse(data));
        });

    }).on("error", (e) => { reject(e); });
});

/*  Given two pairs of coordinates, finds a value to determine the straight-line distance between them.
 *  The higher the return value, the lower the similarity.
 */
const calcDistanceWeight = (userLat, userLng, placeLat, placeLng) => {
    const p = Math.PI / 180;
    const c = Math.cos;

    //formula to calculate distance
    const a = 0.5 - c((placeLat - userLat) * p) / 2 +
        c(userLat * p) * c(placeLat * p) *
        (1 - c((placeLng - userLng) * p)) / 2;
    const distance = 12742 * Math.asin(Math.sqrt(a));

    return distance / DISTANCE_WEIGHT_COEFFICIENT;
};

/*  Helper function to get the weight of a particular type categorized by the Google Places API
 */
const getTypeWeight = (placeType) => {
    return INTEREST_WEIGHT_MAP.get(placeType);
};

/* Finds a weight value corresponding to the similarity between the types that a user is interested in
 * and the types of a place. The higher the return value, the higher the similarity.
 */
const calcInterestWeight = (types, placeTypes) => {
    var weight = 0.0;
    placeTypes.forEach((type, index) => {
        if (types.includes(type)) {
            weight += getTypeWeight(type);
        }
    });
    return weight * INTEREST_WEIGHT_COEFFICIENT;
};

/* Given a list of users interests, returns a list of google places types that the user may like.
 * The list is empty if the user has no interests
 */
const getTypes = (interests) => {
    var types = [];

    interests.forEach((interest, i) => {
        const interestTypeList = INTEREST_TYPE_MAP.get(interest);
        interestTypeList.forEach(function (placeType, j) {
            if (getTypeWeight(placeType) >= 3) {
                types.push(placeType);
            }
        });
    });

    return types;
};

/* Fetches a photo using the Place Photos api call
 * Returns null if placePhoto contains invalid width or photo reference
 */
const fetchPhoto = (placePhoto) => new Promise((resolve, reject) => {
    if (placePhoto == null || placePhoto.width == null || placePhoto.photo_reference == null) {
        return null;
    }

    const url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=" + placePhoto.width.toString() + "&photoreference=" + placePhoto.photo_reference +
        "&key=" + config.API_KEY;

    https.get(url, (res) => {
        let data = "";
        res.on("end", async () => {
            let returnUrl = data.toString().substring(
                data.toString().lastIndexOf("<A HREF=") + 9,
                data.toString().lastIndexOf(">here</A>") - 1);
            resolve(returnUrl);
        });
        res.on("data", (buf) => data += buf.toString());
    })
        .on("error", (e) => { reject(e); });
});

/* Inserts the place into the database if it doesn't already exist
 */
function findAndSavePlace(placeId, name, placeLat, placeLng, placePhoto) {
    try {
        Place.find({ "placeid": placeId }).then(async (place) => {
            if (place[0] == null) {
                const newplace = new Place({
                    placeid: placeId,
                    name,
                    lat: placeLat,
                    lng: placeLng,
                    photo: await fetchPhoto(placePhoto)
                });
                newplace.save();
            }
        });
    } catch (err) {
        logger(err.message);
    }
}

/*
 * Calculates the weight of a given place. Helper function for main algorithms.
 * If the place does not already exist in the databse, it will be inserted here.
 *
 */
function calculateWeightOfPlace(req, parsedPlace, user, types, placeWeightMap, condition) {
    const placeId = parsedPlace.place_id;

    if (placeWeightMap.has(placeId)) {
        return;
    }

    if (condition === "user" && user.seenPlaces.includes(placeId)) {
        return;
    }

    //Fetches all the fields for place entry
    const placeLat = parsedPlace.geometry.location.lat;
    const placeLng = parsedPlace.geometry.location.lng;
    const name = parsedPlace.name;
    const placeTypes = parsedPlace.types;
    let placePhoto = null;
    logger(name);

    if (parsedPlace.photos != null) {
        placePhoto = parsedPlace.photos[0];
    } else {
        placePhoto = null;
    }

    findAndSavePlace(placeId, name, placeLat, placeLng, placePhoto);

    let interestWeight = calcInterestWeight(types, placeTypes);
    let distanceWeight = calcDistanceWeight(req.body.lat, req.body.lng, placeLat, placeLng);

    let placeWeight = interestWeight - distanceWeight;
    //logger(name + " " + interestWeight + " " + distanceWeight);

    placeWeightMap.set(placeId, placeWeight);
}

/* 
 * Given a list of types, this function returns a list of sorted places based on weights for each place in the types.
 */
async function getRecommendationList(req, types, user, condition, returnList) {
    var placeWeightMap = new Map();

    for (var i = 0; i < types.length; i++) {
        const type = types[parseInt(i, 10)];

        const url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + req.body.lat.toString() + "," + req.body.lng.toString() +
            "&radius=" + RADIUS + "&type=" + type + "&key=" + config.API_KEY;

        logger(url);

        let parsedRes = await getResponse(url);

        if (parsedRes.status !== "OK") {
            return;
        }
        parsedRes.results.forEach((parsedPlace) => {
            calculateWeightOfPlace(req, parsedPlace, user, types, placeWeightMap, condition);
        });
    }

    //sort the results based on weight and return the top 10
    let sortedPlaceMap = new Map([...placeWeightMap.entries()].sort((a, b) => b[1] - a[1]));
    sortedPlaceMap.forEach((key, value) => {
        if (returnList.length < MAX_PLACES_RETURNED) {
            returnList.push(value);
        }
    });
    logger(sortedPlaceMap);
}


/*  Recommendation algorithm for the discover screen (each user).
 *  Requires user latitude and longitude and returns a list of up to MAX_PLACES_RETURNED places by placeid
 */
router.post("/users/:userid", async (req, res) => {
    try {
        let user = await User.findById(req.params.userid);

        var userTypes = ["tourist_attraction"];
        userTypes = userTypes.concat(getTypes(user.interests));

        var returnList = [];
        await getRecommendationList(req, userTypes, user, "user", returnList);

        logger(returnList);

        return res.status(201).json({ "placeids": returnList });
    } catch (err) {
        return res.status(400).json({ message: err.message });
    }
});

/*  Suggestion algorithm for the suggestion screen (each group).
 *  Requires user latitude and longitude and returns a list of up to MAX_PLACES_RETURNED places by placeid
 */
router.post("/groups/:groupid", async (req, res) => {
    try {
        let group = await Group.findById(req.params.groupid);

        var groupTypes = ["tourist_attraction"];
        groupTypes = groupTypes.concat(getTypes(group.interests));

        var returnList = [];
        returnList = returnList.concat(group.likedPlaces);
        await getRecommendationList(req, groupTypes, null, "group", returnList);

        return res.status(201).json({ "placeids": returnList });
    } catch (err) {
        logger(err.message);
        return res.status(400).json({ message: err.message });
    }
});

router.post("/search", async (req, res) => {
    try {
        let returnList = [];
        const url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + req.body.query.replace(" ", "+") + "&location=" + req.body.lat.toString() + "," + req.body.lng.toString() +
            "&radius=20000&key=" + config.API_KEY;
        logger(url);
        const parsedRes = await getResponse(url);
        if (parsedRes.status !== "OK") {
            res.status(400).send("Invalid query");
        }

        parsedRes.results.forEach((parsedPlace) => {
            const placeId = parsedPlace.place_id;
            const placeLat = parsedPlace.geometry.location.lat;
            const placeLng = parsedPlace.geometry.location.lng;
            const name = parsedPlace.name;
            let placePhoto = null;

            if (parsedPlace.photos != null) {
                placePhoto = parsedPlace.photos[0];
            } else {
                placePhoto = null;
            }

            findAndSavePlace(placeId, name, placeLat, placeLng, placePhoto);

            returnList.push(placeId);
        });
        logger(returnList);
        res.status(201).json({ "placeids": returnList });

    } catch (err) {
        res.status(400).json({ message: err.message });
    }
});

module.exports = router;
