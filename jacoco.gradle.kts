tasks.register<JacocoReport>("jacocoTestReport") {
    println("")
    println("> Task :testCoverageReport started")

    group = "verification"
    description = "Code coverage report for both Android and Unit tests."

    val testTask = tasks.getByName("testDebugUnitTest")
    println("> Task :testCoverageReport - dependsOn: $testTask")
    dependsOn(testTask)

    reports {
        with(html) {
            isEnabled = true
            destination = file(Jacoco.getDestinationPath(project))
        }
    }

    val sourceDir = files(Jacoco.getSourceDirectories(project))
    val classDir = files(Jacoco.getClassDirectories(project))
    val execData = Jacoco.getExecutionData(project)

    sourceDirectories.from(sourceDir)
    classDirectories.from(classDir)
    executionData.from(execData)

    println("> Task :testCoverageReport finished")
}
