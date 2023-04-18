import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
}

extra.apply {
    set("PUBLISH_GROUP_ID", "com.toggl")
    set("PUBLISH_VERSION", "0.2.0")
    set("PUBLISH_ARTIFACT_ID", "komposable-architecture")
}

//apply(from = "${rootProject.projectDir}/scripts/publish-module.gradle")

//java {
//    withJavadocJar()
//    withSourcesJar()
//}

kotlin {

    jvm {
        jvmToolchain(11)
        withJava()
    }

    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }

    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlin.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.junit.jupiter.api)
                runtimeOnly(libs.junit.jupiter.engine)
                implementation(libs.mockK)
                implementation(libs.turbine)
//                implementation(libs.kotestMatchers)

                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlin.test.core)
                implementation(libs.kotlin.test.junit5)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
//        val jvmMain by getting
//        val jvmTest by getting
//        val jsMain by getting
//        val jsTest by getting
//        val nativeMain by getting
//        val nativeTest by getting
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs += listOf(
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.FlowPreview"
        )
    }
}
