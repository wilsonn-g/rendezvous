const supertest = require('supertest');
const app = require('../index.js');
const User = require("../models/user");
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

describe("test running recommendation on user", () => {
    it("should return on user with more than one interests", async (done) => {
        let mockUser = new User({
            interests: ["sports", "food", "nature"]
        });
        await mockUser.save();

        let req = "lat=49.2643993&lng=-123.2318585";

        let res = await request.post("/recommend/users/"+mockUser._id).send(req).expect(201);
        done();
    });

    it("should 400 error, bad user", async (done) => {

        let req = "lat=0&lng=0";

        let res = await request.post("/recommend/users/324234").send(req).expect(400);
        done();
    });
    it("should 404 error, bad req", async (done) => {
        let mockUser = new User({
            interests: ["sports", "food", "nature"]
        });
        await mockUser.save();
        let req = "";

        let res = await request.post("/recommend/users/"+mockUser._id).send(req).expect(400);
        done();
    });
});
