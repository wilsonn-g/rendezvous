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
describe("test POST /groups/members", () => {
  it ("should return a success message of an added member", async (done) => {
      let mockUser1 = new User ({
        interests: ['sports','nature'],
        likedPlaces: ['123','421'],
        availability: [true,false],
      });
      await mockUser1.save();

      let mockUser2 = new User ({
        interests: ['shopping','nature','sports'],
        likedPlaces: ['123'],
        availability: [true,true],
      });
      await mockUser2.save();

      let mockGroup = new Group ({
        groupname: "Komrades",
        members: [`${mockUser1._id}`],
      });
      await mockGroup.save();
      let req = `member=${mockUser2._id}`;

      let res = await request.post("/groups/"+mockGroup._id+"/members").send(req).expect(201);
      expect(res.text == "Successfully added member");
      done();
  });
  it ("should return error 400 since we provide badID/bad parameter", async (done) => {
      let req = "improperfield=90210";

      let res = await request.post("/groups/9000/members").send(req).expect(400);
      done();
  });
  it ("member already exists in group", async (done) => {
    let mockUser1 = new User ({
      interests: ['sports','nature'],
      likedPlaces: ['123','421'],
      availability: [true,false],
    });
    await mockUser1.save();

    let mockUser2 = new User ({
      interests: ['art'],
      likedPlaces: ['420'],
      availability: [false,false],
    });
    await mockUser2.save();

    let mockGroup = new Group ({
      groupname: "Komrades",
      members: [`${mockUser1._id}`,`${mockUser2._id}`],
    });
    await mockGroup.save();
    let req = `member=${mockUser1._id}`;

    let res = await request.post("/groups/"+mockGroup._id+"/members").send(req).expect(201);
    expect(res.text == "Successfully added member");
      done();
  });
});
