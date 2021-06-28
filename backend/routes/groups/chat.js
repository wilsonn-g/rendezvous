const debug = require("debug");
const express = require("express");
const logger = debug("app:error");
const app = express();
const server = require("http").createServer(app);
const io = require("socket.io")(server);
const Group = require("../../models/group");
const User = require("../../models/user");
const Message = require("../../models/message");
const mongoose = require("mongoose");

const port = 8080;

const admin = require("firebase-admin");
const serviceAccount = require("../../firebasekey_rendezvous.json");


server.listen(port, () => {
  mongoose.connect("mongodb://localhost/rendezvous", {
    useNewUrlParser: true,
    useUnifiedTopology: true
  });

  admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://rendezvous-1cddb.firebaseio.com"
  });
});

const pushNotifications = async (groupMembers, sender, message) => {
  try {
    logger("In push notification");
    logger(groupMembers);
    //refactor this into function
    groupMembers.forEach(async (userid) => {
      let user = await User.findById(userid);
      let deviceToken = user.deviceToken;
      logger(deviceToken);
      if (deviceToken === "") {
        return;
      }
      // FCM Variables
      const payload = {
        notification: {
          title: "Rendezvous",
          body: sender + ": " + message,
        }
      };

      const options = {
        priority: "high",
        timeToLive: 86400, // 1 day
      };

      admin.messaging().sendToDevice(deviceToken, payload, options)
        .then((response) => {
          logger("Successfully sent push notification: ", response);
        })
        .catch((error) => {
          logger("Error sending push notification ", error);
        });
    });
  } catch (err) {
    logger(err);
  }
};

async function updateChatLog(data) {
  let groupid = new mongoose.Types.ObjectId(data.groupID);
  try {
    let group = await Group.findById(groupid);
    let newMessage = new Message({
      senderName: data.username,
      message: data.message,
      timeStamp: data.timeStamp
    });
    await pushNotifications(group.members, data.username, data.message);
    group.lastSender = data.username;
    group.lastMessage = data.message;
    group.messages.push(newMessage);
    await group.save();
    logger("Successfully updated latest message.");
  } catch (err) {
    logger(err);
    logger("Failed to update latest message.");
  }
}

const numUsers = 0;

io.on("connection", (socket) => {
  var addedUser = false;
  // when the client emits 'new message', this listens and executes
  socket.on("new message", (data) => {
    // we tell the client to execute 'new message'
    // sends message to all people in new room
    let room = data.groupID; 
    socket.join(room);
    socket.to(room).emit("new message", {
      username: data.username,
      message: data.message
    });
    updateChatLog(data);
  });

  // when the client emits 'typing', we broadcast it to others
  socket.on("typing", (data) => {
    let room = data.groupID;
    socket.join(room);
    socket.to(room).emit("typing", {
      username: data.username
    });
  });

  // when the client emits 'stop typing', we broadcast it to others
  socket.on("stop typing", (data) => {
    let room = data.groupID;
    socket.join(room);
    socket.to(room).emit("stop typing", {
      username: data.username
    });
  });

});
