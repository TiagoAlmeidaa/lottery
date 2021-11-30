package com.tiagoalmeida.lottery.util

object Constants {

    const val TAG_LOG = "LotteryDebug"

    const val ONE_SECOND: Long = 1 * 1000
    const val THREE_SECONDS: Long = 3 * 1000
    const val SEVEN_SECONDS: Long = 7 * 1000
    const val THIRTY_SECONDS: Long = 30 * 1000

    const val REQUEST_CODE_NEW_GAME = 1111
    const val REQUEST_CODE_DETAIL_GAME = 2222

    const val BUNDLE_GAME_JSON = "bundle.games.json"
    const val BUNDLE_GAME_REMOVED_JSON = "bundle.games.removed.json"

    const val SHARED_PREFERENCES_KEY = "shared.preferences.key"
    const val SHARED_PREFERENCES_GAMES = "shared.preferences.games"
    const val SHARED_PREFERENCES_LAST_MEGASENA = "shared.preferences.last.megasena"
    const val SHARED_PREFERENCES_LAST_LOTOFACIL = "shared.preferences.last.lotofacil"
    const val SHARED_PREFERENCES_LAST_LOTOMANIA = "shared.preferences.last.lotomania"
    const val SHARED_PREFERENCES_LAST_QUINA = "shared.preferences.last.quina"
    const val SHARED_PREFERENCES_LAST_TIMEMANIA = "shared.preferences.last.timemania"

    const val BOTTOM_SHEET_FILTER_ID = "bottom.sheet.filter"

    const val NOTIFICATION_CHANNEL_ID = "747"
    const val NOTIFICATION_ID = 1

    const val WORKER_PERIODICITY_ONE_DAY = 24L
    const val WORKER_BACK_OFF_POLICY_THIRTY_MINUTES = 30L

    const val KEYS_FILE_NAME = "native-lib"

}
