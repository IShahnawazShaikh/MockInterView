package com.example.mockinterview.ui.allmeet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AllMeetingViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AllMeetingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}