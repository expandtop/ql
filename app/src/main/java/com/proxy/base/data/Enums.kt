package com.proxy.base.data

/**
 * @date 2024/8/6
 */
class Enums {
    enum class LEVEL(val type: String, val id: Int) {
        FREE("free", 3), VIP("vip", 1), SVIP("svip", 2), NORMAL("normal", 4), ALL("all", 0)
    }

}