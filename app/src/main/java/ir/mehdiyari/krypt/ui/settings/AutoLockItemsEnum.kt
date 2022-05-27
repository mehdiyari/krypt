package ir.mehdiyari.krypt.ui.settings

enum class AutoLockItemsEnum(val value: Int) {
    Disabled(-1),
    ThirtySecond(30),
    OneMinute(60),
    TwoMinute(120),
    FiveMinute(300),
    TenMinute(600),
    FifteenMinute(900),
    ThirtyMinute(1800),
    OneHour(3600)
}