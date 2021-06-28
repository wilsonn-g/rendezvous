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
describe("test GET /groups", () => {
  it ("should return a list of all groups", async (done) => {
        let mockUser1 = new User();
        let mockUser2 = new User();

        await mockUser1.save();
        await mockUser2.save();

        let mockGroup1 = new Group ({
          groupname: "fun",
          members: [mockUser1._id,mockUser2._id],
        });

        await mockGroup1.save();

        let res = await request.get("/groups/"+mockGroup1._id).expect(200);
        expect(res.body._id==mockGroup1._id).toBe(true);
        done();
    });

    it ("should 404 if the group does not exist", async (done) => {
          let res = await request.get("/groups/123").expect(404);
          done();
    });
    it ("should return a list of messages for the group", async (done) => {
        let mockGroup1 = new Group ({
          groupname: "fun",
          messages: [{senderName: "Guy", message: "hi"}, {senderName: "Preet", message: "hello"}]
        });

        await mockGroup1.save();

        let res = await request.get("/groups/"+mockGroup1._id+"/messages").expect(200);
        expect(res.body[0].senderName === mockGroup1.messages[0].senderName).toBe(true);
        expect(res.body[0].message === mockGroup1.messages[0].message).toBe(true);
        done();
    });
});
