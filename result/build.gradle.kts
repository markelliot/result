import java.net.URI

plugins {
    `java-library`
    `maven-publish`
    `signing`
}

dependencies {
    testImplementation(platform("org.junit:junit-bom"))
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.assertj:assertj-core")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            pom {
                name.set("result")
                description.set("A Rust-inspired success and error container.")
                url.set("https://github.com/markelliot/result")
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                developers {
                    developer {
                        id.set("markelliot")
                        name.set("Mark Elliot")
                        email.set("markelliot@users.noreply.github.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/markelliot/result.git")
                    developerConnection.set("scm:git:https://github.com/markelliot/result.git")
                    url.set("https://github.com/markelliot/result")
                }
            }
        }
    }

    repositories {
        maven {
            name = "MavenCentral"
            url = URI.create("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                username = System.getenv("MAVEN_CENTRAL_USER")
                password = System.getenv("MAVEN_CENTRAL_PASSWORD")
            }
        }
    }
}

configure<SigningExtension> {
    val key = System.getenv("SIGNING_KEY")
    val password = System.getenv("SIGNING_PASSWORD")
    val publishing: PublishingExtension by project
    useInMemoryPgpKeys(key, password)
    sign(publishing.publications)
}
