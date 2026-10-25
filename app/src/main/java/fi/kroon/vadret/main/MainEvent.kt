package fi.kroon.vadret.main

internal sealed interface MainEvent {

    data object OnStart : MainEvent
}