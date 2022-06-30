import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    `maven-publish`
}

group = "io.github.medianik"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

dependencies {
    val tgbotapi_version = "2.1.0"
    testImplementation(kotlin("test"))
    implementation("dev.inmo:tgbotapi:$tgbotapi_version")
    implementation("dev.inmo:tgbotapi.core:$tgbotapi_version")
    implementation("dev.inmo:tgbotapi.api:$tgbotapi_version")
    implementation("dev.inmo:tgbotapi.utils:$tgbotapi_version")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "io.github.medianik"
            artifactId = "spring-boot-starter-telegram-bot"
            version = "0.1"
            pom {
                name.set("${groupId}:${artifactId}")
                description.set("Spring boot starter for telegram bot, allowing to create telegram bot with spring boot and " +
                        "use Controller-like programming style")
                url.set("https://github.com/MediaNik5/${artifactId}")

                licenses{
                    license{
                        name.set("MIT License")
                        url.set("http://www.opensource.org/licenses/mit-license.php")
                    }
                }
                developers{
                    developer{
                        name.set("Nikita Rostovtsev")
                        email.set("stafilopok@mail.ru")
                    }
                }
                scm{
                    connection.set("scm:git:git://github.com/MediaNik5/${artifactId}.git")
                    developerConnection.set("scm:git:ssh://github.com:MediaNik5/${artifactId}.git")
                    url.set("http://github.com/MediaNik5/${artifactId}/tree/master")
                }
            }

            from(components["java"])
        }
    }
}