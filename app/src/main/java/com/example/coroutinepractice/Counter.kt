package com.example.coroutinepractice

class Counter(var num: Int = 0) {
    fun inc(){
        num += 1
    }
    fun incSync(){
        synchronized(this){
            num += 1
        }
    }
}