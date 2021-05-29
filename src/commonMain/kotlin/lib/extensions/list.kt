package lib.extensions

class ObservableCollection<T>(
    private val wrapped: MutableCollection<T>,
    val onChange: (MutableCollection<T>) -> Unit
) : MutableCollection<T> by wrapped {
    override fun add(element: T): Boolean {
        if (wrapped.add(element)) {
            onChange(this)
            return true
        }
        return false
    }
}
