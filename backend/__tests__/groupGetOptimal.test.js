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
// Test for GET request for all groups
describe("test GET /groups/:groupid/optimaltimes", () => {
  it ("should return a list of optimal times dependent on members availability", async (done) => {
        let mockUser1 = new User ({
          availability: [true,false,false,true,false,false,false,true,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false]
        });
        await mockUser1.save();

        let mockUser2 = new User ({
          availability: [true,true,false,true,true,false,true,false,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false]
        });
        await mockUser2.save();

        let mockGroup = new Group ({
          groupname: "Komrades",
          members: [`${mockUser1._id}`,`${mockUser2._id}`],
          commonAvailability: [true, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, false, false,false,false,false,false,false,false,false,true,true,true,true]
        });
        await mockGroup.save();
        let req = `member=${mockUser2._id}`;

        let res = await request.get("/groups/"+mockGroup._id+"/optimaltimes").expect(200);
        expect(res.body.length!=0).toBe(true);
        done();
    });

    it ("error test, if the groupid is bad", async (done) => {
          let res = await request.get("/groups/123/optimaltimes").expect(404);
          done();
    });

    it ("no times where members share an availability", async (done) => {
      let mockUser1 = new User ({
        availability: [true,false,false,true,false,false,false,true,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false, true, true, false]
      });
      await mockUser1.save();

      let mockUser2 = new User ({
        availability: [true,true,false,true,true,false,true,false,true,true,false,false,false,false,false,false,false,false,false,false,false,false,false,true,false, true, true, false]
      });
      await mockUser2.save();

      let mockGroup = new Group ({
        groupname: "Komrades",
        members: [`${mockUser1._id}`,`${mockUser2._id}`],
        commonAvailability: [true, false, false, true, false, false, false, false, false, false]
      });
      await mockGroup.save();
      let req = `member=${mockUser2._id}`;

      let res = await request.get("/groups/"+mockGroup._id+"/optimaltimes").expect(200);
      expect(res.body.length!=0).toBe(true);
      done();
    });
});
