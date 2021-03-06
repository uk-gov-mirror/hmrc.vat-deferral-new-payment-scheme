import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-backend-play-27"  % "3.4.0",
    "uk.gov.hmrc"             %% "simple-reactivemongo"       % "7.31.0-play-27",
    "com.amazonaws"           % "aws-java-sdk-s3"             % "1.11.915",
    "uk.gov.hmrc"             %% "stub-data-generator"        % "0.5.3",
    "org.typelevel"           %% "cats-core"                  % "2.1.1",
    "com.gilt"                %% "gfc-aws-s3"                 % "0.1.0",
    "com.chuusai"             %% "shapeless"                  % "2.3.3"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-27"   % "3.0.0" % Test,
    "org.scalatest"           %% "scalatest"                % "3.1.2"                 % Test,
    "com.typesafe.play"       %% "play-test"                % current                 % Test,
    "com.vladsch.flexmark"    %  "flexmark-all"             % "0.35.10"               % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "4.0.3"                 % "test, it",
    "org.scalatestplus"       %% "mockito-3-4"              % "3.2.2.0"               % "test, it"

  )
}
