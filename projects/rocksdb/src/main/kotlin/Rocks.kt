import org.rocksdb.InfoLogLevel
import org.rocksdb.Options
import org.rocksdb.RocksDB

class Rocks private constructor(private val db: RocksDB, private val afterClose: () -> Unit): AutoCloseable {
    companion object {
        init {
            RocksDB.loadLibrary()
        }

        fun open(name: String): Rocks {
            val opts = Options().apply {
                setCreateIfMissing(true)
                setInfoLogLevel(InfoLogLevel.INFO_LEVEL)
            }
            return Rocks(RocksDB.open(opts, name), opts::close)
        }
    }

    fun get(key: ByteArray): ByteArray? = db.get(key)

    fun put(key: ByteArray, value: ByteArray) = db.put(key, value)

    override fun close() {
        db.close()
        afterClose()
    }
}
