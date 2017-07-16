package org.rowanacm.android.authentication;

public class TodoItem {

    private String text;
    private boolean completed;

    public TodoItem() {
    }

    public TodoItem(String text, boolean completed) {
        this.text = text;
        this.completed = completed;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TodoItem todoItem = (TodoItem) o;

        if (completed != todoItem.completed) return false;
        return text != null ? text.equals(todoItem.text) : todoItem.text == null;

    }

    @Override
    public int hashCode() {
        int result = text != null ? text.hashCode() : 0;
        result = 31 * result + (completed ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TodoItem{" +
                "text='" + text + '\'' +
                ", completed=" + completed +
                '}';
    }
}
