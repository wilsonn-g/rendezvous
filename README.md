# :tokyo_tower: Rendezvous

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/8bead3d04a3a4acc937687e3026177c2)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=mebesius/rendezvous&amp;utm_campaign=Badge_Grade)

To get you **off** your phone and face-to-face with your friends. 

Our project for CPEN 321 - Software Engineering at UBC

<img src="https://github.com/mebesius/rendezvous/blob/master/screenshots/login-register.png" width=200 align=left>
<img src="https://github.com/mebesius/rendezvous/blob/master/screenshots/activity-finder.png" width=200 align=left/>

## Front End

Using the Google-recommended approach of a single activity and many fragments for Android.

### Front-End Setup

Download Android Studio here: <https://developer.android.com/studio>    

**Note**: You will need Java/JRE since the project is written in Java, but hopefully you have that from CPEN 221.  

To emulate the app on a virtual device, you will need to pick where the **API Level >= 28**, since the app is currently declared to be of that minSDK level in the manifest file. A good choice of virtual device is a Pixel 2 XL since @mebesius has one on Andoid 10 as of the moment.  

## Back-End

NodeJS, Express, AWS

### Back-End Setup

Make sure to **not** expose any API keys (not sure if this is relevant since the repo is private but it is general good practice anyways, especially if you end up working on open-source software.)
