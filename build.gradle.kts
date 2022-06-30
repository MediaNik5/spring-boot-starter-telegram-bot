import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    id("org.springframework.boot") version "2.7.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    `maven-publish`
    `java-library`
    signing
}

group = "io.github.medianik5"
version = "0.1"
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
        freeCompilerArgs = listOf("-Xjsr305=strict", "-opt-in=kotlin.RequiresOptIn")
        jvmTarget = "1.8"
    }
}
java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "io.github.medianik5"
            artifactId = "spring-boot-starter-telegram-bot"
            version = "0.1"

            from(components["java"])
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
        }
    }
    repositories {
        maven{
            credentials{
                username = findProperty("user").toString()
                password = findProperty("password").toString()
            }
//            url = if(version.toString().endsWith("SNAPSHOT"))
//                URI("https://s01.oss.sonatype.org/content/repositories/snapshots/")
//            else
            url = URI("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
        }
    }
}

signing{
    sign(publishing.publications["mavenJava"])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
