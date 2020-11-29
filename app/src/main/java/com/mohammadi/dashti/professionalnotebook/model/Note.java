package com.mohammadi.dashti.professionalnotebook.model;

import java.util.Comparator;

public class Note {

    private String category;
    private String title;
    private String note;
    private Long time;

    Note() {
    }

    public Note(String category, String title, String note, Long time) {
        this.category = category;
        this.title = title;
        this.note = note;
        this.time = time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    //Sort By title ( ascending )
    public static final Comparator<Note> BY_TITLE = (o1, o2) -> o1.getTitle().compareTo(o2.getTitle());

    //Sort By category ( ascending )
    public static final Comparator<Note> BY_CATEGORY = (o1, o2) -> o1.getCategory().compareTo(o2.getCategory());

    //Sort By time ( ascending )
    public static final Comparator<Note> BY_TIME = (o1, o2) -> o1.getTime().compareTo(o2.getTime());

}
