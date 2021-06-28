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
describe("test POST /:userid/friends", () => {
  it ("should post a new userid to friends array", async (done) => {
        let mockUser = new User ({
          friends: []
        });

        await mockUser.save();
        let req = "userid= 90210";

        let getres = await request.post("/users/"+mockUser._id+"/friends").send(req).expect(201);
        expect(getres.text == "Successfully added friend").toBe(true);

        done();
  });

  it ("should return a list of friends the user has", async (done) => {
        let badUserId = 2103;

        let req = "userid=90210";

        let getres = await request.post("/users/"+badUserId+"/friends").send(req).expect(400);
        done();
  });

  it ("should return a 201 status but friend is already a friend", async (done) => {
        let mockUser = new User ({
          friends: ["12345","54321"],
        });

        await mockUser.save();
        let req = "userid=12345";

        let getres = await request.post("/users/"+mockUser._id+"/friends").send(req).expect(201);
        expect(getres.text == "Successfully added friend").toBe(true);

        done();
  });

});
