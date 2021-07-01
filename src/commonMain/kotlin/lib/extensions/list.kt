package lib.extensions

class ObservableCollection<C: MutableCollection<T>, T>(
    val value: C
) : MutableCollection<T> by value {
    private val observers = mutableListOf<(Collection<T>) -> Unit>()

    override fun add(element: T): Boolean {
        if (value.add(element)) {
            onChange()
            return true
        }
        return false
    }

    private fun onChange() = observers.forEach { it(this) }

    fun observe(observerBlock: (Collection<T>) -> Unit) {
        observers += observerBlock
    }
}
