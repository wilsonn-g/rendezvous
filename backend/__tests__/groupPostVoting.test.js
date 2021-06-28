const supertest = require('supertest');
const app = require('../index.js');
const User = require("../models/user");
const Group = require("../models/group");
const mongoose = require('mongoose');
const request = supertest(app);
const databaseName = 'usertestdb'
const firebasemock = require('firebase-mock');


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
describe("test POST /groups/:groupid/voting", () => {
  it ("should return a suggested event to vote on", async (done) => {
    admin.messaging = jest.fn(() => ({
      sendToDevice: jest.fn(() => { return; }),
    }));

    let mockUser1 = new User ({
      deviceToken: "",
    });
    await mockUser1.save();

    let mockUser2 = new User ({
      deviceToken: "",
    });
    await mockUser2.save();

    let mockGroup = new Group ({
      groupname: "Komrades",
      members: [`${mockUser2._id}`,`${mockUser1._id}`],
    });
    await mockGroup.save();
      let req = "index=12923473323&placeid=9000&placeName=UBC&senderId=me:)&sendername=wilson&photo=photo";
      let res = await request.post("/groups/"+mockGroup._id+"/voting").send(req).expect(201);
      expect(res.body.placeid == 9000).toBe(true);
      expect(res.body.placeName == "UBC").toBe(true);
      expect(res.body.senderId == "me:)").toBe(true);
      expect(res.body.senderName === "wilson").toBe(true);
      expect(res.body.photo === "photo").toBe(true);
      done();
  });
  it ("should return error 400 if invalid groupid is passed as param", async (done) => {
    admin.messaging = jest.fn(() => ({
      sendToDevice: jest.fn(() => { throw new Error(); }),
    }));
      let req = "improperfield=90210";
      let badID = "90210";

      let res = await request.post("/groups/"+badID+"/voting").send(req).expect(400);
      done();
  });
  it ("successful push notifications unlike first test", async (done) => {
      admin.messaging = jest.fn(() => ({
        sendToDevice: jest.fn(() => { return; }),
      }));
      
      let mockUser1 = new User ({
        deviceToken: "1",
      });
      await mockUser1.save();

      let mockUser2 = new User ({
        deviceToken: "2",
      });
      await mockUser2.save();

      let mockGroup = new Group ({
        groupname: "Komrades",
        members: [`${mockUser2._id}`,`${mockUser1._id}`],
      });
      await mockGroup.save();

      let req = "index=213258972389&placeid=9000&placeName=UBC&senderId=me:)&sendername=wilson&photo=photo";
      let res = await request.post("/groups/"+mockGroup._id+"/voting").send(req).expect(201);
      expect(res.body.placeid == 9000).toBe(true);
      expect(res.body.placeName == "UBC").toBe(true);
      expect(res.body.senderId == "me:)").toBe(true);
      expect(res.body.senderName === "wilson").toBe(true);
      expect(res.body.photo === "photo").toBe(true);
      done();
  });
});
