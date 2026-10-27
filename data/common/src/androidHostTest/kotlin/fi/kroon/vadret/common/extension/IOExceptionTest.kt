package fi.kroon.vadret.common.extension

import fi.kroon.vadret.common.ErrorCode
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.test.Test
import kotlin.test.assertEquals

class IOExceptionTest {

    @Test
    fun `an unknown host means the network is unavailable`() {
        assertEquals(
            expected = ErrorCode.NETWORK_UNAVAILABLE,
            actual = UnknownHostException("smhi.se").networkErrorCode()
        )
    }

    @Test
    fun `a timeout is a network failure`() {
        assertEquals(
            expected = ErrorCode.NETWORK_FAILURE,
            actual = SocketTimeoutException("slow").networkErrorCode()
        )
    }
}