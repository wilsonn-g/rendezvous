const supertest = require('supertest');
const app = require('../index.js');
const Group = require("../models/group");
const mongoose = require('mongoose');
const request = supertest(app);
const databaseName = 'usertestdb';

beforeAll(async () => {
    const url = `mongodb://127.0.0.1'/${databaseName}`;
    await mongoose.connect(url, { useNewUrlParser: true, useUnifiedTopology: true });
});


async function removeAllCollections() {
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
describe("test DELETE /:groupid", () => {
    it("should delete a group from the database", async (done) => {
        let mockGroup1 = new Group({
            groupname: "fun"
        });

        await mockGroup1.save();
        let req = "groupid="+mockGroup1._id;

        let getres = await request.delete("/groups/"+mockGroup1._id).send(req).expect(200);
        expect(getres.text == "Deleted group").toBe(true);
        done();
    });
    it("should return error 400 if bad request is made", async (done) => {
        let badGroupid = "51094";
        let req = "groupid=90210";

        let res = await request.delete("/groups/" + badGroupid).send(req).expect(404);
        expect(res.text === "Cannot find group").toBe(true);
        done();
    });
});
