package com.example.cpen321.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import static com.example.cpen321.data.User.userId;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.cpen321.MainActivity;
import com.example.cpen321.R;
import com.example.cpen321.retrofit.NodeJS;
import com.example.cpen321.retrofit.RetroFitClientUtils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


    public class LoginFragment extends Fragment {

        private NodeJS our_API;
        private CompositeDisposable compositeDisposable = new CompositeDisposable();

        private MaterialEditText edit_email;
        private MaterialEditText edit_pass;
        private static final String EMAIL = "email";
        private CallbackManager callbackManager;

        @Override
        public void onStop() {
            compositeDisposable.clear();
            super.onStop();
        }

        @Override
        public void onDestroy() {
            compositeDisposable.clear();
            super.onDestroy();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            View view = inflater.inflate(R.layout.login_layout, container, false);

            //initialize nodejs api
            Retrofit retrofit = RetroFitClientUtils.getInstanceLogin();
            our_API = retrofit.create(NodeJS.class);

            Button btn_register = view.findViewById(R.id.register_btn);
            Button btn_login = view.findViewById(R.id.login_btn);


            //Facebook login
            LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
            callbackManager = CallbackManager.Factory.create();

            edit_email = (MaterialEditText) view.findViewById(R.id.edit_email);
            edit_email.setIconLeft(R.drawable.acc_icon);
            edit_pass = (MaterialEditText) view.findViewById(R.id.edit_pass);
            edit_pass.setIconLeft(R.drawable.lock_icon);

            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loginUser(edit_email.getText().toString(), edit_pass.getText().toString());
                }
            });

            btn_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    registerUser(edit_email.getText().toString(), edit_pass.getText().toString());
                }
            });

            loginButton.setReadPermissions(Arrays.asList(EMAIL));
            loginButton.setFragment(this);
            // Callback registration

            loginButton.registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            GraphRequest request = GraphRequest.newMeRequest(
                                    loginResult.getAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response) {
                                            String first_name;
                                            String last_name;
                                            String email;
                                            Log.v("LoginActivity", response.toString());

                                            if (response.getError()!= null) {
                                                Log.e("FBError", "err while parsing Facebook user data");
                                            } else {
                                                email = object.optString("email");
                                                first_name = object.optString("first_name");
                                                last_name = object.optString("last_name");
                                                facebookUserLogin(first_name, last_name, email);
                                            }
                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,first_name,last_name,email");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }

                        @Override
                        public void onCancel() {
                            // App code
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            Toast.makeText(getActivity(), "Incorrect password. Try again.", Toast.LENGTH_SHORT).show();
                        }
                    });

            return view;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
        }

        private void facebookUserLogin(String firstName, String lastName, String email) {
            SharedPreferences myPrefs= getActivity().getSharedPreferences("mypref", 0);
            String token = myPrefs.getString("device token", "0");
            compositeDisposable.add(our_API.registerUser(email, firstName, lastName, token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            Toast.makeText(getActivity(), "Registration successful", Toast.LENGTH_SHORT).show();
                            loginUser(email,token);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable error) throws Exception {
                            if (error.getMessage().contains("connect")){
                                Toast.makeText(getActivity(), "Failed to connect to remote server", Toast.LENGTH_SHORT).show();
                            } else {
                                loginUser(email,token);
                            }
                        }

                    }));
        }

        private void registerUser(String email, String password) {
            View enter_name_view = LayoutInflater.from(getActivity()).inflate(R.layout.enter_name_layout,null);

            if (email.isEmpty()|| password.isEmpty()) {
                Toast.makeText(getActivity(),"Missing fields",Toast.LENGTH_SHORT).show();
                return;
            }

            new MaterialStyledDialog.Builder(getActivity())
                    .setTitle("Register")
                    .setStyle(Style.HEADER_WITH_ICON)
                    .setHeaderColor(R.color.dark_blue)
                    .setDescription("Enter your first and last name")
                    .setCustomView(enter_name_view)
                    .setNegativeText("Cancel")
                    .setIcon(R.drawable.group_icon)
                    .onNegative((dialog,which) -> {dialog.dismiss();})
                    .setPositiveText("Register")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            MaterialEditText edit_first_name = (MaterialEditText) enter_name_view.findViewById(R.id.edit_first_name);
                            MaterialEditText edit_last_name = (MaterialEditText) enter_name_view.findViewById(R.id.edit_last_name);

                            String first_name = edit_first_name.getText().toString();
                            String last_name = edit_last_name.getText().toString();

                            if (first_name.isEmpty() || last_name.isEmpty()) {
                                Toast.makeText(getActivity(), "Please enter first and last name", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            compositeDisposable.add(our_API.registerUser(email, edit_first_name.getText().toString(), edit_last_name.getText().toString(), password)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<String>() {
                                        @Override
                                        public void accept(String s) throws Exception {
                                            Toast.makeText(getActivity(), "Registration successful", Toast.LENGTH_SHORT).show();
                                        }

                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable error) throws Exception {
                                            if (error.getMessage().contains("connect")){
                                                Toast.makeText(getActivity(), "Failed to connect to remote server", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getActivity(), "Registration error: email already in use", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    }));
                        }
                    }).show();
        }

        private void loginUser(String email, String password) {
            SharedPreferences myPrefs= getActivity().getSharedPreferences("mypref", 0);
            String token = myPrefs.getString("device token", "0");

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getActivity(),"Missing fields",Toast.LENGTH_SHORT).show();
                return;
            }

            compositeDisposable.add(our_API.loginUser(email, password, token)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            if (s.contains("Success")) {
                                userId = s.substring(9, s.length() - 1);
                                String filename = "login-info.txt";
                                String fileContents = userId;
                                FileOutputStream outputStream;
                                File file = new File(getContext().getFilesDir(), filename);
                                System.out.println(file);
                                try {
                                    outputStream = getContext().openFileOutput(filename, Context.MODE_PRIVATE);
                                    outputStream.write(fileContents.getBytes());
                                    outputStream.close();
                                } catch (Exception e) {
                                    Log.e("ERROR", "Couldn't write to file");
                                    e.printStackTrace();
                                }

                                Toast.makeText(getActivity(), "Login Success", Toast.LENGTH_SHORT).show();
                                Intent myIntent = new Intent(getContext(), MainActivity.class);
                                startActivity(myIntent);
                            } else {
                                Toast.makeText(getActivity(), "" + s, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Consumer<Throwable> () {
                        @Override
                        public void accept(Throwable error) throws Exception {
                            if (error.getMessage().contains("connect")){
                                Toast.makeText(getActivity(), "Failed to connect to remote server", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Error logging in. Try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }));
        }
    }

