package gps.parser

class SentenceParser(private val recv: () -> Char) {
    fun nextMessage(): Sentence {
        var state: SentenceState = SentenceState.Prefix()
        while (true) {
            state = when (state) {
                is SentenceState.Prefix      -> state.next(recv)
                is SentenceState.Talker      -> state.next(recv)
                is SentenceState.MessageType -> state.next(recv)
                is SentenceState.Fields      -> state.next(recv)
                is SentenceState.Checksum    -> state.next(recv)
                is SentenceState.Complete    -> return state.sentence
            }
        }
    }
}
