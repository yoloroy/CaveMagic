package logic.gameObjects.logic

interface MessageHandler {
    val messages: MutableList<Int>

    fun sendMessage(message: Int) = messages.add(message)
}