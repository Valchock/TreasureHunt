package com.project.treasurehunt;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GeoFenceViewModel extends ViewModel {

    MutableLiveData<Integer> currentClueIndex = new MutableLiveData<>();
    Integer currentIndex = 0;

    public MutableLiveData<Integer> getCurrentClue() {
        if (currentClueIndex == null) {
            currentClueIndex = new MutableLiveData<>();
        }
        currentClueIndex.setValue(currentIndex);
        return currentClueIndex;
    }

    public void updateClue() {
        if (currentIndex < 4) {
            currentIndex = currentIndex + 1;
            currentClueIndex.setValue(currentIndex);
        } else {
            currentIndex = 4;
            currentClueIndex.setValue(currentIndex);
        }

    }
}
