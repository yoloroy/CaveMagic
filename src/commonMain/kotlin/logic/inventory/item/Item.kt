package logic.inventory.item

import com.soywiz.korim.bitmap.Bitmap

data class Item(val icon: Bitmap, val name: String, val description: String, val action: () -> Unit) {
    fun use() = action()
}
