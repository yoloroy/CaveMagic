package logic.inventory.item

import com.soywiz.korim.bitmap.Bitmap

abstract class Item(val tileId: Int, val icon: Bitmap, val name: String, val description: String, val action: () -> Unit)
