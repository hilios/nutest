import com.typesafe.config.ConfigFactory

lazy val conf = ConfigFactory.parseFile(new File("./conf/application.conf")).resolve()

name := conf.getString("app.name")

version := conf.getString("app.version")

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  filters,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
