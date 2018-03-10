package keyv

import org.rocksdb.InfoLogLevel
import org.rocksdb.Options
import org.rocksdb.RocksDB

sealed class Rocks(private val db: RocksDB): AutoCloseable, KeyValueStore {
    override fun close() = db.close()

    class KV(private val db: RocksDB) : Rocks(db) {
        override fun get(key: ByteArray): ByteArray? = db.get(key)

        override fun put(key: ByteArray, value: ByteArray) = db.put(key, value)

        override fun delete(key: ByteArray) = db.delete(key)
    }

    class PrefixedKV(private val db: RocksDB, private val prefix: ByteArray = byteArrayOf()) : Rocks(db) {
        override fun get(key: ByteArray): ByteArray? = db.get(prefix + key)

        override fun put(key: ByteArray, value: ByteArray) = db.put(prefix + key, value)

        override fun delete(key: ByteArray) = db.delete(prefix + key)
    }

    companion object {
        init {
            RocksDB.loadLibrary()
        }

        fun connect(name: String, namespace: ByteArray? = null): Rocks {
            val opts = Options().apply {
                setCreateIfMissing(true)
                setInfoLogLevel(InfoLogLevel.INFO_LEVEL)
            }
            val db = RocksDB.open(opts, name)

            return when (namespace) {
                null -> KV(db)
                else -> PrefixedKV(db, namespace)
            }
        }
    }
}
