let radius = "10000";
let distanceWeightCoefficient = 10;
let interestWeightCoefficient = 2;
let maxPlacesReturned = 10;

function interestTypeMap() {
    var typeMap = new Map();
    typeMap.set("shopping", ["shopping_mall", "clothing_store", "jewelry_store", "store"]);
    typeMap.set("food", ["restaurant", "cafe", "bakery", "meal_takeaway"]);
    typeMap.set("spirituality", ["church", "hindu_temple", "mosque", "synagogue"]);
    typeMap.set("nature", ["park", "campground", "zoo"]);
    typeMap.set("movies", ["movie_theater"]);
    typeMap.set("art", ["art_gallery", "museum"]);
    typeMap.set("nightlife", ["night_club", "bar", "casino"]);
    typeMap.set("dessert", ["bakery", "cafe"]);
    typeMap.set("literature", ["library", "museum"]);
    typeMap.set("healthsafety", ["spa", "beauty_salon", "gym"]);
    typeMap.set("animals", ["zoo", "aquarium"]);
    typeMap.set("sports", ["stadium", "bowling_alley"]);
    typeMap.set("general", ["amusement_park", "aquarium", "bowling_alley", "shopping_mall"]);
    return typeMap;
}

function interestWeightMap() {
    var weightMap = new Map();
    weightMap.set("tourist_attraction", 3);
    weightMap.set("shopping_mall", 7);
    weightMap.set("clothing_store", 1);
    weightMap.set("jewelry_store", 1);
    weightMap.set("store", 1);
    weightMap.set("restaurant", 7);
    weightMap.set("cafe", 3);
    weightMap.set("bakery", 3);
    weightMap.set("meal_takeaway", 1);
    weightMap.set("park", 3);
    weightMap.set("campground", 1);
    weightMap.set("zoo", 1);
    weightMap.set("movie_theater", 3);
    weightMap.set("art_gallery", 3);
    weightMap.set("museum", 3);
    weightMap.set("bar", 3);
    weightMap.set("night_club", 3);
    weightMap.set("casino", 1);
    weightMap.set("library", 1);
    weightMap.set("spa", 3);
    weightMap.set("beauty_salon", 2);
    weightMap.set("gym", 2);
    weightMap.set("aquarium", 6);
    weightMap.set("stadium", 4);
    weightMap.set("bowling_alley", 2);
    weightMap.set("amusement_park", 6);
    weightMap.set("church", 2);
    weightMap.set("hindu_temple", 2);
    weightMap.set("mosque", 2);
    weightMap.set("synagogue", 1);
    return weightMap;
}


module.exports = {
    interestTypeMap,
    interestWeightMap,
    radius,
    distanceWeightCoefficient,
    interestWeightCoefficient,
    maxPlacesReturned
};
