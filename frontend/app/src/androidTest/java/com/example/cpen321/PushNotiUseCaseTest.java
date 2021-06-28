package com.example.cpen321;

import android.util.Log;
import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.uiautomator.UiDevice;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class PushNotiUseCaseTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void add_friend() {

        //check that user is at chat/home screen
        onView(withText("New Chat")).check(matches(isDisplayed()));
        //enter add friends screen
        onView(withContentDescription("More options")).perform(click());
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            Log.e("Error", "Sleep failed");
        }
        onView(withText("Add Friends")).perform(click());
        //check that user is at add friends screen
        onView(withText("Search")).check(matches(isDisplayed()));

        //search for a user
        onView(withId(R.id.editText2)).perform(typeText("Testfirst"), closeSoftKeyboard());
        onView(withId(R.id.search)).perform(click());

        //check that result(s) appeared
        onView(allOf(withText(startsWith("Testfirst Testlast")),
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                isCompletelyDisplayed()))
                .check(matches(isDisplayed()));

        //send them a request
        onView(withId(R.id.search_results)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.add)));


        //log out
        onView(withId(R.id.profile)).perform(click());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Logout")).perform(click());

        //log in as test account
        onView(withId(R.id.edit_email)).perform(typeText("test"));
        onView(withId(R.id.edit_pass)).perform(typeText("test"));
        onView(withId(R.id.login_btn)).perform(click());

        //check request received
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Requests")).perform(click());
        onView(allOf(withText(startsWith("Trent Walsh")),
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                isCompletelyDisplayed()))
                .check(matches(isDisplayed()));
    }


    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performClick();
            }
        };
    }

}
