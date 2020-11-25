package com.example.coroutinepractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.coroutinepractice.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mHandler = Handler(Looper.getMainLooper())

    private val cnt1 = MutableLiveData(arrayOf(0))
    private val cnt2 = MutableLiveData(arrayOf(0))
    private val cnt3 = MutableLiveData(arrayOf(0))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTVs()
        setBtn()

        testSynchronized()
    }

    private fun testSynchronized(){
        Log.d("", "availableProcessors(): ${Runtime.getRuntime().availableProcessors()}")

        val counter = Counter(0)

        val threads = mutableListOf<Thread>()
        val threadsSync = mutableListOf<Thread>()

        //(not synchronized) increment integer in `obj` starting with 0
        for(trial in 0 until 10) {

            threads.clear()
            counter.num = 0

            for (i in 0..2) {
                threads.add(Thread(Runnable {
                    for (j in 1..100000)
                        counter.inc()
                }))
                threads[i].start()
            }
            for (i in 0..2)
                threads[i].join()

            Log.d("", "obj.num: ${counter.num} (not synchronized) ")
        }

        //(synchronized) increment integer in `obj` starting with 0
        for(trial in 0 until 10) {

            threadsSync.clear()
            counter.num = 0

            for (i in 0..2) {
                threadsSync.add(Thread(Runnable {
                    for (j in 1..100000)
                        counter.incSync()
                }))
                threadsSync[i].start()
            }
            for (i in 0..2)
                threadsSync[i].join()

            Log.d("", "obj.num: ${counter.num} (synchronized)")
        }
    }

    private fun setTVs(){
        cnt1.observe(this){
            binding.tv1.text = it[0].toString()
        }
        cnt2.observe(this){
            binding.tv2.text = it[0].toString()
        }
        cnt3.observe(this) {
            binding.tv3.text = it[0].toString()
        }
    }

    private fun setBtn(){
        binding.btn1.setOnClickListener{
            startCountingSync()
        }
        binding.btn2.setOnClickListener{
            startCountingAsync()
        }
        binding.btn3.setOnClickListener{
            startCountingPara()
        }
    }

    //3 of them on same thread
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

    //1), 3) on same thread - 2) on different thread
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

    //all on different thread
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

}