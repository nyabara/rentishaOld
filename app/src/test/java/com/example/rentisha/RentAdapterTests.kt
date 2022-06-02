package com.example.rentisha

import android.content.Context
import com.example.rentisha.adapter.RentAdapter
import com.example.rentisha.data.DataSource
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock

class RentAdapterTests {
    private val context = mock(Context::class.java)

    @Test
    fun adapter_size(){
        val dataSet = DataSource().loadRents()

        val rentAdapter = RentAdapter(context,dataSet)

        assertEquals("dataset not equal",dataSet.size,rentAdapter.itemCount)

    }
}