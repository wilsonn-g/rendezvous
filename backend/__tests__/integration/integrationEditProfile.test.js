const supertest = require('supertest');
const app = require('../../index.js');
const User = require("../../models/user");
const mongoose = require('mongoose');
const databaseName = 'usertestdb';
const request = supertest(app);

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

describe("testing edit profile sequence", () => {
    it("should update a users fields", async (done) => {
        let email = "demo@gmail.com";
        let password = "pass";
        let firstname = "Guy";
        let lastname = "Lusina";
        let res1 = await request.post("/users")
            .send("email=" + email + "&password=" + password + "&firstname=" + firstname + "&lastname=" + lastname)
            .expect(201);

        let res2 = await request.post("/users/login")
            .send("email=" + email + "&password=" + password)
            .expect(201);

        let userid = res1.body._id.toString();

        let req = {interests: ["nature", "sports"], availability:[false]};

        let res3 = await request.put("/users/"+userid)
            .send(req)
            .expect(200);
        
        let user = await User.findById(userid);

        expect(user.interests[0] === "nature").toBe(true);
        expect(user.interests[1] === "sports").toBe(true);
        expect(user.availability[0] === false).toBe(true);
        done();
    });


});

