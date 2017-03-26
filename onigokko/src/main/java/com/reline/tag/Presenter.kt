package com.reline.tag

/*
 * Copyright 2016 Nathaniel Reline
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.lang.ref.WeakReference

abstract class Presenter<V> {
    protected val TAG = javaClass.simpleName
    private var view: WeakReference<V>? = null

    protected open fun onBind() {}
    protected open fun onUnbind() {}

    fun takeView(view: V) {
        if (this.view != null) {
            if (this.view!!.get() !== view) {
                this.view!!.clear()
                this.view = WeakReference(view)
            }
        } else {
            this.view = WeakReference(view)
        }
        this.onBind()
    }

    fun dropView(view: V) {
        this.onUnbind()
        if (this.view != null && this.view!!.get() === view) {
            this.view = null
        }
    }

    protected fun view(): V? {
        if (view == null) {
            return null
        } else {
            return view!!.get()
        }
    }

    fun hasView(): Boolean {
        return view() != null
    }
}
