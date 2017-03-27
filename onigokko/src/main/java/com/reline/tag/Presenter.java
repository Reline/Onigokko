package com.reline.tag;

import java.lang.ref.WeakReference;

public abstract class Presenter<V> {
    protected final String TAG = getClass().getSimpleName();
    private WeakReference<V> view;

    protected void onBind() {}
    protected void onUnbind() {}

    public final void takeView(V view) {
        if (view == null) {
            throw new NullPointerException("view == null");
        }

        if (this.view() != null) {
            if (this.view.get() != view) {
                this.view.clear();
                this.view = new WeakReference<>(view);
            }
        } else {
            this.view = new WeakReference<>(view);
        }

        this.onBind();
    }

    public final void dropView(V view) {
        this.onUnbind();
        if (view == null) {
            throw new NullPointerException("view == null");
        }

        if (this.view != null && this.view.get() == view) {
            this.view = null;
        }
    }

    protected final V view() {
        if (view == null) {
            return null;
        } else {
            return view.get();
        }
    }

    protected boolean hasView() {
        return view() != null;
    }
}
