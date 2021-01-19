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

import com.google.inject.{ImplementedBy, Inject, Singleton}
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats
import uk.gov.hmrc.vatdeferralnewpaymentscheme.model.fileimport.TimeToPay

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[MongoTimeToPayRepo])
trait TimeToPayRepo {
  def addMany(timeToPay: Array[TimeToPay])
  def deleteAll()
  def exists(vrn: String): Future[Boolean]
}

@Singleton
class MongoTimeToPayRepo @Inject() (mongo: ReactiveMongoComponent)(implicit ec: ExecutionContext)
  extends ReactiveRepository[TimeToPay, BSONObjectID] (
    collectionName = "fileImportTimeToPay",
    mongo          = mongo.mongoConnector.db,
    TimeToPay.format,
    ReactiveMongoFormats.objectIdFormats)
  with TimeToPayRepo {

  def addMany(timeToPay: Array[TimeToPay]): Unit = {
    bulkInsert(timeToPay)
  }

  def deleteAll(): Unit ={
    removeAll()
  }

  def exists(vrn: String): Future[Boolean] = {
    find("vrn" -> vrn).map(_.nonEmpty).recover{ case _ ⇒ false }
  }
}