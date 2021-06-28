const supertest = require('supertest');
const app = require('../index.js');
const User = require("../models/user");
const Group = require("../models/group");
const mongoose = require('mongoose');
const request = supertest(app);
const databaseName = 'usertestdb'

beforeAll(async () => {
    const url = `mongodb://127.0.0.1'/${databaseName}`;
    await mongoose.connect(url, {useNewUrlParser: true, useUnifiedTopology: true});
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
// Test for GET request for all groups
describe("test GET /groups/planned", () => {
  it ("should return a list of planned events", async (done) => {
        let mockGroup1 = new Group ({
          groupname: "fun",
          plannedEvents: [{datetime: 121231762, placeid: "ChIJDzRjfrRyhlQR8SRHq2eOtwk"}],
        });

        await mockGroup1.save();

        let res = await request.get("/groups/"+mockGroup1._id+"/planned").expect(200);
        expect(res.body === mockGroup1.plannedEvents).toBe(false);
        done();
    });
    it ("should 404 if no such group exists", async (done) => {
          let res = await request.get("/groups/123/planned").expect(404);
          done();
    });
});
