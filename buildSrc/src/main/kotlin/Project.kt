fun nameToId(name: String): String {
    return name.replace("[^A-Za-z]".toRegex(), "").toLowerCase() + "-plugin"
}