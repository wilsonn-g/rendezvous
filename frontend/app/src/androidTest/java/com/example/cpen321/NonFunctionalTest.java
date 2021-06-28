package com.example.cpen321;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.contrib.PickerActions;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
public class NonFunctionalTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);


    @Test
    public void check_average_touch_time() {

//        onView(withId(R.id.edit_email)).perform(typeText("pop"));
//        onView(withId(R.id.edit_pass)).perform(typeText("pop"), closeSoftKeyboard());
//        onView(withId(R.id.login_btn)).perform(click());

        try {
            Thread.sleep(2500);
        } catch (Exception e) {
            Log.e("Error", "Sleep failed");
        }

        onView(withText("New Chat")).check(matches(isDisplayed()));

        long startTime = System.currentTimeMillis();

        onView(withText("New Chat")).perform(click());

        long timeCheck1 = System.currentTimeMillis() - startTime;
        long totalTime1 = System.currentTimeMillis();

        try {
            Thread.sleep(500);
        } catch (Exception e) {
            Log.e("Error", "Sleep failed");
        }

        onView(withId(R.id.cancel)).perform(click());

        long timeCheck2 = System.currentTimeMillis() - totalTime1;
        long totalTime2 = System.currentTimeMillis();

        onView((withId(R.id.new_chat))).perform(click());

        long timeCheck3 = System.currentTimeMillis() - totalTime2;
        long totalTime3 = System.currentTimeMillis();

        onView((withId(R.id.cancel))).perform(click());

        long timeCheck4 = System.currentTimeMillis() - totalTime3;

        System.out.println("time: " + ((timeCheck1 + timeCheck2 + timeCheck3 + timeCheck4) / 4));
    }

    @Test
    public void discover_time_check() {

        //enter discover screen

        long startTime = System.currentTimeMillis();

       // onView();
    }




}
