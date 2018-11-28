name := """test-service"""

version := "1.0"

scalaVersion := "2.12.2"

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xlint", // Enable recommended additional warnings.
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
  "-language:existentials",
  "-language:higherKinds"
)

val akkaHttpVersion = "10.1.5"
val mockitoVersion = "2.8.47"
val scalatestVersion = "3.0.1"
val serviceChassisVersion = "1.0.4-SNAPSHOT"
val macroParadiseVersion = "2.1.0"
val guavaVersion = "20.0"

addCompilerPlugin("org.scalamacros" % "paradise" % macroParadiseVersion cross CrossVersion.full)


libraryDependencies ++= {

  Seq(
    "allawala" %% "service-chassis" % serviceChassisVersion,
    "com.google.guava" % "guava" % guavaVersion,

    // Test Dependencies
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "it,test",
    "org.mockito" % "mockito-core" % mockitoVersion % "it,test",
    "org.scalatest" %% "scalatest" % scalatestVersion % "it,test"
  )
}

enablePlugins(BuildInfoPlugin, GitVersioning)

// BuildInfo plugin Settings
buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, git.gitCurrentBranch, git.gitHeadCommit)
buildInfoPackage := "allawala"
buildInfoOptions += BuildInfoOption.BuildTime

resolvers += "Service Chassis Snapshots" at "https://s3-ap-southeast-2.amazonaws.com/maven.allawala.com/service-chassis/snapshots"
resolvers += "Service Chassis Releases" at "https://s3-ap-southeast-2.amazonaws.com/maven.allawala.com/service-chassis/releases"

lazy val root = (project in file(".")).configs(IntegrationTest).settings(Defaults.itSettings)

fork in run := true
parallelExecution in Test := false
parallelExecution in IntegrationTest := false