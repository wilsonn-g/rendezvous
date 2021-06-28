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
describe("test GET /:userid/search/:query", () => {
  it ("should return all users with a first name or last name who match the query", async (done) => {
        let mockUser1 = new User ({
          firstname: "Yash",
          lastname: "Sardhara",
          name: "Yash Sardhara",
        });

        await mockUser1.save();

        let mockUser2 = new User ({
          firstname: "Ming",
          lastname: "Xuan",
          name: "Ming Xuan",
        });
        await mockUser2.save();

        let mockUser3 = new User ({
          firstname: "Ming",
          lastname: "Sardhara",
          name: "Ming Sardhara",
        });

        await mockUser3.save();

        let searchQuery = "Ming";

        let getres = await request.get("/users/"+mockUser1._id+"/search/"+searchQuery).expect(201);
        expect(getres.body.length == 2).toBe(true);
        done();
  });

  it ("should return a 400 error because the query is invalid", async (done) => {
    let badId = undefined;
    let badQuery = undefined;

    let getres = await request.get("/users/"+badId+"/search/"+badQuery).expect(400);
    done();
  });

  it ("should return a 400 error because it is looking for a userid not in db", async (done) => {

    let badID = 39012;
    let searchQuery = "pop";
    let getres = await request.get("/users/"+badID+"/search/"+searchQuery).expect(400);
    done();
  });

});
