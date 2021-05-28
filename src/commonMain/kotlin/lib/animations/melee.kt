package lib.animations

import com.soywiz.klock.seconds
import com.soywiz.korge.animate.animate
import com.soywiz.korge.tween.get
import com.soywiz.korio.async.async
import com.soywiz.korma.geom.Point
import com.soywiz.korma.interpolation.Easing
import lib.extensions.clamp
import logic.gameObjects.gameObject.GameObject

@Suppress("DeferredResultUnused")
suspend fun GameObject.animateMeleeAttackOn(target: GameObject) {
    val savedPos = view.pos.copy()

    async(view.stage!!.coroutineContext) {
        view.animate(time = 0.075.seconds, easing = Easing.EASE_OUT_QUAD) {
            val offset = (target.pos - this@animateMeleeAttackOn.pos).clamp(Point(-1)..Point(+1)) * 2

            tween(view::pos[view.pos + offset], time = time)
            tween(view::pos[savedPos], time = time)
        }
    }
}

suspend fun GameObject.animateHandlingAttack(from: GameObject?) {
    val savedSize = Point(view.unscaledWidth, view.unscaledHeight)
    val savedPos = view.pos.copy()

    view.animate(time = 0.15.seconds, easing = Easing.LINEAR) {
        if (from != null) {
            val offset = (from.pos - this@animateHandlingAttack.pos).clamp(Point(-1)..Point(+1)) * -2

            tween(view::pos[view.pos + offset], time = time)
            tween(view::pos[savedPos], time = time)
        } else {
            tween(view::scale[1.2], view::pos[view.pos - savedSize * 0.06], time = time)
            tween(view::scale[1.0], view::pos[savedPos], time = time)
        }
    }
}
