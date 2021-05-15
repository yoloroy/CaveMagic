package logic.magic

interface Magic

enum class AreaMagic : Magic {
    Square,
    Circle,
    Wave;
}

enum class DamageMagic : Magic {
    Lightning
}
