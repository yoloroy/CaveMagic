package lib.extensions

class ObservableCollection<T>(
    private val wrapped: MutableCollection<T>
) : MutableCollection<T> by wrapped {
    private val observers = mutableListOf<(Collection<T>) -> Unit>()

    override fun add(element: T): Boolean {
        if (wrapped.add(element)) {
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
