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
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            suppressPomMetadataWarningsFor("javadocElements")
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
}

configure<SigningExtension> {
    val key = System.getenv("SIGNING_KEY")
    val password = System.getenv("SIGNING_PASSWORD")
    val publishing: PublishingExtension by project
    useInMemoryPgpKeys(key, password)
    sign(publishing.publications)
}
