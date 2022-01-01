package com.nsa.notes.extra;

import com.nsa.notes.models.UserModel;

public interface SignInListener {
    void info(int code);
    void data(UserModel model);
}
