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
// Test for GET request for all groups of a given user
describe("test GET /:userid/groups", () => {
  it ("should return a list of groups the user is in", async (done) => {
        let mockUser = new User ({
          groups: ["12345","54321"],
        });

        await mockUser.save();

        let getres = await request.get("/users/"+mockUser._id+"/groups").expect(200);
        expect(mockUser.groups.length == getres.body.length).toBe(true);
        let i = true;
        let j;
        for (j = 0; j < mockUser.groups.length; j++) {
          if (mockUser.groups[j]!=getres.body[j]) {
            i = false;
          }
        }
        expect(i).toBe(true);
        done();
  });

  it ("should return a 404 error because it is looking for a userid that doesn't exist", async (done) => {
    let mockUser = new User ({
      groups: ["12345","54321"],
    });

    await mockUser.save();
    let badID = 1234

    let getres = await request.get("/users/"+badID+"/groups").expect(404);
    done();
  });

  it ("should return a 404 error because it is looking for a undefined userid error", async (done) => {
    let mockUser = new User ({
      groups: ["12345","54321"],
    });

    await mockUser.save();
    let badID = undefined;

    let getres = await request.get("/users/"+badID+"/groups").expect(404);
    done();
  });

});
