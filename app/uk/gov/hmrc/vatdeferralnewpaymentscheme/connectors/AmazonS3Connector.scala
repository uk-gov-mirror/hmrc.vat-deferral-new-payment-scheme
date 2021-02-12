/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.vatdeferralnewpaymentscheme.connectors

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Framing, Sink, Source}
import akka.util.ByteString
import com.amazonaws.AmazonClientException
import com.amazonaws.services.s3.model.{GetObjectRequest, S3Object}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.gilt.gfc.aws.s3.akka.S3DownloaderSource._
import javax.inject.Inject
import play.api.Logger
import uk.gov.hmrc.vatdeferralnewpaymentscheme.config.AppConfig

import scala.collection.immutable
import scala.concurrent.Future
import scala.io.{BufferedSource, Source => IOSource}

class AmazonS3Connector @Inject()(config: AppConfig)(implicit system: ActorSystem) {

  val logger = Logger(getClass)

  implicit val ec = system.dispatcher
  implicit val materializer = akka.stream.ActorMaterializer()

  private lazy val s3client: AmazonS3 = {
    val builder = AmazonS3ClientBuilder
      .standard()
      .withPathStyleAccessEnabled(true)

    builder.withRegion(config.region)
    builder.build()
  }

  val splitter: Flow[ByteString, ByteString, NotUsed] = Framing.delimiter(
    ByteString("\n"),
    maximumFrameLength = 1024,
    allowTruncation = true
  )

  def getFile(filename: String): Option[S3Object] = {
    try {
      Some(s3client.getObject(new GetObjectRequest(config.bucket, filename)))
    } catch {
      case ex: AmazonClientException =>
        logger.error(s"Couldn't fetch $filename from S3", ex)
        None
    } 
  }

  def processFile[A](
    filename: String,
    func1: PartialFunction[String, A],
    func2: Seq[A] => Future[Unit],
    func3: => Future[Unit]
  ): Future[Unit] = {
    getFile(filename).map { file =>
      val source: BufferedSource = IOSource.fromInputStream(file.getObjectContent)
      source
        .getLines()
        .collect(func1)
        .grouped(10000).map { x =>
        func2(x)
      }
    }.fold(throw new RuntimeException("unable to get file")){ _ => func3}
  }

  def chunkFileDownload[A](
    filename: String,
    func1: PartialFunction[String, A],
    func2: Seq[A] => Future[Unit],
    func3: => Future[Unit]
  ): Future[Unit] = {
    val chunkSize = 1024 * 1024 // 1 Mb chunks to request from S3
    val memoryBufferSize = 128 * 1024 // 128 Kb buffer

    val source = Source.s3ChunkedDownload(
      s3client,
      config.bucket,
      filename,
      chunkSize,
      memoryBufferSize
    ).via(splitter)
      .map(_.utf8String.trim)
      .collect(func1)
      .grouped(10000)

    source.runWith(Sink.foldAsync() {
      case (acc, x) =>
        func2(x)
    }).map {_=>
      func3
    }
  }

  def getObject(filename: String): S3Object = s3client.getObject(config.bucket, filename)

  def exists(filename: String): Boolean = s3client.doesObjectExist(config.bucket, filename)
}