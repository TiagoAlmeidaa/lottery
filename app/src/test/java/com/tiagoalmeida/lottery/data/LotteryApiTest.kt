package com.tiagoalmeida.lottery.data

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit

@RunWith(JUnit4::class)
class LotteryApiTest {

    @MockK(relaxed = true)
    lateinit var retrofit: Retrofit

    private lateinit var appRetrofit: LotteryApi

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        appRetrofit = LotteryApi(retrofit)
    }

    @Test
    fun `getService should return the correct service`() {
        val expectedService = mockk<LotteryApiService>()

        every {
            retrofit.create(LotteryApiService::class.java)
        }.returns(expectedService)

        val result = appRetrofit.getService()

        assertEquals(expectedService, result)
    }
}