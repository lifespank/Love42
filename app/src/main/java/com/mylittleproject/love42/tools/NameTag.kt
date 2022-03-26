package com.mylittleproject.love42.tools

val Any.NAME_TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) {
            tag
        } else {
            tag.substring(0, 23)
        }
    }