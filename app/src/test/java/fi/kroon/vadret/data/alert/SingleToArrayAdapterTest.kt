package fi.kroon.vadret.data.alert

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import fi.kroon.vadret.data.alert.adapter.SingleToArray
import fi.kroon.vadret.data.alert.adapter.SingleToArrayAdapter
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.util.Arrays
import java.util.Collections

@RunWith(MockitoJUnitRunner::class)
class SingleToArrayAdapterTest {

    private lateinit var moshi: Moshi
    private lateinit var adapter: JsonAdapter<List<String>>

    @Before
    fun setup() {
        moshi = Moshi.Builder()
            .add(SingleToArrayAdapter.INSTANCE)
            .build()

        adapter = moshi.adapter(
            Types.newParameterizedType(List::class.java, String::class.java),
            SingleToArray::class.java
        )
    }

    @Test
    fun `adapter parses non-list to list`() {
        assertThat(adapter.fromJson(valid_json)).isEqualTo(Arrays.asList("hata", "data"))
    }

    @Test
    fun `invalid json is parsed correctly to a list`() {
        assertThat(adapter.fromJson(invalid_json)).isEqualTo(Collections.singletonList("hata"))
    }

    @Test
    fun `parses empty list correctly`() {
        assertThat(adapter.fromJson(empty_list)).isEqualTo(emptyList<String>())
    }

    val invalid_json = "\"hata\""
    val valid_json = "[\"hata\", \"data\"]"
    val empty_list = "[]"
}