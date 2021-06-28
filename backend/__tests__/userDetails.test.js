const supertest = require('supertest');
const app = require('../index.js');
const User = require("../models/user");
const mongoose = require('mongoose');
const request = supertest(app);
const crypto = require("crypto");
const databaseName = 'usertestdb';

/*
 *	The following functions are used to encrypt the password
 */
var genRandomString = function (length) {
    return crypto.randomBytes(Math.ceil(length / 2))
        .toString("hex") //converts it to hex format
        .slice(0, length);
};

// Uses SHA(Secure hashing algorithm) to encrypt 64-bit words
var sha512 = function (password, salt) {
    var hash = crypto.createHmac("sha512", salt);
    hash.update(password);
    var value = hash.digest("hex");
    return {
        salt,
        passwordHash: value
    };
};

function saltHashPassword(userPassword) {
    var salt = genRandomString(16); //Randomly generates 16 characters
    var passwordData = sha512(userPassword, salt);
    return passwordData;
}

function checkHashPassword(userPassword, salt) {
    var passwordData = sha512(userPassword, salt);
    return passwordData;
}

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

describe("test GET /users/:userid", () => {
    let hashData = saltHashPassword("password");
    let mockUser = new User({
        firstname: "John",
        lastname: "Smith",
        name: "John" + " " + "Smith",
        email: "jsmith@gmail.com",
        deviceToken: "",
        password: hashData.passwordHash,
        salt: hashData.salt
    });

    it("should return JSON object for a user given a valid user id", async (done) => {
        let user1;
        await mockUser.save();
        let res2 = await request.get("/users/"+mockUser._id).expect(200);
        expect(res2.body._id).toBe(`${mockUser._id}`);
        done();
    });

    it("should 404 for a user with invalid userid", async (done) => {

        let res2 = await request.get("/users/12312").expect(404);
        done();
    });
});
