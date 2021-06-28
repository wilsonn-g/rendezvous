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
describe("test DELETE /:userid", () => {
  it ("should delete a user from the database", async (done) => {
      let mockUser = new User ({
        groups: ["12345","54321"],
      });

      await mockUser.save();
      let req = `userid=${mockUser._id}`;

      let getres = await request.delete("/users/"+mockUser._id).send(req).expect(200);
      expect(getres.text == "Deleted user").toBe(true);
      done();
  });
  it ("should return error 400 if bad request is made", async (done) => {
      let badUserid = "51094";
      let req = "group=90210";

      let getres = await request.delete("/users/"+badUserid).send(req).expect(404);
      done();
  });
  it ("should be undefined user bad delete request", async (done) => {
      let mockUser = new User ({
        groups: ["12345","54321"],
      });

      await mockUser.save();
      let req = "userid=undefined"
      let badUserId = undefined;

      let getres = await request.delete("/users/"+badUserId).send(req).expect(404);
      done();
  });
});
