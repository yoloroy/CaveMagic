package lib.extensions

typealias ObservableMutableList<T> = ObservableCollection<MutableList<T>, T>

class ObservableCollection<C: MutableCollection<T>, T>(
    val value: C
) : MutableCollection<T> by value {
    private val observers = mutableListOf<(Collection<T>) -> Unit>()

    override fun addAll(elements: Collection<T>) = value.addAll(elements).also { if (it) onChange() }

    override fun add(element: T): Boolean = value.add(element).also { if (it) onChange() }

    @Suppress("UNCHECKED_CAST")
    fun removeLastOrNull(): T? = (value as MutableList<T>).removeLastOrNull().also { onChange() }

    override fun clear() = value.clear().also { onChange() }

    private fun onChange() = observers.forEach { it(this) }

    fun observe(observerBlock: (Collection<T>) -> Unit) {
        observers += observerBlock
    }
}
