object ProjectVersions {
    const val rlVersion = "4.9.9"
    const val apiVersion = "^1.0.0"
}

object Libraries {
    private object Versions {
        const val apacheCommonsText = "1.8"
        const val guice = "4.2.2"
        const val lombok = "1.18.10"
        const val okhttp3 = "4.2.2"
        const val pf4j = "3.6.0"
        const val rxjava = "3.0.10"
    }

    const val apacheCommonsText = "org.apache.commons:commons-text:${Versions.apacheCommonsText}"
    const val guice = "com.google.inject:guice:${Versions.guice}:no_aop"
    const val lombok = "org.projectlombok:lombok:${Versions.lombok}"
    const val okhttp3 = "com.squareup.okhttp3:okhttp:${Versions.okhttp3}"
    const val pf4j = "org.pf4j:pf4j:${Versions.pf4j}"
    const val rxjava = "io.reactivex.rxjava2:rxjava:${Versions.rxjava}"
}