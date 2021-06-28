package com.example.cpen321;

import android.widget.TimePicker;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.contrib.PickerActions;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.AllOf.allOf;
import static com.example.cpen321.MainActivity.userLng;
import static com.example.cpen321.MainActivity.userLat;

@RunWith(AndroidJUnit4.class)
public class SuggestionUseCaseTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void make_suggestion_recommended() {

        //check that we are in home screen
        onView(withText("New Chat")).check(matches(isDisplayed()));

        //log out
        onView(withId(R.id.profile)).perform(click());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Logout")).perform(click());

        //log in as trent
        onView(withId(R.id.edit_email)).perform(typeText("trent@gmail.com"));
        onView(withId(R.id.edit_pass)).perform(typeText("pass"));
        onView(withId(R.id.login_btn)).perform(click());

        onView(allOf(withText(startsWith("test")),
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                isCompletelyDisplayed()))
                .perform(click());

        //let chat load
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //check we are in messages screen
        onView(withId(R.id.messages)).check(matches(isDisplayed()));

        userLat = 49.2624906;
        userLng = -123.2524502;

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        onView(withText("Suggest")).perform(click());

        //check we are in suggestion screen
        onView(withText("Here's what you've bookmarked:")).check(matches(isDisplayed()));

        //let suggestions load
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //choose a place
        onView(withId(R.id.suggestedPlaces)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //pick time
        onView(Matchers.instanceOf(TimePicker.class)).perform(PickerActions.setTime(3, 30));
        onView(withText("OK")).perform(click());

        //pick day
        onView(withText("Monday"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        //let chat load
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //check that we are back in messages screen
        onView(withId(R.id.messages)).check(matches(isDisplayed()));

        //let chat load
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //scroll up
        onView(withId(R.id.messages))
                .perform(RecyclerViewActions.scrollToPosition(0));

        //check that new suggestion appeared
        onView(allOf(withText(startsWith("I suggest")),
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                isCompletelyDisplayed()))
                .check(matches(isDisplayed()));

        //leave chat
        //openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        //onView(withText("Back")).perform(click());

    }
}

