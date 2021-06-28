const supertest = require('supertest');
const app = require('../../index.js');
const User = require("../../models/user");
const mongoose = require('mongoose');
const databaseName = 'usertestdb';
const request = supertest(app);

beforeAll(async () => {
    const url = `mongodb://127.0.0.1'/${databaseName}`;
    await mongoose.connect(url, { useNewUrlParser: true, useUnifiedTopology: true });
});


async function removeAllCollections() {
    const collections = Object.keys(mongoose.connection.collections);
    for (const collectionName of collections) {
        const collection = mongoose.connection.collections[collectionName];
        await collection.deleteMany();
    }
}

afterEach(async () => {
    await removeAllCollections();
})


async function dropAllCollections() {
    const collections = Object.keys(mongoose.connection.collections)
    for (const collectionName of collections) {
        const collection = mongoose.connection.collections[collectionName]
        try {
            await collection.drop();
        } catch (error) {
            // This error happens when you try to drop a collection that's already dropped. Happens infrequently.
            // Safe to ignore.
            if (error.message === 'ns not found') return

            // This error happens when you use it.todo.
            // Safe to ignore.
            if (error.message.includes('a background operation is currently running')) return

            console.log(error.message);
        }
    }
}

// Disconnect Mongoose
afterAll(async () => {
    await dropAllCollections();
    await mongoose.connection.close();
})

describe("testing discover places sequence", () => {
    it("checks if users can like, bookmark, or dislike places", async (done) => {
        let email = "demo@gmail.com";
        let password = "pass";
        let firstname = "Guy";
        let lastname = "Lusina";
        let res1 = await request.post("/users")
            .send("email=" + email + "&password=" + password + "&firstname=" + firstname + "&lastname=" + lastname)
            .expect(201);

        let res2 = await request.post("/users/login")
            .send("email=" + email + "&password=" + password)
            .expect(201);

        let userid = res1.body._id.toString();

        let req = {interests: ["nature", "sports"], availablity:[false]};

        let res3 = await request.put("/users/"+userid)
            .send(req)
            .expect(200);

        let res4 = await request.post("/recommend/users/"+userid).send("lat=49.2643993&lng=-123.2318585").expect(201);

        let returnList = res4.body.placeids;

        expect(returnList.length===10).toBe(true);

        for (let i = 0; i < 10; i++) {
            res4 = await request.post("/users/"+userid+"/seen").send("placeid="+returnList[i]).expect(201);
            if (i < 3) {
            } else if (i < 7) {
                res4 = await request.post("/users/"+userid+"/liked").send("placeid="+returnList[i]).expect(201);
            } else {
                res4 = await request.post("/users/"+userid+"/bookmarked").send("placeid="+returnList[i]).expect(201);
                res4 = await request.post("/users/"+userid+"/seen").send("placeid="+returnList[i]).expect(201);
            }
        }

        let user = await User.findById(userid);

        for (let j = 0; j < 10; j++) {
            expect(user.seenPlaces.includes(returnList[j])).toBe(true);
            if (j < 3) {
                expect(user.likedPlaces.includes(returnList[j])).toBe(false);
                expect(user.bookmarkedPlaces.includes(returnList[j])).toBe(false);
            } else if (j < 7) {
                expect(user.likedPlaces.includes(returnList[j])).toBe(true);
                expect(user.bookmarkedPlaces.includes(returnList[j])).toBe(false);
            } else {
                expect(user.seenPlaces.includes(returnList[j])).toBe(true);
                expect(user.bookmarkedPlaces.includes(returnList[j])).toBe(true);
            }
        }

        done();
    });


});
