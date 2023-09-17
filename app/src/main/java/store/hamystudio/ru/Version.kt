package store.hamystudio.ru

import androidx.annotation.Keep
import com.google.firebase.database.DataSnapshot
import com.jakewharton.byteunits.BinaryByteUnit
import java.text.DecimalFormat
import java.text.NumberFormat

@Keep
data class Version(
    var key: String? = null,
    val name: String? = null,
    val description: String? = null,
    val timestamp: Long? = null,
    val apkRef: String? = null,
    var apkSize: Long? = null,
    val apkGeneration: Long? = null,
    val apkUrl: String? = null,
) : Comparable<Version> {

    companion object {

        private val SAFE_PARSER: (DataSnapshot) -> Version? = { snapshot ->
            snapshot.getValue(Version::class.java)?.apply {
                key = snapshot.key
            }
        }

        fun parse(dataSnapshot: DataSnapshot) = SAFE_PARSER(dataSnapshot)

        private val DECIMAL_FORMAT_LONG = (NumberFormat.getNumberInstance() as DecimalFormat).apply { maximumFractionDigits = 2 }
        private val DECIMAL_FORMAT_SHORT = (NumberFormat.getNumberInstance() as DecimalFormat).apply { maximumFractionDigits = 0 }

        private fun formatBytesSize(bytes: Long?): String? = when {
            bytes == null || bytes == 0L -> null
            BinaryByteUnit.BYTES.toMebibytes(bytes) < 1L -> BinaryByteUnit.format(bytes, DECIMAL_FORMAT_SHORT)
            else -> BinaryByteUnit.format(bytes, DECIMAL_FORMAT_LONG)
        }
    }

    val semver by lazy { SemVer.parse(name) }

    val descriptionToHtml by lazy {
        Utils.parseHtml(description)
    }

    enum class Status {
        DEFAULT,
        DOWNLOADING,
        INSTALLING,
        OPENING
    }

    var status: Status = Status.DEFAULT

    var apkSizeBytesDisplay: String? = null
        private set
        get() {
            if (field == null) field = formatBytesSize(apkSize)
            return field
        }

    fun updateApkSize(bytes: Long?) {
        apkSize = bytes
        apkSizeBytesDisplay = null
    }

    var apkFileAvailable: Boolean = false

    /**
     * Keep track of progress when #status is #DOWNLOADING
     */
    var progress: Int = 0

    fun updateStatus(status: Status, progress: Int = 0) {
        this.status = status
        this.progress = progress
    }

    override fun compareTo(other: Version): Int {
        return -when (val compared = SemVer.nonNull(semver).compareTo(SemVer.nonNull(other.semver))) {
            0 -> (timestamp ?: 0L).compareTo(other.timestamp ?: 0L)
            else -> compared
        }
    }

    fun hasApkRef(): Boolean = !apkRef.isNullOrBlank()

    fun hasApkUrl(): Boolean = !apkUrl.isNullOrBlank()

}

