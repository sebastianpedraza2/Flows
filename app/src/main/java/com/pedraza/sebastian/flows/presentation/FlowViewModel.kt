package com.pedraza.sebastian.flows.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FlowViewModel : ViewModel() {

    init {
        collectFlow()
    }

    val countDownFLow = flow {
        var timer = 10
        /**
         * Flow that emits values from 10 - 0
         */
        emit(timer)
        while (timer > 0) {
            delay(1000L)
            timer--
            emit(timer)
        }

    }


    /**
     * To understant how a custom filter could works
     */
    inline fun <T> List<T>.customFilterList(crossinline predicate: (T) -> Boolean): List<T> {
        var newList = mutableListOf<T>()
        this.forEach {
            if (predicate(it)) {
                newList.add(it)
            }
        }
        return newList
    }

    /**
     * Collecting the flow inside of a coroutine because its a suspend call
     */

    private fun collectFlow() = viewModelScope.launch(Dispatchers.IO) {
        countDownFLow
            .filter {
                it % 2 == 0
            }
            .map {
                it * 2
            }
            .onEach {
                println("The current time is : $it")
            }
            .collect { time ->
                println("The current time is : $time")
            }
    }


    /**
     * OTRA MANERA DE COLLECTAR SIN ESTAR ADENTRO DE UNA CORRUTINA
     */
    private fun otherWayOfCollecting() {

        countDownFLow.onEach { time ->
            println("The current time is : $time")
        }.launchIn(viewModelScope)
    }


    /**
     * Terminal OPERATORS
     */
    fun terminalCollectFlow() = viewModelScope.launch(Dispatchers.IO) {
        val numberOfItemsInTheFlow = countDownFLow.count {
            it % 2 == 0
        }

        val reduceResult = countDownFLow.reduce { accumulator, value ->
            /**
             * Retorno S, que es el acumulador
             */
                 accumulator + value
        }
    }
}