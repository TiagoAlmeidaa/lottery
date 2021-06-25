import org.gradle.api.Project
import org.gradle.api.file.FileTree

object Jacoco {

    private val exclusions = listOf(
        "**/databinding/**/*.*",
        "**/android/databinding/*Binding.*",
        "**/BR.*",
        "**/R.*",
        "**/R$*.*",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*_MembersInjector.*",
        "**/*Module_*Factory.*",
        "**/*Fragment*.*",
        "**/*Activity*.*",
        "**/*Adapter*.*",
        "**/*ViewPager*.*",
        "**/*ViewHolder*.*",
        "**/*Module*.*",
        "**/util/*",
        "**/ui/*",
        "**/LotteryApplication*"
    )

    fun getDestinationPath(project: Project): String =
        "${project.projectDir}/jacoco/reports/html/coverage-report"

    fun getSourceDirectories(project: Project): String =
        "${project.projectDir}/src/main/java"

    fun getClassDirectories(project: Project): FileTree =
        project.fileTree("${project.buildDir}/tmp/kotlin-classes/debug") {
            setExcludes(exclusions)
        }

    fun getExecutionData(project: Project): FileTree =
        project.fileTree(project.buildDir) {
            include(
                "jacoco/testDebugUnitTest.exec",
                "outputs/code-coverage/connected/*coverage.ec"
            )
        }

}