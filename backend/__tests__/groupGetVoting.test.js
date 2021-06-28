const supertest = require('supertest');
const app = require('../index.js');
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
describe("test GET /groups/voting", () => {
  it ("should return a list of planned events", async (done) => {
        let mockGroup1 = new Group ({
          groupname: "fun",
          votingEvents: [{datetime: 121231762, placeid: "ChIJDzRjfrRyhlQR8SRHq2eOtwk", photo: "photo", placeName: "name", senderId: "a", membersYes: ["a"], membersTotal: ["a"], senderName: "samplename"}]
        });

        await mockGroup1.save();

        let res = await request.get("/groups/"+mockGroup1._id+"/voting").expect(200);
        expect(res.body[0].datetime === mockGroup1.votingEvents[0].datetime).toBe(true);
        expect(res.body[0].placeid === mockGroup1.votingEvents[0].placeid).toBe(true);
        done();
    });
    it ("should 404 if no such group exists", async (done) => {
          let res = await request.get("/groups/123/voting").expect(404);
          done();
    });
});
