package com.bharathksunil.androidauthmvp.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bharathksunil.util.TextUtils;

/**
 * The SignInPresenter implementation to perform signIn
 * A presenter interacts with both the UI and the repository
 * this is independent of the way the UI or the Repositories are implemented
 *
 * @author Bharath on 26-01-2018.
 */

public class SignInPresenterImpl implements SignInPresenter {

    @Nullable
    private SignInPresenter.View view;
    private SignInPresenter.Repository repository;
    private Repository.SignInCallbacks signInCallbacks;

    public SignInPresenterImpl(@NonNull Repository repository) {
        this.repository = repository;
        this.signInCallbacks = new Repository.SignInCallbacks() {
            @Override
            public void onSignInSuccessful() {
                if (view != null) {
                    view.onProcessEnded();
                    view.onUserSignedIn();
                }
            }

            @Override
            public void onEmailIncorrect() {
                if (view != null) {
                    view.onProcessEnded();
                    view.onEmailError(FormErrorType.INCORRECT);
                }
            }

            @Override
            public void onPasswordIncorrect() {
                if (view != null) {
                    view.onProcessEnded();
                    view.onPasswordError(FormErrorType.INCORRECT);
                }
            }

            @Override
            public void onRepositoryException() {
                if (view != null) {
                    view.onProcessEnded();
                    view.onUnexpectedError();
                }
            }

            /**
             * This is called when the user tries to sign in, but he is already signed in
             */
            @Override
            public void isAlreadySignedIn() {
                if (view != null) {
                    view.onProcessEnded();
                    view.onUserAlreadySignedIn();
                }
            }
        };
    }

    /**
     * Call this method in the onResume to set the current view
     *
     * @param view the SignInPresenter.View
     */
    @Override
    public void setView(@Nullable View view) {
        this.view = view;
    }

    /**
     * Respond to SignIn button clicks and perform the sign in
     */
    @Override
    public void onSignInButtonClicked() {
        if (view != null) {
            view.onProcessStarted();
            //get These fields from the view
            String email = view.getEmailField();
            String password = view.getPasswordField();
            //check syntax and fields
            if (TextUtils.isEmpty(email)) {
                view.onEmailError(FormErrorType.EMPTY);
                view.onProcessEnded();
            } else if (!TextUtils.isEmailValid(email)) {
                view.onEmailError(FormErrorType.INVALID);
                view.onProcessEnded();
            } else if (TextUtils.isEmpty(password)) {
                view.onPasswordError(FormErrorType.EMPTY);
                view.onProcessEnded();
            } else if (!TextUtils.isPasswordStrong(password)) {
                view.onPasswordError(FormErrorType.INVALID);
                view.onProcessEnded();
            }
            //if all are valid proceed with signIn
            else {
                repository.signInWithEmailAndPassword(email, password, signInCallbacks);
            }
        }
    }
}