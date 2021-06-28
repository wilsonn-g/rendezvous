const supertest = require('supertest');
const app = require('../../index.js');
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

describe("testing login sequence", () => {
    it("should login a user", async (done) => {
        let email = "demo@gmail.com";
        let password = "pass";
        let firstname = "Guy";
        let lastname = "Lusina";
        let res1 = await request.post("/users")
            .send("email=" + email + "&password=" + password + "&firstname=" + firstname + "&lastname=" + lastname)
            .expect(201);


        let res2 = await request.post("/users")
            .send("email=" + email + "&password=" + password + "&firstname=" + firstname + "&lastname=" + lastname)
            .expect(400);

        let res3 = await request.post("/users/login")
            .send("email=" + email + "420" + "&password=" + password)
            .expect(400);

        res3 = await request.post("/users/login")
            .send("email=" + email + "&password=" + password + "420")
            .expect(400);

        res3 = await request.post("/users/login")
            .send("email=" + email + "&password=" + password)
            .expect(201);

        done();
    });


});

