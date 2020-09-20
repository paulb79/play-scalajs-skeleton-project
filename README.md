# Scala Play with ScalaJS and React

**Why:**
 
After reading a lot of documentation from various sources I  found much of the examples either overlapped, was now broken or referred to older versions (particularly ScalaJs). 
I felt that it was harder than it needs to be to find a working example which uses the latest Play, ScalaJS and React libraries to begin a project upon. So I've captured the steps 
I took to put together a barebones project ready for your ideas. 

Steps performed: 

* `sbt new vmunier/play-scalajs.g8`

This task creates a basic Scala Play application.

* Add the following to `plugins.sbt`

```sbt
// Comment to get more information during initialization
logLevel := Level.Warn

// Resolvers
resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

// Sbt plugins

// Use Scala.js v1.x
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.1.1")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.2")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.0.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.2")
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.4")
addSbtPlugin("ch.epfl.scala" % "sbt-web-scalajs-bundler" % "0.18.0")
```

* Now update the `build.sbt` as follows

Define a server target with the following definition: 
```sbt
lazy val server = (project in file("server"))
  .settings(commonSettings)
  .settings(
    scalaJSProjects := Seq(client),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip),
    // triggers scalaJSPipeline when using compile or continuous compilation
    compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
    libraryDependencies ++= Seq(
      "com.vmunier" %% "scalajs-scripts" % "1.1.4",
      guice,
      specs2 % Test
    )
  )
  .enablePlugins(PlayScala)
  .enablePlugins(WebScalaJSBundlerPlugin)
  .dependsOn(sharedJvm)
```

Define a client target with the following definition: 
```sbt
lazy val client = (project in file("client"))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalaJSBundlerPlugin)
  .enablePlugins(JSDependenciesPlugin)
  .settings(commonSettings)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    scalaJSUseMainModuleInitializer in Test := false,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.0.0",
      "com.github.japgolly.scalajs-react" %%% "core" % "1.7.5",
      "com.github.japgolly.scalajs-react" %%% "extra" % "1.7.5",
      "com.github.japgolly.scalacss" %%% "core" % "0.6.1",
      "com.github.japgolly.scalacss" %%% "ext-react" % "0.6.1",
      "com.lihaoyi" %%% "utest" % "0.7.5" % "test"
    ),
    npmDependencies in Compile ++= Seq(
      "react" -> "16.13.1",
      "react-dom" -> "16.13.1"
    ),
    requireJsDomEnv in Test := true
  )
  .dependsOn(sharedJs)
```

Both the server and client can benefit and use a shared target which can be defined as:
```sbt 
lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(commonSettings)
  .jsConfigure(_.enablePlugins(ScalaJSWeb))
lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js
``` 

In order to have the Play application server start as the default sbt project we add the following to `build.sbt`

```sbt
onLoad in Global := (Command
  .process("project server", _: State)) compose (onLoad in Global).value
```

Take a look in `build.sbt` for the complete example

* Launch this project in intellij from the root folder run the command `idea .`

## How to run

1. Check out the project
1. From your terminal `cd` into the root folder
1. Assuming you have `sbt` installed then run `sbt run`
1. After a few moments you should see

```
--- (Running the application, auto-reloading is enabled) ---

[info] p.c.s.AkkaHttpServer - Listening for HTTP on /0:0:0:0:0:0:0:0:9000

(Server started, use Enter to stop and go back to the console...)
```
1. Now in your web browser open a new tab and head to `http://localhost:9000`, it will take a few seconds the first time as Play defers compilation to first invocation and it will need to pull down the npm dependencies.  


## Resources followed

All of these resources are required to setup your new project

* [Create a new Scala Project](https://www.scala-sbt.org/1.x/docs/sbt-new-and-Templates.html)
* [ScalaJs Project Setup](https://www.scala-js.org/doc/project/)
* [Scalajs-react](https://github.com/japgolly/scalajs-react/blob/master/doc/USAGE.md)
* [Configure Intellij](https://github.com/japgolly/scalajs-react/blob/master/doc/IDE.md)
* [scalajs-bundler](https://scalacenter.github.io/scalajs-bundler/)



