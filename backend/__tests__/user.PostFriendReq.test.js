const supertest = require('supertest');
const app = require('../index.js');
const User = require("../models/user");
const mongoose = require('mongoose');
const request = supertest(app);
const databaseName = 'usertestdb'

beforeAll(async () => {
    const url = `mongodb://127.0.0.1'/${databaseName}`;
    await mongoose.connect(url, {useNewUrlParser: true,useUnifiedTopology: true});
});


async function removeAllCollections () {
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
// Test for POST request for all groups of a given user
describe("test POST /:userid/friendrequests", () => {
  it ("should post a new friend req to friend request array", async (done) => {
        let mockUser = new User ({
          friendRequests: ["12345","54321"],
        });

        await mockUser.save();
        let req = "userid= 90210";

        let getres = await request.post("/users/"+mockUser._id+"/friendrequests").send(req).expect(201);
        expect(getres.text == "Successfully added friend request").toBe(true);

        done();
  });

  it ("should 400 on bad user id input", async (done) => {
        let badUserId = 2103;

        let req = "userid=90210";

        let getres = await request.post("/users/"+badUserId+"/friendrequests").send(req).expect(400);
        done();
  });

  it ("should return a 201 status but friend request is already in array", async (done) => {
        let mockUser = new User ({
          friendRequests: ["12345","54321"],
        });

        await mockUser.save();
        let req = "userid=12345";

        let getres = await request.post("/users/"+mockUser._id+"/friendrequests").send(req).expect(201);
        expect(getres.text == "Successfully added friend request").toBe(true);

        done();
  });

});
