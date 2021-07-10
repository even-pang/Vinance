package com.project.vinance.view.sub

import com.project.vinance.view.implementation.TextChangeListenable

object NeedToChangeList {
    val fragmentList: MutableList<TextChangeListenable> = mutableListOf()

    inline fun <reified T> getFragment(): T? {
        for (fragment in fragmentList) {
            if (fragment is T) {
                return fragment
            }
        }

        return null
    }
}