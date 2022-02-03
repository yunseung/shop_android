package gsshop.mobile.v2.util

import android.content.Context
import java.io.IOException
import java.nio.charset.Charset

/**
 * Created by Prashanth Reddy (facebook.com/pr.amrita) (github.com/itspr)  on 04-01-2016.
 */
class JsonUtils {
    companion object {
        @JvmStatic
        fun loadJSONFromAsset(context: Context, fileName: String): String? {
            var json: String?
            json = try {
                val inputStream = context.assets.open(fileName)
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                String(buffer, Charsets.UTF_8)
            } catch (ex: IOException) {
                ex.printStackTrace()
                return null
            }
            return json
        }
    }
}