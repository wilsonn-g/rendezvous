package com.example.cpen321;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.*;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginActivityTextBoxTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void loginActivityTextBoxTest() {
        ViewInteraction materialEditText = onView(
                allOf(withId(R.id.edit_email),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout2),
                                        0),
                                0),
                        isDisplayed()));
        materialEditText.perform(replaceText("johnsmith@email.com"), closeSoftKeyboard());

        ViewInteraction editText = onView(
                allOf(withId(R.id.edit_email), withText("johnsmith@email.com"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout2),
                                        0),
                                0),
                        isDisplayed()));
        editText.check(matches(withText("johnsmith@email.com")));

        ViewInteraction materialEditText2 = onView(
                allOf(withId(R.id.edit_pass),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout2),
                                        2),
                                0),
                        isDisplayed()));
        materialEditText2.perform(replaceText("12345678"), closeSoftKeyboard());

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.edit_pass), withText("12345678"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout2),
                                        2),
                                0),
                        isDisplayed()));
        editText2.check(matches(withText("12345678")));

        ViewInteraction materialEditText3 = onView(
                allOf(withId(R.id.edit_pass), withText("12345678"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout2),
                                        2),
                                0),
                        isDisplayed()));
        materialEditText3.perform(pressImeActionButton());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
