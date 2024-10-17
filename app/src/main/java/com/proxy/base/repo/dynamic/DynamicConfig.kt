package com.proxy.base.repo.dynamic


data class DynamicConfig(
    val success: Boolean,
    val new: String,
    val priority: Int = 0, // 越小优先级越高
    val isIP: Boolean = false, // 是否是IP
) {
    companion object {
        fun default(): DynamicConfig {
            return DynamicConfig(false, "")
        }
    }
}