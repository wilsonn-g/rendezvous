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

describe("testing messaging between a group of 3", () => {
    it("should send chat messages between 3 people", async (done) => {
        let email = "demo@gmail.com";
        let password = "pass";
        let firstname = "Guy";
        let lastname = "Lusina";
        let res1 = await request.post("/users")
            .send("email=" + email + "&password=" + password + "&firstname=1" + firstname + "&lastname=" + lastname)
            .expect(201);

        let res2 = await request.post("/users/login")
            .send("email=" + email + "&password=" + password)
            .expect(201);

        let res3 = await request.post("/users")
            .send("email=" + email + "2" + "&password=" + password + "&firstname=2" + firstname + "&lastname=" + lastname)
            .expect(201);

        let res4 = await request.post("/users/login")
            .send("email=" + email + "2" + "&password=" + password)
            .expect(201);

        let res5 = await request.post("/users")
            .send("email=" + email + "3" + "&password=" + password + "&firstname=3" + firstname + "&lastname=" + lastname)
            .expect(201);

        res4 = await request.post("/users/login")
            .send("email=" + email + "3" + "&password=" + password)
            .expect(201);

        let userid1 = res1.body._id.toString();
        let userid2 = res3.body._id.toString();
        let userid3 = res5.body._id.toString();

        res1 = await request.get("/users/"+userid1+"/search/1Guy").expect(201);

        res2 = await request.post("/users/"+userid2+"/friendrequests").send("userid="+userid1).expect(201);

        res1 = await request.get("/users/"+userid1+"/search/2Guy").expect(201);

        res2 = await request.post("/users/"+userid3+"/friendrequests").send("userid="+userid1).expect(201);

        res2 = await request.get("/users/"+userid3+"/friendrequests").expect(200);

        res2 = await request.delete("/users/"+userid3+"/friendrequests").send("userid="+userid1).expect(200);

        res2 = await request.post("/users/"+userid3+"/friends").send("userid="+userid1).expect(201);

        res2 = await request.post("/users/"+userid1+"/friends").send("userid="+userid3).expect(201);

        res2 = await request.get("/users/"+userid2+"/friendrequests").expect(200);

        res2 = await request.delete("/users/"+userid2+"/friendrequests").send("userid="+userid1).expect(200);

        res2 = await request.post("/users/"+userid2+"/friends").send("userid="+userid1).expect(201);

        res2 = await request.post("/users/"+userid1+"/friends").send("userid="+userid2).expect(201);

        res2 = await request.get("/users/"+userid1+"/friends").expect(200);
        expect(res2.body.includes(userid2)).toBe(true);

        res2 = await request.get("/users/"+userid2+"/friends").expect(200);
        expect(res2.body.includes(userid1)).toBe(true);

        res2 = await request.get("/users/"+userid3+"/friends").expect(200);
        expect(res2.body.includes(userid1)).toBe(true);

        res2 = await request.get("/users/"+userid1+"/friends").expect(200);
        expect(res2.body.includes(userid3)).toBe(true);

        //Creating the group
        res2 = await request.post("/groups").send("groupname=mygroup").expect(201);
        let groupid = res2.body._id.toString();

        res2 = await request.post("/groups/"+groupid+"/members").send(`member=${userid1}`).expect(201);
        res2 = await request.post("/groups/"+groupid+"/members").send(`member=${userid2}`).expect(201);
        res2 = await request.post("/groups/"+groupid+"/members").send(`member=${userid3}`).expect(201);

        res2 = await request.get("/groups/"+groupid+"/members");
        expect(res2.body.includes(userid1)).toBe(true);
        expect(res2.body.includes(userid2)).toBe(true);
        expect(res2.body.includes(userid3)).toBe(true);

        
        done();
    });


});
