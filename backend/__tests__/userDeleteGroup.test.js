const supertest = require('supertest');
const app = require('../index.js');
const User = require("../models/user");
const mongoose = require('mongoose');
const request = supertest(app);
const databaseName = 'usertestdb';

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

// Test for DELETE request to remove a group from a user
describe("test DELETE /:userid/groups", () => {
  it ("should return a list of groups the user is in", async (done) => {
      let mockUser = new User ({
        groups: ["12345","54321"],
      });

      await mockUser.save();
      let req = "group=12345";

      let getres = await request.delete("/users/"+mockUser._id+"/groups").send(req).expect(200);
      expect(getres.text == "Successfully removed group").toBe(true);
      done();
  });
  it ("should return error 400 if bad request is made", async (done) => {
      let badUserid = "51094";
      let req = "group=90210";

      let getres = await request.delete("/users/"+badUserid+"/groups").send(req).expect(400);
      expect(getres.text == "Bad request").toBe(true);
      done();
  });
  it ("should be groupid that doesn't exists", async (done) => {
      let mockUser = new User ({
        groups: ["12345","54321"],
      });

      await mockUser.save();
      let req = "group=90210";

      let getres = await request.delete("/users/"+mockUser._id+"/groups").send(req).expect(200);
      expect(getres.text == "Successfully removed group").toBe(true);
      done();
  });
});
