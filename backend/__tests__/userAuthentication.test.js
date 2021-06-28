const supertest = require('supertest');
const app = require('../index.js');
const User = require("../models/user");
const mongoose = require('mongoose');
const request = supertest(app);
const crypto = require("crypto");
const databaseName = 'usertestdb'

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
})

describe("testing user registration", () => {
    it("should register user with new email with 201 code", async (done) => {
        let res = await request.post("/users")
            .send("email=demo@demo.com&password=demo&firstname=demofirst&lastname=demolast")
            .expect(201);

        const user = await User.findOne({ email: "demo@demo.com" });
        expect(user.firstname).toBe("demofirst");
        expect(user.lastname).toBe("demolast");
        expect(user.email).toBe("demo@demo.com");
        done();
    });

    it("should 400 user with existing email", async (done) => {
        let hashData = saltHashPassword("password");
        user1 = new User({
            firstname: "John",
            lastname: "Smith",
            name: "John" + " " + "Smith",
            email: "jsmith@gmail.com",
            deviceToken: "",
            password: hashData.passwordHash,
            salt: hashData.salt
        });
        let mockUser;
        await user1.save()
            .then((user) => {mockUser = user});

        let res = await request.post("/users")
            .send("email=" + mockUser.email + "&password=password&firstname=" + mockUser.firstname + "&lastname=" + mockUser.lastname)
            .expect(400);
        expect(res.text).toBe("User already exists with that email");
        done();
    });

    it("should 400 user with invalid/missing required field", async (done) => {
        let res = await request.post("/users")
            .send("email=john@gmail.com&firstname=John&lastname=Shah")
            .expect(400);
        done();
    });
});

