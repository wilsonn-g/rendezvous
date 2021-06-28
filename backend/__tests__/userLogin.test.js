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

describe("testing user login", () => {
    it("should login user with correct password", async (done) => {
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
        let user1;
        await mockUser.save().then((user) => {user1 = user});

        res = await request.post("/users/login")
            .send("email=" + user1.email + "&password=password&token=testToken")
            .expect(201);

        let user = await User.findById(user1._id);
        expect(user._id.toString()).toBe(res.body.substring(8, res.body.length));
        expect(user.deviceToken).toBe("testToken");
        done();
    });

    it("should 400 if no user exists for that email", async (done) => {
        let res = await request.post("/users/login")
            .send("email=fakeemail@demo.com&password=password&token=testToken")
            .expect(400);
        done();
    });

    it("should 400 with invalid/missing required field", async (done) => {
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
        await user1.save()

        res = await request.post("/users/login")
            .send("password=password&token=testToken")
            .expect(400);
        done();
    });
    it("incorrect password", async (done) => {
      let hashData = saltHashPassword("omega");
      user1 = new User({
          firstname: "John",
          lastname: "Smith",
          name: "John" + " " + "Smith",
          email: "jsmith@gmail.com",
          deviceToken: "",
          password: hashData.passwordHash,
          salt: hashData.salt
      });
      await user1.save();
      res = await request.post("/users/login")
          .send("email=jsmith@gmail.com&password=123&token=testToken")
          .expect(400);
      expect(res.text == "Incorrect Password");
      done();
    });
});
