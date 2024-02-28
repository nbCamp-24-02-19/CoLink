package com.seven.colink.util

class PersonnelUtils {

    companion object {
        fun incrementCount(
            selectedCount: Int,
            totalCount: Int,
            limit: Int
        ): Pair<Int, Int> {
            return if (totalCount < limit) {
                Pair(selectedCount + 1, totalCount + 1)
            } else {
                Pair(selectedCount, totalCount)
            }
        }

        fun decrementCount(
            selectedCount: Int,
            totalCount: Int
        ): Pair<Int, Int> {
            return if (selectedCount > 0) {
                Pair(selectedCount - 1, totalCount - 1)
            } else {
                Pair(selectedCount, totalCount)
            }
        }
    }
}
