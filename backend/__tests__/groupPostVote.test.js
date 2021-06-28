const supertest = require('supertest');
const app = require('../index.js');
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

// Test for POST request for adding a group to a list of users
describe("test POST /groups/:groupid/vote", () => {
  it ("should return a suggested event to vote on", async (done) => {
    let event1 = {
      placeid: "9000",
      datetime: 10000,
      membersTotal: ["123","900","90"],
      membersYes: ["123","900","90"],
      plannedEvents: [],
    };
    let event2 = {
      placeid: "9000",
      datetime: 10000,
      membersTotal: ["1"],
      membersYes: [],
      plannedEvents: [],
    };
    let event3 = {
      placeid: "9000",
      datetime: 10000,
      membersTotal: ["9"],
      membersYes: [],
      plannedEvents: [],
    };
    let event4 = {
      placeid: "9000",
      datetime: 10000,
      membersTotal: ["9","2","3"],
      membersYes: ["9"],
      plannedEvents: [],
    };
    let mockGroup = new Group ({
      groupname: "Komrades",
      members: ["2","1","3"],
      votingEvents: [event1,event2,event3,event4],
    });

    await mockGroup.save();
    let req = "placeid=9000&userid=1&datetime=10000&vote=yes";
    let res = await request.post("/groups/"+mockGroup._id+"/vote").send(req).expect(201);
    expect(res.text == "Successfully added vote");
    done();
  });
  it ("should return a suggested event to vote on", async (done) => {
    let event1 = {
      placeid: "9000",
      datetime: 10000,
      membersTotal: ["123","900","90"],
      membersYes: ["123","900","90"],
      plannedEvents: [],
    };
    let mockGroup = new Group ({
      groupname: "Komrades",
      members: ["2","1","3"],
      votingEvents: [event1]
    });

    await mockGroup.save();
    let req = "placeid=9000&userid=123&datetime=10000&vote=yes";
    let res = await request.post("/groups/"+mockGroup._id+"/vote").send(req).expect(201);
    expect(res.text == "Successfully added vote");
    done();
  });
  it ("should return error 400 if invalid groupid/params are passed", async (done) => {
      let req = "improperfield=90210";
      let badID = "90210";

      let res = await request.post("/groups/"+badID+"/vote").send(req).expect(400);
      done();
  });
});
