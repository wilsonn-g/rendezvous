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

describe("test UPDATE /users/:userid", () => {
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

    it("should update all the values for a given user id", async (done) => {
        let user1;
        await mockUser.save().then((user) => { user1 = user });
        let res2 = await request.put("/users/" + user1._id.toString())
            .send({interests: ["shopping", "nature"], availability: [true, false]})
            .expect(200);

        let user = await User.findById(user1._id);
        expect(user.firstname).toBe("John");
        expect(user.lastname).toBe("Smith");
        expect(user.interests[0]).toBe("shopping");
        expect(user.interests[1]).toBe("nature");
        expect(user.availability[0]).toBe(true);
        expect(user.availability[1]).toBe(false);
        expect(res2.text).toBe("Successfully updated user details");
        done();
    });

    it("shouldn't update values if no JSON body values are passed in", async (done) => {
        let mockUser = new User({
            firstname: "John",
            lastname: "Smith",
            name: "John" + " " + "Smith",
            email: "jsmith@gmail.com",
            deviceToken: "",
            password: hashData.passwordHash,
            salt: hashData.salt
        });

        await mockUser.save();
        let res2 = await request.put("/users/"+mockUser._id.toString())
            .send({})
            .expect(200);

        let user = await User.findById(mockUser._id);
        expect(user.interests.length==0).toBe(true);
        expect(user.availability.length==0).toBe(true);
        expect(res2.text).toBe("Successfully updated user details")
        done();
    });

    it("should 400 with invalid userid", async(done) => {
        await mockUser.save();
        let res2 = await request.put("/users/12312")
            .send({firstname: "Paul", lastname: "Lin", interests: ["shopping", "nature"], friends: ["12345"], availability: [true, false]})
            .expect(400);
        done();
    });
});
