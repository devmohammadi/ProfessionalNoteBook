package com.mohammadi.dashti.professionalnotebook.model;

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
}
