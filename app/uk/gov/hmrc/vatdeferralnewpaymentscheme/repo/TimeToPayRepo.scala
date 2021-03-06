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

package uk.gov.hmrc.vatdeferralnewpaymentscheme.repo

import cats.implicits._
import com.google.inject.{ImplementedBy, Inject, Singleton}
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONObjectID
import reactivemongo.play.json.collection.JSONCollection
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats
import uk.gov.hmrc.vatdeferralnewpaymentscheme.model.fileimport.TimeToPay

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[MongoTimeToPayRepo])
trait TimeToPayRepo extends BaseFileImportRepo {
  def exists(vrn: String): Future[Boolean]
}

@Singleton
class MongoTimeToPayRepo @Inject() (
  reactiveMongoComponent: ReactiveMongoComponent
)(
  implicit ec: ExecutionContext
)
  extends ReactiveRepository[TimeToPay, BSONObjectID] (
    collectionName = "fileImportTimeToPay",
    mongo          = reactiveMongoComponent.mongoConnector.db,
    TimeToPay.format,
    ReactiveMongoFormats.objectIdFormats)
    with TimeToPayRepo {

  val tempCollection: JSONCollection =
    mongo()
      .collection[JSONCollection]("fileImportTimeToPayTemp")

  def renameCollection(): Future[Boolean] = {
    collection.db.connection.database("admin")
      .flatMap { adminDatabase =>
        logger.info(s"File Import: Renaming collection via main database, params: '${collection.db.name}' '${collection.name}' ")
        adminDatabase.renameCollection(collection.db.name, "fileImportTimeToPayTemp", collection.name, true)
      }.map { renameResult: BSONCollection =>
      logger.info(s"File Import: '${collection.name}' collection renamed operation finished, result: ${renameResult}")
      true
    }
  }

  def exists(vrn: String): Future[Boolean] = {
    find("vrn" -> vrn).map(_.nonEmpty).recover{ case _ ??? false }
  }

  override def indexes: Seq[Index] = Seq(
    Index(
      name = "vrnIndex".some,
      key = Seq( "vrn" -> IndexType.Ascending),
      background = true,
      unique = true
    )
  )
}