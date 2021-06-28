const supertest = require('supertest');
const app = require('../index.js');
const User = require("../models/user");
const Group = require("../models/group");
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

            (error.message);
        }
    }
}

// Disconnect Mongoose
afterAll(async () => {
    await dropAllCollections();
    await mongoose.connection.close();
})

// Test for POST request for adding a group to a list of users
describe("test GET /groups/members", () => {
  it ("should successfully GET members of a group", async (done) => {
      let mockUser1 = new User ({
        interests: ['sports','nature']
      });
      await mockUser1.save();

      let mockGroup = new Group ({
        groupname: "Komrades",
        members: [`${mockUser1._id}`],
      });
      await mockGroup.save();

      let res = await request.get("/groups/"+mockGroup._id+"/members").expect(200);
      done();
  });
  it ("should return error 404 since we provide invalid groupid", async (done) => {
      let res = await request.get("/groups/9000/members").expect(404);
      expect(res.text === "Group not found").toBe(true);
      done();
  });
});
