package com.example.ichat.Model;

import java.util.ArrayList;

public class story {

    private String storyBy;
    private long storyAt;
    ArrayList<UserStory> stories;

    public story() {
    }


    public String getStoryBy() {
        return storyBy;
    }

    public void setStoryBy(String storyBy) {
        this.storyBy = storyBy;
    }

    public long getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(long storyAt) {
        this.storyAt = storyAt;
    }

    public ArrayList<UserStory> getStories() {
        return stories;
    }

    public void setStories(ArrayList<UserStory> stories) {
        this.stories = stories;
    }
}
