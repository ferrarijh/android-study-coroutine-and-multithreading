# Coroutine and Multi-Threading

### Setup

MainActivity setup:
```kotlin
class MainActivity : AppCompatActivity() {

    private val mHandler = Handler(Looper.getMainLooper())  //for multi-threading

    val cnt1 = MutableLiveData(arrayOf(0))
    val cnt2 = MutableLiveData(arrayOf(0))
    val cnt3 = MutableLiveData(arrayOf(0))
    //...
}
```

synchronous setup:
```kotlin
    private fun startCountingSync(){
        CoroutineScope(Dispatchers.Main).launch{
            withContext(Dispatchers.Main){
                for(i in 1..10){
                    cnt1.value!![0]++
                    cnt1.value = cnt1.value!!
                    delay(100)
                }
                Log.d("", "cnt1 (sync)- ${Thread.currentThread().hashCode()}")
            }
            withContext(Dispatchers.Main){
                for(i in 1..10){
                    cnt2.value!![0]++
                    cnt2.value = cnt2.value!!
                    delay(100)
                }
                Log.d("", "cnt2 (sync)- ${Thread.currentThread().hashCode()}")
            }
            withContext(Dispatchers.Main){
                for(i in 1..10){
                    cnt3.value!![0]++
                    cnt3.value = cnt3.value!!
                    delay(100)
                }
                Log.d("", "cnt3 (sync)- ${Thread.currentThread().hashCode()}")
            }
        }
    }
```

asynchronous setup:
```kotlin
    private fun startCountingAsync(){
        //1)
        CoroutineScope(Dispatchers.Main).launch{
            withContext(Dispatchers.Default){
                for(i in 1..10){
                    cnt1.value!![0]++
                    withContext(Dispatchers.Main){
                        cnt1.value = cnt1.value!!
                    }
                    delay(100)
                }
                Log.d("", "cnt1 (async) - ${Thread.currentThread().hashCode()}")
            }
        }

        //2)
        CoroutineScope(Dispatchers.Main).launch{
            withContext(Dispatchers.Main){
                for(i in 1..10){
                    cnt2.value!![0]++
                    withContext(Dispatchers.Main){
                        cnt2.value = cnt2.value!!
                    }
                    delay(100)
                }
                Log.d("", "cnt2 (async) - ${Thread.currentThread().hashCode()}")
            }
        }

        //3)
        CoroutineScope(Dispatchers.Main).launch{
            withContext(Dispatchers.Default){
                for(i in 1..10){
                    cnt3.value!![0]++
                    withContext(Dispatchers.Main){
                        cnt3.value = cnt3.value!!
                    }
                    delay(100)
                }
                Log.d("", "cnt3 (async) - ${Thread.currentThread().hashCode()}")
            }
        }
    }
```

parallel(multi-thread) setup:
```kotlin
        private fun startCountingPara(){
            Thread(Runnable{
                for(i in 1..10){
                    cnt1.value!![0]++
                    mHandler.post(Runnable{
                        cnt1.value = cnt1.value!!
                    })
                    Thread.sleep(100)
                }
                Log.d("", "cnt1 (para) - ${Thread.currentThread().hashCode()}")
            }).start()
    
            Thread(Runnable{
                for(i in 1..10){
                    cnt2.value!![0]++
                    mHandler.post(Runnable{
                        cnt2.value = cnt2.value!!
                    })
                    Thread.sleep(100)
                }
                Log.d("", "cnt2 (para) - ${Thread.currentThread().hashCode()}")
            }).start()
    
            Thread(Runnable{
                for(i in 1..10){
                    cnt3.value!![0]++
                    mHandler.post(Runnable{
                        cnt3.value = cnt3.value!!
                    })
                    Thread.sleep(100)
                }
                Log.d("", "cnt3 (para) - ${Thread.currentThread().hashCode()}")
            }).start()
    
        }
```

### Output

log output for synchronous setup:
```
D/: cnt1 (sync)- 244106889
D/: cnt2 (sync)- 244106889
D/: cnt3 (sync)- 244106889
```

log output for asynchronous setup:
```
D/: cnt2 (async) - 113893791
D/: cnt1 (async) - 112046828
D/: cnt3 (async) - 112046828
```

Notice 1) and 3) are on same thread even though they're ran asynchronously since heir values are incremented on 'Dispatchers.Main' context.
Value of 2) was incremented on 'Dispatchers.Main' context.

log output for multi-thread setup:
```
D/: cnt1 (para) - 188808802
D/: cnt2 (para) - 167402483
D/: cnt3 (para) - 124909869
```