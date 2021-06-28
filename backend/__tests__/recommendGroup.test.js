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
});

describe("test running recommendation on group", () => {
    let mockGroup = new Group({
        groupname: "please",
        interests: ["shopping", "food", "nature"]
    });
    it("should return on user with more than one interests", async (done) => {
        await mockGroup.save().then((group) => { group1 = group });

        let res = await request.post("/recommend/groups/" + group1._id).send("lat=49.2643993&lng=-123.2318585").expect(201);
        done();
    })
    it("should 400 error, bad group", async (done) => {

        let req = "lat=0&lng=0";

        let res = await request.post("/recommend/groups/324234").send(req).expect(400);
        done();
    });

});