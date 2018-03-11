package kv

interface KeyValueStore {
    /**
     * Set the value for the given key.
     *
     * @param key the specified key to be inserted
     * @param value the value associated with the specified key
     *
     * @throws Exception if the put cannot be completed.
     */
    fun put(key: ByteArray, value: ByteArray)

    /**
     * Set the value for the given key.
     *
     * The given string is assumed to be UTF 8.
     *
     * @param key the specified key to be inserted
     * @param value the value associated with the specified key
     *
     * @throws Exception if the put cannot be completed.
     */
    fun put(key: ByteArray, value: String) = put(key, value.toByteArray(Charsets.UTF_8))

    /**
     * Set the value for the given key.
     *
     * @param key the specified key to be inserted
     * @param value the value associated with the specified key
     *
     * @throws Exception if the put cannot be completed.
     */
    fun put(long: Long, value: ByteArray) = put(Bytes.from(long), value)

    /**
     * Returns a new {@code ByteArray} storing the value associated for the given key.
     *
     * If the given key does not have a value associated with it {@code null} will be
     * returned.
     *
     * @param key the key retrieve the value
     * @returns a {@code ByteArray} storing the value associated for the given key or {@code null}
     *
     * @throws Exception if the get cannot be completed
     */
    fun get(key: ByteArray): ByteArray?

    /**
     * Deletes the entry (if any) for the given key.
     *
     * @param key the key to delete within database
     *
     * @throws Exception if the delete cannot be completed
     */
    fun delete(key: ByteArray)
}
